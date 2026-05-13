package com.proyecto.scca.model.dto;

import com.proyecto.scca.model.entity.RolUsuario;

public record UsuarioDTO(
        Integer idUsuario,
        String nombre,
        String email,
        RolUsuario rol
) {}