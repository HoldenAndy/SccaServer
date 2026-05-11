package com.proyecto.scca.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImagenRequest(@NotNull Integer idLectura, @NotBlank String rutaArchivo, @NotNull Double pesoKb) {}