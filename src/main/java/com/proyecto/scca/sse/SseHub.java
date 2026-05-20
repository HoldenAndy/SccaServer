package com.proyecto.scca.sse;

import com.proyecto.scca.model.dto.LecturaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class SseHub {

    private static final long TIMEOUT_MS = 30L * 60L * 1000L;

    private final Map<Integer, List<SseEmitter>> emisoresPorUsuario = new ConcurrentHashMap<>();

    public SseEmitter registrar(Integer idUsuario) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);

        emisoresPorUsuario
                .computeIfAbsent(idUsuario, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remover(idUsuario, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            remover(idUsuario, emitter);
        });
        emitter.onError(e -> {
            log.debug("Error en emitter de usuario {}: {}", idUsuario, e.getMessage());
            remover(idUsuario, emitter);
        });

        try {
            emitter.send(SseEmitter.event().name("conectado").data("ok"));
        } catch (IOException e) {
            remover(idUsuario, emitter);
        }

        log.debug("SSE conectado: usuario={}, total={}", idUsuario,
                emisoresPorUsuario.get(idUsuario).size());
        return emitter;
    }

    private void remover(Integer idUsuario, SseEmitter emitter) {
        List<SseEmitter> lista = emisoresPorUsuario.get(idUsuario);
        if (lista != null) {
            lista.remove(emitter);
            if (lista.isEmpty()) emisoresPorUsuario.remove(idUsuario);
        }
    }

    public void publicarLectura(LecturaDTO lectura) {
        emisoresPorUsuario.forEach((idUsuario, lista) ->
                lista.forEach(emitter -> enviar(emitter, "lectura", lectura, idUsuario))
        );
    }

    private void enviar(SseEmitter emitter, String evento, Object data, Integer idUsuario) {
        try {
            emitter.send(SseEmitter.event().name(evento).data(data));
        } catch (IOException | IllegalStateException e) {
            log.debug("Fallo al enviar evento a usuario {}: {}", idUsuario, e.getMessage());
            remover(idUsuario, emitter);
        }
    }

    @Scheduled(fixedRate = 25_000)
    public void heartbeat() {
        emisoresPorUsuario.forEach((idUsuario, lista) ->
                lista.forEach(e -> enviar(e, "ping", System.currentTimeMillis(), idUsuario))
        );
    }
}
