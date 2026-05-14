package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.model.Rol;
import es.arenzana.autoescuela.model.Usuario;
import es.arenzana.autoescuela.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername((String) request.get("username"));
            usuario.setPassword((String) request.get("password"));
            usuario.setNombre((String) request.get("nombre"));
            usuario.setCorreo((String) request.get("correo"));
            usuario.setRol(Rol.valueOf((String) request.get("rol")));
            if (request.get("fechaExpiracion") != null && !((String) request.get("fechaExpiracion")).isEmpty()) {
                usuario.setFechaExpiracion(LocalDate.parse((String) request.get("fechaExpiracion")));
            }

            Usuario saved = usuarioService.crearUsuario(usuario);
            saved.setPassword(null);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Usuario usuarioActualizado = new Usuario();
            if (request.containsKey("nombre")) {
                usuarioActualizado.setNombre((String) request.get("nombre"));
            }
            if (request.containsKey("correo")) {
                usuarioActualizado.setCorreo((String) request.get("correo"));
            }
            if (request.containsKey("rol")) {
                usuarioActualizado.setRol(Rol.valueOf((String) request.get("rol")));
            }
            if (request.containsKey("password")) {
                String password = (String) request.get("password");
                if (password != null && !password.isEmpty()) {
                    usuarioActualizado.setPassword(password);
                }
            }
            
            if (request.containsKey("fechaExpiracion")) {
                String fechaStr = (String) request.get("fechaExpiracion");
                if (fechaStr != null && !fechaStr.isEmpty()) {
                    usuarioActualizado.setFechaExpiracion(LocalDate.parse(fechaStr));
                } else {
                    usuarioActualizado.setFechaExpiracion(null);
                }
            }

            Usuario updated = usuarioService.actualizarUsuario(id, usuarioActualizado);
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}