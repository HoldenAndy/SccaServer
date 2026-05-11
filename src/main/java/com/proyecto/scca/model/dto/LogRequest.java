package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LogRequest(@NotBlank String nivel, @NotBlank String modulo, @NotBlank String mensaje) {}