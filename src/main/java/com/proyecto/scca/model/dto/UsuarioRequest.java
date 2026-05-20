package com.proyecto.scca.model.dto;
import com.proyecto.scca.model.entity.RolUsuario;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record UsuarioRequest(
        @NotBlank String nombre,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull RolUsuario rol,
        LocalDateTime fechaCreacion
) {}