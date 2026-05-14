package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.dto.PreguntaConTemaDTO;
import es.arenzana.autoescuela.model.Pregunta;
import es.arenzana.autoescuela.service.PreguntaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/preguntas")
@PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
public class PreguntaRestController {

    private final PreguntaService preguntaService;

    public PreguntaRestController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Pregunta> preguntas = preguntaService.listarTodas();
        List<PreguntaConTemaDTO> preguntaDTOs = preguntas.stream()
                .map(p -> new PreguntaConTemaDTO(p, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(preguntaDTOs);
    }

    @GetMapping("/tema/{temaId}")
    public ResponseEntity<?> listarPorTema(@PathVariable Long temaId) {
        List<Pregunta> preguntas = preguntaService.listarPorTema(temaId);
        List<PreguntaConTemaDTO> preguntaDTOs = preguntas.stream()
                .map(p -> new PreguntaConTemaDTO(p, false))
                .collect(Collectors.toList());
        return ResponseEntity.ok(preguntaDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Pregunta pregunta = preguntaService.obtenerPorId(id);
            return ResponseEntity.ok(new PreguntaConTemaDTO(pregunta));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> request) {
        try {
            String texto = (String) request.get("texto");
            Long temaId = ((Number) request.get("temaId")).longValue();
            String explicacion = (String) request.get("explicacion");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> respuestasData = (List<Map<String, Object>>) request.get("respuestas");

            List<PreguntaService.RespuestaData> respuestas = respuestasData.stream()
                    .map(r -> {
                        PreguntaService.RespuestaData rd = new PreguntaService.RespuestaData();
                        rd.setTexto((String) r.get("texto"));
                        rd.setCorrecta((Boolean) r.get("correcta"));
                        return rd;
                    })
                    .toList();

            Pregunta pregunta = preguntaService.crearPregunta(texto, temaId, explicacion, respuestas);
            return ResponseEntity.ok(pregunta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String texto = (String) request.get("texto");
            Long temaId = ((Number) request.get("temaId")).longValue();
            String explicacion = (String) request.get("explicacion");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> respuestasData = (List<Map<String, Object>>) request.get("respuestas");

            List<PreguntaService.RespuestaData> respuestas = respuestasData.stream()
                    .map(r -> {
                        PreguntaService.RespuestaData rd = new PreguntaService.RespuestaData();
                        rd.setTexto((String) r.get("texto"));
                        rd.setCorrecta((Boolean) r.get("correcta"));
                        return rd;
                    })
                    .toList();

            Pregunta pregunta = preguntaService.actualizarPregunta(id, texto, temaId, explicacion, respuestas);
            return ResponseEntity.ok(pregunta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            preguntaService.eliminarPregunta(id);
            return ResponseEntity.ok(Map.of("mensaje", "Pregunta eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}