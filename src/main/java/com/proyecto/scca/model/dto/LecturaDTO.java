package com.proyecto.scca.model.dto;

import java.time.LocalDateTime;

public record LecturaDTO(
        Integer idLectura,
        Integer idNodo,
        Double ph,
        Double temperatura,
        Double turbidez,
        Double tds,
        LocalDateTime fechaHora) {}