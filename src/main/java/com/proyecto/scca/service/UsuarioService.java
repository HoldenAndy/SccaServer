package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.model.dto.UsuarioDTO;
import com.proyecto.scca.model.dto.UsuarioRequest;
import com.proyecto.scca.model.dto.UsuarioUpdateRequest;
import com.proyecto.scca.model.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> listarUsuarios();
    PageResponse<UsuarioDTO> listarUsuariosPaginados(Boolean activo, int page, int size);
    UsuarioDTO crearUsuario(UsuarioRequest req);
    Usuario getEntidadPorId(Integer id);
    UsuarioDTO actualizarUsuario(Integer id, UsuarioUpdateRequest req);
    UsuarioDTO cambiarRol(Integer id, com.proyecto.scca.model.entity.RolUsuario nuevoRol);
    void activarUsuario(Integer id);
    void desactivarUsuario(Integer id);
}