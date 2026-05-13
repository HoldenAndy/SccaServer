package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.AnalisisDTO;
import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.service.AnalisisIaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analisis")
@RequiredArgsConstructor
public class AnalisisIaController {

    private final AnalisisIaService analisisService;

    @PostMapping("/generar/{idLectura}")
    @ResponseStatus(HttpStatus.CREATED)
    public AnalisisDTO generarAnalisisIA(@PathVariable Integer idLectura) {
        log.info("Solicitud para generar análisis IA de lectura {}", idLectura);
        return analisisService.generarAnalisisReal(idLectura);
    }

    @GetMapping("/nodo/{idNodo}/paginado")
    public PageResponse<AnalisisDTO> getAnalisisPaginados(
            @PathVariable Integer idNodo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return analisisService.obtenerAnalisisPaginados(idNodo, inicio, fin, page, size);
    }

    @GetMapping("/lectura/{idLectura}")
    public AnalisisDTO getAnalisisPorLectura(@PathVariable Integer idLectura) {
        return analisisService.obtenerPorLectura(idLectura);
    }

    @GetMapping("/historial")
    public List<AnalisisDTO> getHistorialGeneral() {
        return analisisService.obtenerHistorial();
    }
}