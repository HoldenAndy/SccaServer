package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.RolUsuario;
import com.proyecto.scca.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public PageResponse<UsuarioDTO> getUsuarios(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return usuarioService.listarUsuariosPaginados(activo, page, size);
    }


    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para crear usuario");
        return usuarioService.crearUsuario(request);
    }


    @PutMapping("/usuarios/{id}")
    public UsuarioDTO actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest request) {
        return usuarioService.actualizarUsuario(id, request);
    }

    @PatchMapping("/usuarios/{id}/rol")
    public UsuarioDTO cambiarRol(@PathVariable Integer id, @RequestParam RolUsuario rol) {
        return usuarioService.cambiarRol(id, rol);
    }

    @PatchMapping("/usuarios/{id}/activar")
    @ResponseStatus(HttpStatus.OK)
    public void activarUsuario(@PathVariable Integer id) {
        log.info("Solicitud de reactivación para usuario ID: {}", id);
        usuarioService.activarUsuario(id);
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivarUsuario(@PathVariable Integer id) {
        usuarioService.desactivarUsuario(id);
    }
}
