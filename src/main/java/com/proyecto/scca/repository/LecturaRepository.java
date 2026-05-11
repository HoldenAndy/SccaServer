package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.LecturaSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LecturaRepository extends JpaRepository<LecturaSensor, Integer> {

    Optional<LecturaSensor> findTop1ByNodo_IdNodoOrderByFechaHoraDesc(Integer idNodo);

    List<LecturaSensor> findTop100ByNodo_IdNodoOrderByFechaHoraDesc(Integer idNodo);

    Page<LecturaSensor> findByNodo_IdNodoAndFechaHoraBetween(
            Integer idNodo, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    List<LecturaSensor> findByNodo_IdNodoAndFechaHoraBetweenOrderByFechaHoraAsc(
            Integer idNodo, LocalDateTime inicio, LocalDateTime fin);
}