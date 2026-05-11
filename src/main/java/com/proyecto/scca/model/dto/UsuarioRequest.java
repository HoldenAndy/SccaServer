package com.proyecto.scca.model.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record UsuarioRequest(@NotBlank String nombre, @Email @NotBlank String email, @NotBlank String rol) {}