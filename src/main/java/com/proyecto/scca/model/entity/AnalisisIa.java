package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analisis_ia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalisisIa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAnalisis;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lectura", nullable = false)
    private LecturaSensor lectura;

    @Column(columnDefinition = "TEXT", nullable = false) private String resultadoTexto;
    @Column(columnDefinition = "TEXT") private String promptUtilizado;
    @Column(nullable = false) private Integer tiempoResMs;
    @Column(nullable = false) private LocalDateTime fechaHora;
}