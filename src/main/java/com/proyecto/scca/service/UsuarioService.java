package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.UsuarioDTO;
import com.proyecto.scca.model.dto.UsuarioRequest;
import com.proyecto.scca.model.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> listarUsuarios();
    UsuarioDTO crearUsuario(UsuarioRequest req);
    Usuario getEntidadPorId(Integer id);
}