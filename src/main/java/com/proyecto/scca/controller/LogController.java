package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.LogDTO;
import com.proyecto.scca.model.dto.LogRequest;
import com.proyecto.scca.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_SOPORTE')")
public class LogController {
    private final LogService logService;

    @GetMapping
    public List<LogDTO> getLogs() { return logService.obtenerLogsRecientes(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogDTO registrarLog(@Valid @RequestBody LogRequest request) {
        return logService.registrarLog(request);
    }
}