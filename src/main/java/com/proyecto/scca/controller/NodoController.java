package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.NodoDTO;
import com.proyecto.scca.model.dto.NodoRequest;
import com.proyecto.scca.service.NodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/nodos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NodoController {

    private final NodoService nodoService;

    @GetMapping
    public List<NodoDTO> getNodos() {
        return nodoService.listarNodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodoDTO registrarNodo(@Valid @RequestBody NodoRequest request) {
        log.info("API REST: Solicitud para registrar nuevo nodo");
        return nodoService.registrarNodo(request);
    }
}