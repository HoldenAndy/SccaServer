package com.proyecto.scca.model.dto;

import java.time.LocalDateTime;

public record NodoDTO(
        Integer idNodo,
        String macAddress,
        String ubicacion,
        Boolean estadoConexion,
        LocalDateTime ultimaLectura
) {}