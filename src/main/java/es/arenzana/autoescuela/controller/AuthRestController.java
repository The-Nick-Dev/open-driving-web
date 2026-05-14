package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.dto.LoginRequest;
import es.arenzana.autoescuela.dto.LoginResponse;
import es.arenzana.autoescuela.model.Usuario;
import es.arenzana.autoescuela.security.JwtService;
import es.arenzana.autoescuela.service.UsuarioService;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthRestController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String usernameOrEmail = request.getUsername();
            String rawPassword = request.getPassword();

            Usuario usuario = usuarioService.findByUsernameOrEmail(usernameOrEmail);

            // Comparar contraseña
            if (!usuarioService.checkPassword(rawPassword, usuario.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
            }

            // Comprobar expiración
            if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().isBefore(LocalDate.now())) {
                return ResponseEntity.status(403).body(Map.of("error", "Tu cuenta ha expirado."));
            }

            // Cargar UserDetails para el Token
            UserDetails userDetails = usuarioService.loadUserByUsername(usuario.getUsername());
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getRol()
            ));

        } catch (RuntimeException e) {
            e.printStackTrace(); 
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado o error de sistema"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Usuario usuario = usuarioService.findByUsernameOrEmail(username);

        LoginResponse response = new LoginResponse(
                token,
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getRol()
        );
        return ResponseEntity.ok(response);
    }
}
