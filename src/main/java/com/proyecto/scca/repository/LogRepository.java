package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.LogSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogRepository extends JpaRepository<LogSistema, Integer> {
    List<LogSistema> findTop100ByOrderByFechaHoraDesc();
    @Query("SELECT l FROM LogSistema l WHERE " +
            "(:nivel IS NULL OR l.nivel = :nivel) AND " +
            "(:modulo IS NULL OR LOWER(l.modulo) LIKE LOWER(CONCAT('%', :modulo, '%'))) AND " +
            "(cast(:inicio as timestamp) IS NULL OR l.fechaHora >= :inicio) AND " +
            "(cast(:fin as timestamp) IS NULL OR l.fechaHora <= :fin)")
    Page<LogSistema> buscarLogsConFiltros(
            @Param("nivel") String nivel,
            @Param("modulo") String modulo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            Pageable pageable);
}