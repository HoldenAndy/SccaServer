package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.ImagenAgua;
import com.proyecto.scca.model.entity.LecturaSensor;
import com.proyecto.scca.model.entity.NodoEsp32;
import com.proyecto.scca.model.entity.RolUsuario;
import com.proyecto.scca.repository.ImagenRepository;
import com.proyecto.scca.security.UserDetailsImpl;
import com.proyecto.scca.service.ImagenService;
import com.proyecto.scca.service.LecturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagenServiceImpl implements ImagenService {

    private final ImagenRepository imagenRepository;
    private final LecturaService lecturaService;

    private ImagenDTO mapToDTO(ImagenAgua i) {
        return new ImagenDTO(i.getIdImagen(), i.getLectura().getIdLectura(), i.getRutaArchivo(), i.getPesoKb(), i.getFechaHora());
    }

    private void validarPropiedadDelNodo(NodoEsp32 nodo) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("No se pudo validar la identidad del usuario.");
        }

        String rolActual = userDetails.getUsuario().getRol().name();
        if (rolActual.equals(RolUsuario.ADMINISTRADOR.name()) || rolActual.equals(RolUsuario.SOPORTE.name()) || rolActual.equals(RolUsuario.GESTIONADOR.name())) {
            return;
        }

        Integer idUsuarioAutenticado = userDetails.getUsuario().getIdUsuario();
        Integer idDueñoDelNodo = nodo.getCliente().getIdUsuario();

        if (!idDueñoDelNodo.equals(idUsuarioAutenticado)) {
            log.warn("BLOQUEO MULTITENENCIA: Usuario {} intentó ver la imagen de agua del Nodo {}",
                    idUsuarioAutenticado, nodo.getIdNodo());
            throw new AccessDeniedException("Acceso denegado: Esta imagen no pertenece a su hardware.");
        }
    }

    @Override
    @Transactional
    public ImagenDTO registrarMetadataImagen(ImagenRequest req) {
        log.info("Vinculando imagen a la lectura ID: {}", req.idLectura());
        LecturaSensor lectura = lecturaService.getEntidadPorId(req.idLectura());

        ImagenAgua imagen = ImagenAgua.builder()
                .lectura(lectura)
                .rutaArchivo(req.rutaArchivo())
                .pesoKb(req.pesoKb())
                .fechaHora(LocalDateTime.now())
                .build();

        return mapToDTO(imagenRepository.save(imagen));
    }

    @Override
    public List<ImagenDTO> listarImagenes() {
        return imagenRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public ImagenDTO obtenerPorLectura(Integer idLectura) {
        LecturaSensor lectura = lecturaService.getEntidadPorId(idLectura);
        validarPropiedadDelNodo(lectura.getNodo());
        return imagenRepository.findByLectura_IdLectura(idLectura)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró imagen para la lectura: " + idLectura));
    }
}