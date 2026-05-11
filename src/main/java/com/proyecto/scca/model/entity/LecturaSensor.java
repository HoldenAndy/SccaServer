package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas_sensores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LecturaSensor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLectura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nodo", nullable = false)
    private NodoEsp32 nodo;

    @Column(nullable = false) private Double ph;
    @Column(nullable = false) private Double temperatura;
    @Column(nullable = false) private Double turbidez;
    @Column(nullable = false) private Double tds;

    @Column(nullable = false)
    private LocalDateTime fechaHora;
}