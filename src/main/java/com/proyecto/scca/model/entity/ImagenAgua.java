package com.proyecto.scca.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes_agua")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImagenAgua {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImagen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lectura", nullable = false)
    private LecturaSensor lectura;

    @Column(nullable = false) private String rutaArchivo;
    @Column(nullable = false) private Double pesoKb;
    @Column(nullable = false) private LocalDateTime fechaHora;
}