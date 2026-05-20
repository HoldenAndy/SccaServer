package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.LogSistema;
import com.proyecto.scca.repository.LogRepository;
import com.proyecto.scca.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    private LogDTO mapToDTO(LogSistema l) {
        return new LogDTO(l.getIdLog(), l.getNivel(), l.getModulo(), l.getMensaje(), l.getFechaHora());
    }

    @Override
    @Transactional
    public LogDTO registrarLog(LogRequest req) {
        log.info("[SISTEMA] Nuevo evento en módulo {}: {}", req.modulo(), req.mensaje());
        LogSistema logEntidad = LogSistema.builder()
                .nivel(req.nivel().toUpperCase())
                .modulo(req.modulo())
                .mensaje(req.mensaje())
                .fechaHora(LocalDateTime.now())
                .build();
        return mapToDTO(logRepository.save(logEntidad));
    }

    @Override
    public List<LogDTO> obtenerLogsRecientes() {
        return logRepository.findTop100ByOrderByFechaHoraDesc()
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LogDTO> buscarLogsPaginados(String nivel, String modulo, LocalDateTime inicio, LocalDateTime fin, int page, int size) {
        log.info("Auditoría: Búsqueda avanzada de logs - Nivel: {}, Módulo: {}, Rango: [{} - {}], Página: {}",
                nivel, modulo, inicio, fin, page);

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());

        String nivelFiltro = (nivel != null && !nivel.isBlank()) ? nivel.toUpperCase() : null;
        String moduloFiltro = (modulo != null && !modulo.isBlank()) ? modulo : null;

        Page<LogSistema> paginaResult = logRepository.buscarLogsConFiltros(
                nivelFiltro, moduloFiltro, inicio, fin, pageable);

        List<LogDTO> contenido = paginaResult.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        return new PageResponse<>(
                contenido,
                paginaResult.getNumber(),
                paginaResult.getSize(),
                paginaResult.getTotalElements(),
                paginaResult.getTotalPages(),
                paginaResult.isLast()
        );
    }
}