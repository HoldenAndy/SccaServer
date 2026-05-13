package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;
public record CambiarPasswordRequest(
        @NotBlank String newPassword
) {}