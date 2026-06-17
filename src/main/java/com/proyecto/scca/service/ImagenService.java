package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.ImagenDTO;
import com.proyecto.scca.model.dto.ImagenRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagenService {
    ImagenDTO registrarMetadataImagen(ImagenRequest req);
    ImagenDTO registrarImagenHardware(Integer idLectura, MultipartFile archivo);
    List<ImagenDTO> listarImagenes();
    ImagenDTO obtenerPorLectura(Integer idLectura);
}
