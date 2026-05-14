package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import es.arenzana.autoescuela.service.PreguntaService;
import es.arenzana.autoescuela.service.importer.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preguntas/importar")
@PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
public class ImportarPreguntasController {

    private final PreguntaService preguntaService;

    public ImportarPreguntasController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @PostMapping
    public ResponseEntity<?> importar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("formato") String formato,
            @RequestParam("temaId") Long temaId) {
        try {
            QuestionImporter importer = switch (formato.toLowerCase()) {
                case "moodle"       -> new MoodleXmlImporter();
                case "hotpotatoes"  -> new HotPotatoesImporter();
                case "blackboard"   -> new BlackboardImporter();
                case "examview"     -> new ExamViewImporter();
                case "webct"        -> new QtiImporter();
                default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
            };

            List<PreguntaImportada> parsed = importer.importar(file.getInputStream());
            int importadas = preguntaService.importarPreguntas(parsed, temaId);

            return ResponseEntity.ok(Map.of(
                "importadas", importadas,
                "detectadas", parsed.size(),
                "mensaje", importadas + " de " + parsed.size() + " preguntas importadas correctamente"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Error al procesar el archivo: " + e.getMessage()));
        }
    }
}
