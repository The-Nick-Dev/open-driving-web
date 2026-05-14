package es.arenzana.autoescuela.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PageController {

    // Raíz
    @GetMapping("/")
    public String base() {
        return "login";
    }

    // Login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Admin pages
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/admin";
    }

    @GetMapping("/admin/usuarios")
    public String adminUsuarios() {
        return "admin/admin-usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String adminNuevoUsuario() {
        return "admin/admin-usuario-form";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String adminEditarUsuario(@PathVariable Long id) {
        return "admin/admin-usuario-form";
    }

    @GetMapping("/admin/ajustes")
    public String adminAjustes() {
        return "admin/admin-ajustes";
    }

    // Alumno pages
    @GetMapping("/alumno/dashboard")
    public String alumnoDashboard() {
        return "alumno/dashboard";
    }

    @GetMapping("/alumno/test/nuevo/{temaId}")
    public String alumnoNuevoTest(@PathVariable Long temaId) {
        return "alumno/realizar-test";
    }

    @GetMapping("/alumno/test/aleatorio")
    public String alumnoTestAleatorio() {
        return "alumno/realizar-test";
    }

    @GetMapping("/alumno/test/{testId}/resultado")
    public String alumnoResultadoTest(@PathVariable Long testId) {
        return "alumno/resultado-test";
    }

    // Profesor pages
    @GetMapping("/profesor/temas")
    public String profesorTemas() {
        return "profesor/temas";
    }

    @GetMapping("/profesor/preguntas")
    public String profesorPreguntas() {
        return "profesor/preguntas";
    }

    @GetMapping("/profesor/preguntas/nueva")
    public String profesorNuevaPregunta() {
        return "profesor/pregunta-form";
    }

    @GetMapping("/profesor/preguntas/{id}/editar")
    public String profesorEditarPregunta(@PathVariable Long id) {
        return "profesor/pregunta-form";
    }

    // Error pages
    @GetMapping("/error/403")
    public String error403() {
        return "error/403";
    }

    @GetMapping("/error/404")
    public String error404() {
        return "error/404";
    }

    @GetMapping("/error/500")
    public String error500() {
        return "error/500";
    }
}
