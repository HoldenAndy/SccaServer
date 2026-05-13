package com.proyecto.scca.controller;

import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.Usuario;
import com.proyecto.scca.repository.UsuarioRepository;
import com.proyecto.scca.security.JwtUtils;
import com.proyecto.scca.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        String jwtToken = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(
                jwtToken,
                usuario.getNombre(),
                usuario.getRol(),
                usuario.getDebeCambiarPassword()
        ));
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication authentication // Spring inyecta al usuario logueado gracias al JWT
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(request.newPassword()));
        usuario.setDebeCambiarPassword(false);

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Contraseña actualizada correctamente. Ya puedes acceder al sistema.");
    }
}