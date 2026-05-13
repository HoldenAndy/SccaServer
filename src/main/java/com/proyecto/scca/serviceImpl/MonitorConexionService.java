package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.model.entity.NodoEsp32;
import com.proyecto.scca.repository.NodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorConexionService {

    private final NodoRepository nodoRepository;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void verificarConexionNodos() {
        LocalDateTime limite = LocalDateTime.now().minusSeconds(30);

        List<NodoEsp32> nodosCaidos = nodoRepository.findByEstadoConexionTrueAndUltimaLecturaBefore(limite);

        for (NodoEsp32 nodo : nodosCaidos) {
            nodo.setEstadoConexion(false);
            log.warn("Alerta SCCA: El nodo {} ha sido marcado como desconectado.", nodo.getMacAddress());
        }

        if (!nodosCaidos.isEmpty()) {
            nodoRepository.saveAll(nodosCaidos);
        }
    }
}