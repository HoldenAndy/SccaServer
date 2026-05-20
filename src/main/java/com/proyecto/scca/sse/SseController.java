package com.proyecto.scca.sse;

import com.proyecto.scca.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseHub sseHub;

    @GetMapping(value = "/lecturas", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLecturas(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return sseHub.registrar(userDetails.getUsuario().getIdUsuario());
    }
}
