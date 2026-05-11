package com.proyecto.scca.service;

import com.proyecto.scca.model.dto.ImagenDTO;
import com.proyecto.scca.model.dto.ImagenRequest;

import java.util.List;

public interface ImagenService {
    ImagenDTO registrarMetadataImagen(ImagenRequest req);
    List<ImagenDTO> listarImagenes();
    ImagenDTO obtenerPorLectura(Integer idLectura);
}
