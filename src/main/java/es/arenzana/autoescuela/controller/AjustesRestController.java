package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.model.Ajustes;
import es.arenzana.autoescuela.service.AjustesService;
import es.arenzana.autoescuela.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AjustesRestController {

    private final AjustesService ajustesService;
    private final EmailService emailService;

    public AjustesRestController(AjustesService ajustesService, EmailService emailService) {
        this.ajustesService = ajustesService;
        this.emailService = emailService;
    }

    @GetMapping("/nombre")
    public ResponseEntity<Map<String, String>> obtenerNombre() {
        String nombre = ajustesService.obtenerAjustes().getNombreEmpresa();
        return ResponseEntity.ok(Map.of("nombreEmpresa", nombre != null ? nombre : ""));
    }

    @GetMapping("/ajustes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ajustes> obtenerAjustes() {
        Ajustes ajustes = ajustesService.obtenerAjustes();
        ajustes.setMailPassword(null);
        return ResponseEntity.ok(ajustes);
    }

    @PutMapping("/ajustes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ajustes> guardarAjustes(@RequestBody Map<String, Object> request) {
        Ajustes ajustes = ajustesService.obtenerAjustes();

        if (request.containsKey("nombreEmpresa")) {
            ajustes.setNombreEmpresa((String) request.get("nombreEmpresa"));
        }
        if (request.containsKey("mailHost")) {
            ajustes.setMailHost((String) request.get("mailHost"));
        }
        if (request.containsKey("mailPort")) {
            ajustes.setMailPort(Integer.valueOf(request.get("mailPort").toString()));
        }
        if (request.containsKey("mailUsername")) {
            ajustes.setMailUsername((String) request.get("mailUsername"));
        }
        if (request.containsKey("mailPassword")) {
            String password = (String) request.get("mailPassword");
            if (password != null && !password.isEmpty()) {
                ajustes.setMailPassword(password);
            }
        }
        if (request.containsKey("mailProtocol")) {
            ajustes.setMailProtocol((String) request.get("mailProtocol"));
        }

        ajustesService.guardarAjustes(ajustes);
        ajustes.setMailPassword(null);
        return ResponseEntity.ok(ajustes);
    }

    @PostMapping("/ajustes/test-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enviarTestEmail(@RequestBody Map<String, String> request) {
        String testEmail = request.get("testEmail");
        if (testEmail == null || testEmail.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email requerido"));
        }

        try {
            emailService.enviarCorreo(
                testEmail,
                "Prueba de configuración - " + ajustesService.obtenerAjustes().getNombreEmpresa(),
                "Este es un correo de prueba de la autoescuela."
            );
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al enviar: " + e.getMessage()));
        }
    }
}
