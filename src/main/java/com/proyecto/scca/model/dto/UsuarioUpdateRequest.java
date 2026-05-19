package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequest(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @Email(message = "Debe ser un correo válido")
        @NotBlank(message = "El email no puede estar vacío")
        String email
) {}