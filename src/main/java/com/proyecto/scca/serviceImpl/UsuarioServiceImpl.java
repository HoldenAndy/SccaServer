package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.Usuario;
import com.proyecto.scca.repository.UsuarioRepository;
import com.proyecto.scca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Inyectado

    private UsuarioDTO mapToDTO(Usuario u) {
        return new UsuarioDTO(u.getIdUsuario(), u.getNombre(), u.getEmail(), u.getRol());
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
                .debeCambiarPassword(true) // Forzar cambio
                .build();

        return mapToDTO(usuarioRepository.save(usuario));
    }

    @Override
    public Usuario getEntidadPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
    }
}