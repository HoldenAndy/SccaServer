package com.proyecto.scca.serviceImpl;

import com.proyecto.scca.exception.ResourceNotFoundException;
import com.proyecto.scca.model.dto.AnalisisDTO;
import com.proyecto.scca.model.dto.PageResponse;
import com.proyecto.scca.model.entity.*;
import com.proyecto.scca.repository.AnalisisRepository;
import com.proyecto.scca.repository.ImagenRepository;
import com.proyecto.scca.security.UserDetailsImpl;
import com.proyecto.scca.service.AnalisisIaService;
import com.proyecto.scca.service.LecturaService;
import com.proyecto.scca.service.NodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalisisIaServiceImpl implements AnalisisIaService {

    private final AnalisisRepository analisisRepo;
    private final LecturaService lecturaService;
    private final ImagenRepository imagenRepo;
    private final NodoService nodoService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private AnalisisDTO mapToDTO(AnalisisIa a) {
        return new AnalisisDTO(a.getIdAnalisis(), a.getLectura().getIdLectura(), a.getResultadoTexto(), a.getPromptUtilizado(), a.getTiempoResMs(), a.getFechaHora());
    }

    private void validarPropiedadDelNodo(NodoEsp32 nodo) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("No se pudo validar la identidad del usuario.");
        }

        String rolActual = userDetails.getUsuario().getRol().name();
        if (rolActual.equals(RolUsuario.ADMINISTRADOR.name()) || rolActual.equals(RolUsuario.SOPORTE.name()) || rolActual.equals(RolUsuario.GESTIONADOR.name())) {
            return;
        }

        Integer idUsuarioAutenticado = userDetails.getUsuario().getIdUsuario();
        Integer idDueñoDelNodo = nodo.getCliente().getIdUsuario();

        if (!idDueñoDelNodo.equals(idUsuarioAutenticado)) {
            log.warn("BLOQUEO MULTITENENCIA: Usuario {} intentó acceder a datos de IA del Nodo {}",
                    idUsuarioAutenticado, nodo.getIdNodo());
            throw new AccessDeniedException("Acceso denegado: Este diagnóstico no pertenece a su hardware.");
        }
    }

    @Override
    @Transactional
    public AnalisisDTO generarAnalisisReal(Integer idLectura) {
        log.info("Solicitando análisis de IA para la lectura ID: {}", idLectura);

        LecturaSensor lectura = lecturaService.getEntidadPorId(idLectura);
        validarPropiedadDelNodo(lectura.getNodo());
        Optional<ImagenAgua> imagenOpt = imagenRepo.findByLectura_IdLectura(idLectura);
        String base64Image = "";

        if (imagenOpt.isPresent()) {
            try {
                Path path = Paths.get(imagenOpt.get().getRutaArchivo());
                byte[] imageBytes = Files.readAllBytes(path);
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                log.error("Error crítico al leer la imagen física en: {}", imagenOpt.get().getRutaArchivo(), e);
                throw new RuntimeException("No se pudo acceder al archivo de imagen para el análisis de visión.");
            }
        } else {
            log.warn("Análisis abortado: La lectura {} no tiene una imagen asociada en BD.", idLectura);
            throw new IllegalArgumentException("Se requiere una imagen para procesar el análisis con Gemini Vision.");
        }

        String prompt = String.format(
                "Eres un asistente amigable y preventivo que ayuda a familias en su hogar a entender si el agua de su tanque está en buenas condiciones. " +
                        "Revisa estos datos de los sensores: pH: %.2f, Temperatura: %.2f°C, Turbidez (qué tan sucia se ve): %.2f NTU, TDS (partículas disueltas): %.2f ppm. " +
                        "Mira también la foto del agua adjunta. " +
                        "Escribe un mensaje corto, directo y SIN palabras técnicas complejas (máximo 3 párrafos). Debes decirle al usuario: " +
                        "1. ¿El agua parece estar en buen estado o tiene algún problema evidente? " +
                        "2. ¿Qué significan esos números y la foto de forma muy sencilla? (ej. 'el agua está muy turbia', 'el pH está un poco alto'). " +
                        "3. Una recomendación muy práctica para la casa (ej. 'te recomiendo hervirla antes de tomar', 'es mejor usarla solo para limpiar o regar', 'parece perfecta para consumir'). " +
                        "Al final, añade una nota corta y amable recordando que esta es una estimación de sensores inteligentes y no reemplaza un análisis de laboratorio médico.",
                lectura.getPh(), lectura.getTemperatura(), lectura.getTurbidez(), lectura.getTds()
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of("mime_type", "image/jpeg", "data", base64Image))
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        long startTime = System.currentTimeMillis();
        Map<String, Object> response;
        try {
            response = restTemplate.postForObject(geminiApiUrl + "?key=" + geminiApiKey, requestEntity, Map.class);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Google Gemini rechazó la petición con código HTTP {}. Detalle exacto: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error en la API de Gemini: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Fallo de red al intentar contactar a Google Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Error de red al contactar a Gemini Vision.");
        }

        long endTime = System.currentTimeMillis();
        int tiempoResMs = (int) (endTime - startTime);

        String resultadoIA = extraerTextoDeRespuesta(response);

        AnalisisIa analisis = AnalisisIa.builder()
                .lectura(lectura)
                .resultadoTexto(resultadoIA)
                .promptUtilizado(prompt)
                .tiempoResMs(tiempoResMs)
                .fechaHora(LocalDateTime.now())
                .build();

        return mapToDTO(analisisRepo.save(analisis));
    }

    @Override
    public List<AnalisisDTO> obtenerHistorial() {
        return analisisRepo.findTop50ByOrderByFechaHoraDesc()
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public PageResponse<AnalisisDTO> obtenerAnalisisPaginados(Integer idNodo, LocalDateTime inicio, LocalDateTime fin, int page, int size) {

        NodoEsp32 nodo = nodoService.getEntidadPorId(idNodo);
        validarPropiedadDelNodo(nodo);

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());
        Page<AnalisisIa> pagina = analisisRepo.findByLectura_Nodo_IdNodoAndFechaHoraBetween(idNodo, inicio, fin, pageable);

        List<AnalisisDTO> contenido = pagina.getContent().stream().map(this::mapToDTO).toList();
        return new PageResponse<>(
                contenido, pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages(), pagina.isLast()
        );
    }

    @Override
    public AnalisisDTO obtenerPorLectura(Integer idLectura) {

        LecturaSensor lectura = lecturaService.getEntidadPorId(idLectura);
        validarPropiedadDelNodo(lectura.getNodo());

        return analisisRepo.findByLectura_IdLectura(idLectura)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró análisis de IA para lectura: " + idLectura));
    }


    @SuppressWarnings("unchecked")
    private String extraerTextoDeRespuesta(Map<String, Object> response) {
        try {
            if (response == null) throw new RuntimeException("Respuesta nula");
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Error decodificando JSON de Gemini. Respuesta: {}", response);
            throw new RuntimeException("Estructura de respuesta de IA no reconocida.");
        }
    }
}