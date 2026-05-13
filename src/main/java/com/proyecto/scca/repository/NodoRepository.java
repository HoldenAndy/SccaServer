package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NodoRepository extends JpaRepository<NodoEsp32, Integer> {
    Optional<NodoEsp32> findByMacAddress(String macAddress);
    List<NodoEsp32> findByCliente_IdUsuario(Integer idUsuario);
    List<NodoEsp32> findByEstadoConexionTrueAndUltimaLecturaBefore(LocalDateTime limite);
}