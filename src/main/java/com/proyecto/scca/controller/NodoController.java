package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.NodoDTO;
import com.proyecto.scca.model.dto.NodoRequest;
import com.proyecto.scca.security.UserDetailsImpl;
import com.proyecto.scca.service.NodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/nodos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NodoController {

    private final NodoService nodoService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE', 'ROLE_GESTIONADOR')")
    @GetMapping
    public List<NodoDTO> getNodos() {
        return nodoService.listarNodos();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodoDTO registrarNodo(@Valid @RequestBody NodoRequest request) {
        log.info("Solicitud para registrar nuevo nodo");
        return nodoService.registrarNodo(request);
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/mis-nodos")
    public List<NodoDTO> getMisNodos(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Integer idUsuario = userDetails.getUsuario().getIdUsuario();
        log.info("El cliente {} está solicitando sus nodos", userDetails.getUsername());
        return nodoService.listarNodosPorUsuario(idUsuario);
    }
}