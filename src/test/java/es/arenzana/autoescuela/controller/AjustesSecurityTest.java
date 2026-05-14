package es.arenzana.autoescuela.controller;

import es.arenzana.autoescuela.model.Ajustes;
import es.arenzana.autoescuela.service.AjustesService;
import es.arenzana.autoescuela.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de INTEGRACIÓN que verifican la configuración de seguridad.
 *
 * ¿Qué es un test de integración?
 *   Arranca el contexto completo de Spring (incluyendo Spring Security),
 *   pero usa una BD H2 en memoria (perfil "test") en lugar de MariaDB.
 *   Es más lento que un test unitario, pero prueba que las piezas encajan.
 *
 * Herramientas clave:
 *   @SpringBootTest         arranca el contexto Spring completo
 *   @AutoConfigureMockMvc   inyecta MockMvc para hacer peticiones HTTP simuladas
 *   @ActiveProfiles("test")  activa application-test.properties (H2 en vez de MariaDB)
 *   @MockitoBean            reemplaza un bean Spring por un mock de Mockito
 *                            (evita tocar la BD real y el servidor SMTP)
 *   @WithMockUser           simula un usuario autenticado con el rol indicado
 *                            (no necesita JWT; lo gestiona Spring Security Test)
 *
 * ¿Por qué @MockitoBean y no @Mock?
 *   @Mock crea un objeto Mockito pero Spring no lo conoce.
 *   @MockitoBean reemplaza el bean en el contexto de Spring, así el controlador
 *   recibe el mock en lugar del servicio real.
 *   (Nota: @MockitoBean es el reemplazo de @MockBean desde Spring Boot 4.0)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AjustesSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean AjustesService ajustesService;
    @MockitoBean EmailService emailService;

    @BeforeEach
    void configurarMocks() {
        Ajustes ajustes = new Ajustes();
        ajustes.setNombreEmpresa("Autoescuela Test");
        ajustes.setMailHost("smtp.test.com");
        when(ajustesService.obtenerAjustes()).thenReturn(ajustes);
    }

    @Test
    void api_nombre_es_accesible_sin_autenticacion() throws Exception {
        mockMvc.perform(get("/api/nombre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("Autoescuela Test"));
    }

    // /api/ajustes — debe requerir autenticación (401) y rol ADMIN (403)
    @Test
    void api_ajustes_rechaza_peticion_sin_autenticacion() throws Exception {
        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void api_ajustes_es_accesible_para_admin() throws Exception {
        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void api_ajustes_rechaza_rol_alumno() throws Exception {
        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    void api_ajustes_rechaza_rol_profesor() throws Exception {
        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isForbidden());
    }
}
