package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_sistema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LogSistema {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLog;
    @Column(nullable = false) private String nivel; // INFO, WARN, ERROR
    @Column(nullable = false) private String modulo;
    @Column(columnDefinition = "TEXT", nullable = false) private String mensaje;
    @Column(nullable = false) private LocalDateTime fechaHora;
}