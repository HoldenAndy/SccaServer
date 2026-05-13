package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NodoRequest(
        @NotBlank(message = "La dirección MAC es obligatoria")
        String macAddress,

        @NotBlank(message = "La ubicación es obligatoria")
        String ubicacion,

        @NotNull(message = "El ID del usuario dueño es obligatorio")
        Integer idUsuario
) {}