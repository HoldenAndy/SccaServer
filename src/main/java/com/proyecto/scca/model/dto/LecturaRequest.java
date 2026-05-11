package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LecturaRequest(
        @NotNull(message = "El ID del nodo es obligatorio") Integer idNodo,
        @NotNull @DecimalMin("0.0") @DecimalMax("14.0") Double ph,
        @NotNull @DecimalMin("-50.0") @DecimalMax("100.0") Double temperatura,
        @NotNull @DecimalMin("0.0") Double turbidez,
        @NotNull @DecimalMin("0.0") Double tds
) {}