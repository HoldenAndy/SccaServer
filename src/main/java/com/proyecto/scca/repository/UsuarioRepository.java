package com.proyecto.scca.repository;

import com.proyecto.scca.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Page<Usuario> findByActivo(Boolean activo, Pageable pageable);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
}