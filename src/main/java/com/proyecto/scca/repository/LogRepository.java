package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogRepository extends JpaRepository<LogSistema, Integer> {
    List<LogSistema> findTop100ByOrderByFechaHoraDesc();
}