package es.arenzana.autoescuela.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.arenzana.autoescuela.dto.TemaConPreguntasDTO;
import es.arenzana.autoescuela.dto.TemaDTO;
import es.arenzana.autoescuela.model.Tema;
import es.arenzana.autoescuela.repository.TemaRepository;

@RestController
@RequestMapping("/api/temas")
public class TemaRestController {

    private final TemaRepository temaRepository;

    public TemaRestController(TemaRepository temaRepository) {
        this.temaRepository = temaRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar() {
        List<Tema> temas = temaRepository.findByActivoTrueOrderByOrdenAsc();
        List<TemaDTO> temaDTOs = temas.stream()
                .map(TemaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(temaDTOs);
    }

    @GetMapping("/todos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarTodos() {
        List<Tema> temas = temaRepository.findAllByOrderByOrdenAsc();
        List<TemaDTO> temaDTOs = temas.stream()
                .map(TemaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(temaDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return temaRepository.findById(id)
                .map(tema -> ResponseEntity.ok(new TemaConPreguntasDTO(tema)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> request) {
        try {
            Tema tema = new Tema();
            tema.setNombre((String) request.get("nombre"));
            tema.setDescripcion((String) request.get("descripcion"));
            tema.setActivo(true);
            tema.setOrden(((Number) request.get("orden")).intValue());

            Tema saved = temaRepository.save(tema);
            return ResponseEntity.ok(new TemaDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        return temaRepository.findById(id)
                .map(tema -> {
                    if (request.containsKey("nombre")) {
                        tema.setNombre((String) request.get("nombre"));
                    }
                    if (request.containsKey("descripcion")) {
                        tema.setDescripcion((String) request.get("descripcion"));
                    }
                    if (request.containsKey("orden")) {
                        tema.setOrden(((Number) request.get("orden")).intValue());
                    }
                    if (request.containsKey("activo")) {
                        tema.setActivo((Boolean) request.get("activo"));
                    }
                    Tema saved = temaRepository.save(tema);
                    return ResponseEntity.ok(new TemaDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return temaRepository.findById(id)
                .map(tema -> {
                    tema.setActivo(false);
                    temaRepository.save(tema);
                    return ResponseEntity.ok(Map.of("mensaje", "Tema eliminado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}