package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.NodoDTO;
import com.proyecto.scca.model.dto.NodoRequest;
import com.proyecto.scca.model.entity.NodoEsp32;

import java.util.List;

public interface NodoService {
    List<NodoDTO> listarNodos();
    NodoDTO registrarNodo(NodoRequest req);
    NodoEsp32 getEntidadPorId(Integer id);
}
