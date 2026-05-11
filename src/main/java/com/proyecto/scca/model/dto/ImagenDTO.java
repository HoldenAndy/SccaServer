package com.proyecto.scca.model.dto;

import java.time.LocalDateTime;

public record ImagenDTO(Integer idImagen, Integer idLectura, String rutaArchivo, Double pesoKb, LocalDateTime fechaHora) {}