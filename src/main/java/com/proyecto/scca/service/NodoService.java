package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.NodoDTO;
import com.proyecto.scca.model.dto.NodoRequest;
import com.proyecto.scca.model.dto.NodoUpdateRequest;
import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.model.entity.NodoEsp32;

import java.util.List;

public interface NodoService {
    List<NodoDTO> listarNodos();
    PageResponse<NodoDTO> listarNodosPaginados(Boolean activo, int page, int size);
    NodoDTO registrarNodo(NodoRequest req);
    NodoDTO actualizarUbicacion(Integer id, NodoUpdateRequest req);
    NodoDTO transferirPropietario(Integer id, Integer idNuevoUsuario);
    void activarNodo(Integer id);
    void desactivarNodo(Integer id);
    NodoEsp32 getEntidadPorId(Integer id);
    List<NodoDTO> listarNodosPorUsuario(Integer idUsuario);
}
