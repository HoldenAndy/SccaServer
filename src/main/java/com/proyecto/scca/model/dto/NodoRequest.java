package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;

public record NodoRequest(@NotBlank String macAddress, @NotBlank String ubicacion) {}