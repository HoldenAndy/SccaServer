package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.*;
import com.proyecto.scca.model.entity.ImagenAgua;
import com.proyecto.scca.model.entity.LecturaSensor;
import com.proyecto.scca.repository.ImagenRepository;
import com.proyecto.scca.service.ImagenService;
import com.proyecto.scca.service.LecturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagenServiceImpl implements ImagenService {

    private final ImagenRepository imagenRepository;
    private final LecturaService lecturaService;

    private ImagenDTO mapToDTO(ImagenAgua i) {
        return new ImagenDTO(i.getIdImagen(), i.getLectura().getIdLectura(), i.getRutaArchivo(), i.getPesoKb(), i.getFechaHora());
    }

    @Override
    @Transactional
    public ImagenDTO registrarMetadataImagen(ImagenRequest req) {
        log.info("Vinculando imagen a la lectura ID: {}", req.idLectura());
        LecturaSensor lectura = lecturaService.getEntidadPorId(req.idLectura());

        ImagenAgua imagen = ImagenAgua.builder()
                .lectura(lectura)
                .rutaArchivo(req.rutaArchivo())
                .pesoKb(req.pesoKb())
                .fechaHora(LocalDateTime.now())
                .build();

        return mapToDTO(imagenRepository.save(imagen));
    }

    @Override
    public List<ImagenDTO> listarImagenes() {
        return imagenRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public ImagenDTO obtenerPorLectura(Integer idLectura) {
        return imagenRepository.findByLectura_IdLectura(idLectura)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró imagen para la lectura: " + idLectura));
    }
}