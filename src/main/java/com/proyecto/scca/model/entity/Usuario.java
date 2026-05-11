package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    @Column(nullable = false) private String nombre;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String rol; // ADMIN, OPERADOR
}