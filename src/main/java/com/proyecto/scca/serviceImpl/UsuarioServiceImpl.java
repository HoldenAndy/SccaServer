package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.Usuario;
import com.proyecto.scca.repository.UsuarioRepository;
import com.proyecto.scca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private UsuarioDTO mapToDTO(Usuario u) {
        return new UsuarioDTO(u.getIdUsuario(), u.getNombre(), u.getEmail(), u.getRol(), u.getActivo());
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioRequest req) {
        log.info("Creando nuevo usuario por Admin: {}", req.email());
        usuarioRepository.findByEmail(req.email()).ifPresent(u -> {
            throw new IllegalArgumentException("El email ya está registrado.");
        });

        Usuario usuario = Usuario.builder()
                .nombre(req.nombre())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .rol(req.rol())
                .debeCambiarPassword(true)
                .activo(true)
                .build();

        return mapToDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Integer id, UsuarioUpdateRequest req) {
        log.info("Actualizando datos básicos del usuario ID: {}", id);
        Usuario usuario = getEntidadPorId(id);

        usuarioRepository.findByEmail(req.email()).ifPresent(u -> {
            if (!u.getIdUsuario().equals(id)) {
                throw new IllegalArgumentException("El email ya está registrado por otro usuario.");
            }
        });

        usuario.setNombre(req.nombre());
        usuario.setEmail(req.email());

        return mapToDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO cambiarRol(Integer id, com.proyecto.scca.model.entity.RolUsuario nuevoRol) {
        log.info("Modificando rol del usuario ID: {} a {}", id, nuevoRol);
        Usuario usuario = getEntidadPorId(id);
        usuario.setRol(nuevoRol);
        return mapToDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void activarUsuario(Integer id) {
        log.info("Reactivando cuenta del usuario ID: {}", id);
        Usuario usuario = getEntidadPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Integer id) {
        log.warn("Desactivando cuenta de usuario ID: {}", id);
        Usuario usuario = getEntidadPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario getEntidadPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioDTO> listarUsuariosPaginados(Boolean activo, int page, int size) {
        log.info("Consulta paginada de usuarios. Filtro activo: {}, Página: {}, Tamaño: {}", activo, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("idUsuario").ascending());
        Page<Usuario> paginaResult;

        if (activo != null) {
            paginaResult = usuarioRepository.findByActivo(activo, pageable);
        } else {
            paginaResult = usuarioRepository.findAll(pageable);
        }

        List<UsuarioDTO> contenido = paginaResult.getContent().stream()
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