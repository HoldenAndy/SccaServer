package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.*;
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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public List<UsuarioDTO> getUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        log.info("Solicitud para crear usuario");
        return usuarioService.crearUsuario(request);
    }
}