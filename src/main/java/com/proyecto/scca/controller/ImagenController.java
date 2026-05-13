package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.ImagenDTO;
import com.proyecto.scca.model.dto.ImagenRequest;
import com.proyecto.scca.service.ImagenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @Value("${scca.hardware.api-key}")
    private String hardwareApiKeySecreta;

    @PostMapping("/hw/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public ImagenDTO registrarImagenHardware(
            @RequestHeader("X-Hardware-Api-Key") String apiKeyRecibida,
            @Valid @RequestBody ImagenRequest request) {

        if (!hardwareApiKeySecreta.equals(apiKeyRecibida)) {
            throw new RuntimeException("Acceso denegado: API Key de hardware inválida");
        }

        log.info("API REST: Solicitud validada para registrar metadata de imagen de lectura {}", request.idLectura());
        return imagenService.registrarMetadataImagen(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImagenDTO registrarImagen(@Valid @RequestBody ImagenRequest request) {
        log.info("Solicitud para registrar metadata de imagen de lectura {}", request.idLectura());
        return imagenService.registrarMetadataImagen(request);
    }

    @GetMapping("/lectura/{idLectura}")
    public ImagenDTO getImagenPorLectura(@PathVariable Integer idLectura) {
        return imagenService.obtenerPorLectura(idLectura);
    }

    @GetMapping
    public List<ImagenDTO> getAllImagenes() {
        return imagenService.listarImagenes();
    }
}