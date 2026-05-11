package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.NodoEsp32;
import com.proyecto.scca.repository.NodoRepository;
import com.proyecto.scca.service.NodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodoServiceImpl implements NodoService {

    private final NodoRepository nodoRepository;

    private NodoDTO mapToDTO(NodoEsp32 n) {
        return new NodoDTO(n.getIdNodo(), n.getMacAddress(), n.getUbicacion(), n.getEstadoConexion(), n.getUltimaLectura());
    }

    @Override
    public List<NodoDTO> listarNodos() {
        log.debug("Listando todos los nodos registrados.");
        return nodoRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public NodoDTO registrarNodo(NodoRequest req) {
        log.info("Registrando nuevo nodo con MAC: {}", req.macAddress());
        nodoRepository.findByMacAddress(req.macAddress()).ifPresent(n -> {
            throw new IllegalArgumentException("Ya existe un nodo registrado con la MAC: " + req.macAddress());
        });

        NodoEsp32 nodo = NodoEsp32.builder()
                .macAddress(req.macAddress())
                .ubicacion(req.ubicacion())
                .estadoConexion(true)
                .build();
        return mapToDTO(nodoRepository.save(nodo));
    }

    @Override
    public NodoEsp32 getEntidadPorId(Integer id) {
        return nodoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Búsqueda fallida: Nodo con ID {} no existe.", id);
                    return new ResourceNotFoundException("Nodo no encontrado con ID: " + id);
                });
    }
}