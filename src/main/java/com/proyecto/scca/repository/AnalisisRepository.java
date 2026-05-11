package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalisisRepository extends JpaRepository<AnalisisIa, Integer> {
    List<AnalisisIa> findTop50ByOrderByFechaHoraDesc();

    Optional<AnalisisIa> findByLectura_IdLectura(Integer idLectura);

    Page<AnalisisIa> findByLectura_Nodo_IdNodoAndFechaHoraBetween(
            Integer idNodo, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}