package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.LecturaDTO;
import com.proyecto.scca.model.dto.LecturaRequest;
import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.model.entity.LecturaSensor;

import java.time.LocalDateTime;
import java.util.List;

public interface LecturaService {
    LecturaDTO registrarLectura(LecturaRequest req);
    LecturaDTO obtenerUltimaLectura(Integer idNodo);
    List<LecturaDTO> obtenerHistorialNodo(Integer idNodo);
    PageResponse<LecturaDTO> obtenerHistorialPaginado(Integer idNodo, LocalDateTime inicio, LocalDateTime fin, int page, int size, String sortBy, String sortDir);
    List<LecturaDTO> obtenerDatosParaGraficos(Integer idNodo, LocalDateTime inicio, LocalDateTime fin);
    LecturaSensor getEntidadPorId(Integer id);
}
