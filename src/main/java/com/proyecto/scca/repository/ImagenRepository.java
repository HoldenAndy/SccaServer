package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImagenRepository extends JpaRepository<ImagenAgua, Integer> {
    Optional<ImagenAgua> findByLectura_IdLectura(Integer idLectura);
}