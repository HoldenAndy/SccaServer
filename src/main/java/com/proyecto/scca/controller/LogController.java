package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.LogDTO;
import com.proyecto.scca.model.dto.LogRequest;
import com.proyecto.scca.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
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