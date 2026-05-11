package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nodos_esp32")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NodoEsp32 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNodo;
    @Column(nullable = false, unique = true) private String macAddress;
    @Column(nullable = false) private String ubicacion;
    @Column(nullable = false) private Boolean estadoConexion;
    private LocalDateTime ultimaLectura;
}