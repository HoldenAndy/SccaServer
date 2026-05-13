package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.LecturaDTO;
import com.proyecto.scca.model.dto.LecturaRequest;
import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.service.LecturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/lecturas")
@RequiredArgsConstructor
public class LecturaController {

    private final LecturaService lecturaService;

    @Value("${scca.hardware.api-key}")
    private String hardwareApiKeySecreta;

    @PostMapping("/hw/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public LecturaDTO registrarLecturaHardware(
            @RequestHeader("X-Hardware-Api-Key") String apiKeyRecibida,
            @Valid @RequestBody LecturaRequest request) {

        if (!hardwareApiKeySecreta.equals(apiKeyRecibida)) {
            log.warn("Intento de acceso denegado al hardware. Clave incorrecta.");
            throw new RuntimeException("Acceso denegado: API Key inválida");
        }

        log.info("Hardware ESP32 validado. Registrando lectura para el nodo {}", request.idNodo());
        return lecturaService.registrarLectura(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LecturaDTO registrarLectura(@Valid @RequestBody LecturaRequest request) {
        log.info("Solicitud para registrar lectura del nodo {}", request.idNodo());
        return lecturaService.registrarLectura(request);
    }

    @GetMapping("/nodo/{idNodo}/ultima")
    public LecturaDTO getUltimaLectura(@PathVariable Integer idNodo) {
        return lecturaService.obtenerUltimaLectura(idNodo);
    }

    @GetMapping("/nodo/{idNodo}/paginado")
    public PageResponse<LecturaDTO> getHistorialPaginado(
            @PathVariable Integer idNodo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "fechaHora") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return lecturaService.obtenerHistorialPaginado(idNodo, inicio, fin, page, size, sortBy, sortDir);
    }

    @GetMapping("/nodo/{idNodo}/graficos")
    public List<LecturaDTO> getDatosGraficos(
            @PathVariable Integer idNodo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return lecturaService.obtenerDatosParaGraficos(idNodo, inicio, fin);
    }

    @GetMapping("/nodo/{idNodo}/historial-completo")
    public List<LecturaDTO> getHistorialCompleto(@PathVariable Integer idNodo) {
        return lecturaService.obtenerHistorialNodo(idNodo);
    }
}