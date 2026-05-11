package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodoRepository extends JpaRepository<NodoEsp32, Integer> {
    Optional<NodoEsp32> findByMacAddress(String macAddress);
}