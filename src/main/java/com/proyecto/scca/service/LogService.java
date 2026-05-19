package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.LogDTO;
import com.proyecto.scca.model.dto.LogRequest;
import com.proyecto.scca.model.dto.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface LogService {
    public LogDTO registrarLog(LogRequest req);
    public List<LogDTO> obtenerLogsRecientes();
    PageResponse<LogDTO> buscarLogsPaginados(String nivel, String modulo, LocalDateTime inicio, LocalDateTime fin, int page, int size);
}
