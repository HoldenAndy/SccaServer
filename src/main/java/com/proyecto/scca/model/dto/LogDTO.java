package com.proyecto.scca.model.dto;

import java.time.LocalDateTime;

public record LogDTO(Integer idLog, String nivel, String modulo, String mensaje, LocalDateTime fechaHora) {}