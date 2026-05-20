package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.LecturaSensor;
import com.proyecto.scca.model.entity.NodoEsp32;
import com.proyecto.scca.model.entity.RolUsuario;
import com.proyecto.scca.repository.LecturaRepository;
import com.proyecto.scca.repository.NodoRepository;
import com.proyecto.scca.security.UserDetailsImpl;
import com.proyecto.scca.service.LecturaService;
import com.proyecto.scca.service.NodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LecturaServiceImpl implements LecturaService {

    private final LecturaRepository lecturaRepository;
    private final NodoService nodoService;
    private final NodoRepository nodoRepository;

    private LecturaDTO mapToDTO(LecturaSensor l) {
        return new LecturaDTO(l.getIdLectura(), l.getNodo().getIdNodo(), l.getPh(), l.getTemperatura(), l.getTurbidez(), l.getTds(), l.getFechaHora());
    }

    @Transactional
    public LecturaDTO registrarLectura(LecturaRequest req) {
        log.info("Recibiendo lectura del Nodo ID: {}", req.idNodo());

        NodoEsp32 nodo = nodoService.getEntidadPorId(req.idNodo());

        LecturaSensor lectura = LecturaSensor.builder()
                .nodo(nodo)
                .ph(req.ph())
                .temperatura(req.temperatura())
                .turbidez(req.turbidez())
                .tds(req.tds())
                .fechaHora(LocalDateTime.now())
                .build();

        nodo.setUltimaLectura(lectura.getFechaHora());
        nodo.setEstadoConexion(true);
        nodoRepository.save(nodo);

        log.debug("Datos guardados: pH={}, Temp={}°C", req.ph(), req.temperatura());
        return mapToDTO(lecturaRepository.save(lectura));
    }

    private void validarPropiedadDelNodo(NodoEsp32 nodo) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String rol = userDetails.getUsuario().getRol().name();
        if (rol.equals(RolUsuario.ADMINISTRADOR.name())
                || rol.equals(RolUsuario.SOPORTE.name())
                || rol.equals(RolUsuario.GESTIONADOR.name())) {
            return;
        }

        Integer idUsuarioAutenticado = userDetails.getUsuario().getIdUsuario();
        if (!nodo.getCliente().getIdUsuario().equals(idUsuarioAutenticado)) {
            log.warn("Intento de acceso no autorizado. Usuario {} intentó ver el Nodo {}", idUsuarioAutenticado, nodo.getIdNodo());
            throw new AccessDeniedException("No tienes permiso para ver los datos de este hardware.");
        }
    }

    public LecturaDTO obtenerUltimaLectura(Integer idNodo) {
        NodoEsp32 nodo = nodoService.getEntidadPorId(idNodo);
        validarPropiedadDelNodo(nodo);
        return lecturaRepository.findTop1ByNodo_IdNodoOrderByFechaHoraDesc(idNodo)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No hay lecturas registradas para el nodo: " + idNodo));
    }

    public List<LecturaDTO> obtenerHistorialNodo(Integer idNodo) {
        NodoEsp32 nodo = nodoService.getEntidadPorId(idNodo);
        validarPropiedadDelNodo(nodo);
        log.debug("Consultando historial completo (Top 100) para el nodo: {}", idNodo);
        return lecturaRepository.findTop100ByNodo_IdNodoOrderByFechaHoraDesc(idNodo)
                .stream().map(this::mapToDTO).toList();
    }

    public PageResponse<LecturaDTO> obtenerHistorialPaginado(
            Integer idNodo, LocalDateTime inicio, LocalDateTime fin, int page, int size, String sortBy, String sortDir) {

        NodoEsp32 nodo = nodoService.getEntidadPorId(idNodo);
        validarPropiedadDelNodo(nodo);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LecturaSensor> pagina = lecturaRepository.findByNodo_IdNodoAndFechaHoraBetween(idNodo, inicio, fin, pageable);
        List<LecturaDTO> contenido = pagina.getContent().stream().map(this::mapToDTO).toList();

        return new PageResponse<>(
                contenido, pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages(), pagina.isLast()
        );
    }

    public List<LecturaDTO> obtenerDatosParaGraficos(Integer idNodo, LocalDateTime inicio, LocalDateTime fin) {
        NodoEsp32 nodo = nodoService.getEntidadPorId(idNodo);
        validarPropiedadDelNodo(nodo);

        return lecturaRepository.findByNodo_IdNodoAndFechaHoraBetweenOrderByFechaHoraAsc(idNodo, inicio, fin)
                .stream().map(this::mapToDTO).toList();
    }

    public LecturaSensor getEntidadPorId(Integer id) {
        return lecturaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Lectura no encontrada con ID: {}", id);
                    return new ResourceNotFoundException("Lectura no encontrada con ID: " + id);
                });
    }
}
