package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;

public record NodoUpdateRequest(
        @NotBlank(message = "La ubicación es obligatoria")
        String ubicacion
) {}