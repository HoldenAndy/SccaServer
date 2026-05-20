package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.NodoDTO;
import com.proyecto.scca.model.dto.NodoRequest;
import com.proyecto.scca.model.dto.NodoUpdateRequest;
import com.proyecto.scca.model.dto.PageResponse;
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
@RequiredArgsConstructor
public class NodoController {

    private final NodoService nodoService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE', 'ROLE_GESTIONADOR')")
    @GetMapping
    public PageResponse<NodoDTO> getNodos(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return nodoService.listarNodosPaginados(activo, page, size);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodoDTO registrarNodo(@Valid @RequestBody NodoRequest request) {
        log.info("Solicitud para registrar nuevo nodo");
        return nodoService.registrarNodo(request);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
    @PutMapping("/{id}")
    public NodoDTO actualizarUbicacion(@PathVariable Integer id, @Valid @RequestBody NodoUpdateRequest request) {
        return nodoService.actualizarUbicacion(id, request);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @PatchMapping("/{id}/propietario")
    public NodoDTO transferirPropietario(@PathVariable Integer id, @RequestParam Integer idNuevoUsuario) {
        return nodoService.transferirPropietario(id, idNuevoUsuario);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivarNodo(@PathVariable Integer id) {
        nodoService.desactivarNodo(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
    @PatchMapping("/{id}/activar")
    @ResponseStatus(HttpStatus.OK)
    public void activarNodo(@PathVariable Integer id) {
        log.info("Solicitud de reactivación para nodo ID: {}", id);
        nodoService.activarNodo(id);
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