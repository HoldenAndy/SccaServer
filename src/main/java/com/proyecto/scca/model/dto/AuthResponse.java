package com.proyecto.scca.model.dto;

import com.proyecto.scca.model.entity.RolUsuario;

public record AuthResponse(
        String token,
        String nombre,
        RolUsuario rol,
        Boolean debeCambiarPassword) {}