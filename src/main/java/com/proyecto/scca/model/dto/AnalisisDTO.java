package com.proyecto.scca.model.dto;

import java.time.LocalDateTime;

public record AnalisisDTO(Integer idAnalisis, Integer idLectura, String resultadoTexto, String promptUtilizado, Integer tiempoResMs, LocalDateTime fechaHora) {}