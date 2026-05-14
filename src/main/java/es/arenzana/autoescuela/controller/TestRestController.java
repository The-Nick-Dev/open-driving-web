package es.arenzana.autoescuela.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.arenzana.autoescuela.dto.EstadisticasDTO;
import es.arenzana.autoescuela.dto.TestHistorialDTO;
import es.arenzana.autoescuela.dto.TestProgresoDTO;
import es.arenzana.autoescuela.model.TestRealizado;
import es.arenzana.autoescuela.model.Usuario;
import es.arenzana.autoescuela.service.TestService;
import es.arenzana.autoescuela.service.UsuarioService;

@RestController
@RequestMapping("/api/test")
public class TestRestController {

    private final TestService testService;
    private final UsuarioService usuarioService;

    public TestRestController(TestService testService, UsuarioService usuarioService) {
        this.testService = testService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/iniciar-aleatorio")
    public ResponseEntity<?> iniciarTestAleatorio(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario alumno = usuarioService.findByUsernameOrEmail(userDetails.getUsername());
            TestRealizado test = testService.iniciarTestAleatorio(alumno, 30);
            return ResponseEntity.ok(Map.of(
                    "testId", test.getId(),
                    "tema", "Aleatorio",
                    "totalPreguntas", test.getTotalPreguntas()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/iniciar/{temaId}")
    public ResponseEntity<?> iniciarTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long temaId) {
        try {
            Usuario alumno = usuarioService.findByUsernameOrEmail(userDetails.getUsername());
            TestRealizado test = testService.iniciarTest(alumno, temaId);
            return ResponseEntity.ok(Map.of(
                    "testId", test.getId(),
                    "tema", test.getTema().getNombre(),
                    "totalPreguntas", test.getTotalPreguntas()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{testId}")
    public ResponseEntity<?> obtenerTest(@PathVariable Long testId) {
        try {
            TestProgresoDTO progreso = testService.obtenerProgresoTest(testId);
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{testId}/responder")
    public ResponseEntity<?> responder(
            @PathVariable Long testId,
            @RequestBody Map<String, Long> request) {
        try {
            Long preguntaId = request.get("preguntaId");
            Long respuestaId = request.get("respuestaId");

            testService.guardarRespuesta(testId, preguntaId, respuestaId);

            return ResponseEntity.ok(Map.of("mensaje", "Respuesta guardada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{testId}/completar")
    public ResponseEntity<?> completarTest(@PathVariable Long testId) {
        try {
            TestRealizado test = testService.completarTest(testId);
            return ResponseEntity.ok(Map.of(
                    "testId", test.getId(),
                    "completado", test.getCompletado(),
                    "aciertos", test.getAciertos(),
                    "fallos", test.getFallos(),
                    "puntuacion", test.getPuntuacionTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario alumno = usuarioService.findByUsernameOrEmail(userDetails.getUsername());
            EstadisticasDTO estadisticas = testService.obtenerEstadisticas(alumno);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            Usuario alumno = usuarioService.findByUsernameOrEmail(userDetails.getUsername());
            List<TestRealizado> tests = testService.obtenerUltimosTests(alumno, limite);
            List<TestHistorialDTO> testHistorialDTOs = tests.stream()
                    .map(TestHistorialDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(testHistorialDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}