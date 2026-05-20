package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.NodoEsp32;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NodoRepository extends JpaRepository<NodoEsp32, Integer> {
    Page<NodoEsp32> findByActivo(Boolean activo, Pageable pageable);
    Optional<NodoEsp32> findByMacAddress(String macAddress);
    List<NodoEsp32> findByCliente_IdUsuario(Integer idUsuario);
    List<NodoEsp32> findByEstadoConexionTrueAndUltimaLecturaBefore(LocalDateTime limite);
}