package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.NodoEsp32;
import com.proyecto.scca.model.entity.Usuario;
import com.proyecto.scca.repository.NodoRepository;
import com.proyecto.scca.service.NodoService;
import com.proyecto.scca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodoServiceImpl implements NodoService {

    private final NodoRepository nodoRepository;
    private final UsuarioService usuarioService;

    private NodoDTO mapToDTO(NodoEsp32 n) {
        return new NodoDTO(n.getIdNodo(), n.getMacAddress(), n.getUbicacion(), n.getEstadoConexion(), n.getUltimaLectura(), n.getActivo());
    }

    @Override
    public List<NodoDTO> listarNodos() {
        log.debug("Listando todos los nodos registrados.");
        return nodoRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public NodoDTO registrarNodo(NodoRequest request) {
        log.info("Registrando nuevo nodo con MAC: {}", request.macAddress());
        nodoRepository.findByMacAddress(request.macAddress()).ifPresent(n -> {
            throw new IllegalArgumentException("Ya existe un nodo registrado con la MAC: " + request.macAddress());
        });

        Usuario cliente = usuarioService.getEntidadPorId(request.idUsuario());

        NodoEsp32 nodo = NodoEsp32.builder()
                .macAddress(request.macAddress())
                .ubicacion(request.ubicacion())
                .estadoConexion(true)
                .cliente(cliente)
                .activo(true)
                .build();

        return mapToDTO(nodoRepository.save(nodo));
    }

    @Override
    @Transactional
    public NodoDTO actualizarUbicacion(Integer id, NodoUpdateRequest req) {
        log.info("Actualizando localización física del nodo ID: {}", id);
        NodoEsp32 nodo = getEntidadPorId(id);
        nodo.setUbicacion(req.ubicacion());
        return mapToDTO(nodoRepository.save(nodo));
    }

    @Override
    @Transactional
    public NodoDTO transferirPropietario(Integer id, Integer idNuevoUsuario) {
        log.info("Transfiriendo nodo ID: {} al nuevo usuario ID: {}", id, idNuevoUsuario);
        NodoEsp32 nodo = getEntidadPorId(id);
        Usuario nuevoCliente = usuarioService.getEntidadPorId(idNuevoUsuario);
        nodo.setCliente(nuevoCliente);
        return mapToDTO(nodoRepository.save(nodo));
    }

    @Override
    @Transactional
    public void activarNodo(Integer id) {
        log.info("Reactivando el nodo de hardware ID: {}", id);
        NodoEsp32 nodo = getEntidadPorId(id);
        nodo.setActivo(true);
        nodo.setEstadoConexion(false);
        nodoRepository.save(nodo);
    }

    @Override
    @Transactional
    public void desactivarNodo(Integer id) {
        log.warn("Dando de baja el nodo de hardware ID: {}", id);
        NodoEsp32 nodo = getEntidadPorId(id);
        nodo.setActivo(false);
        nodo.setEstadoConexion(false);
        nodoRepository.save(nodo);
    }

    @Override
    public NodoEsp32 getEntidadPorId(Integer id) {
        return nodoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Búsqueda fallida: Nodo con ID {} no existe.", id);
                return new ResourceNotFoundException("Nodo no encontrado con ID: " + id);
        });
    }

    @Override
    public List<NodoDTO> listarNodosPorUsuario(Integer idUsuario) {
        log.debug("Listando nodos para el usuario ID: {}", idUsuario);
        return nodoRepository.findByCliente_IdUsuario(idUsuario)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NodoDTO> listarNodosPaginados(Boolean activo, int page, int size) {
        log.info("Consulta paginada de hardware. Filtro activo: {}, Página: {}, Tamaño: {}", activo, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("idNodo").ascending());
        Page<NodoEsp32> paginaResult;

        if (activo != null) {
            paginaResult = nodoRepository.findByActivo(activo, pageable);
        } else {
            paginaResult = nodoRepository.findAll(pageable);
        }

        List<NodoDTO> contenido = paginaResult.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        return new PageResponse<>(
                contenido,
                paginaResult.getNumber(),
                paginaResult.getSize(),
                paginaResult.getTotalElements(),
                paginaResult.getTotalPages(),
                paginaResult.isLast()
        );
    }
}