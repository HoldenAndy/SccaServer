package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.LogDTO;
import com.proyecto.scca.model.dto.LogRequest;

import java.util.List;

public interface LogService {
    public LogDTO registrarLog(LogRequest req);
    public List<LogDTO> obtenerLogsRecientes();
}
