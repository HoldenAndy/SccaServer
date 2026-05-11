package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.AnalisisDTO;
import com.proyecto.scca.model.dto.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalisisIaService {
    AnalisisDTO generarAnalisisReal(Integer idLectura);
    List<AnalisisDTO> obtenerHistorial();
    PageResponse<AnalisisDTO> obtenerAnalisisPaginados(Integer idNodo, LocalDateTime inicio, LocalDateTime fin, int page, int size);
    AnalisisDTO obtenerPorLectura(Integer idLectura);
}
