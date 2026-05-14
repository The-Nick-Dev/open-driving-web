package es.arenzana.autoescuela.service;

import es.arenzana.autoescuela.dto.EstadisticasDTO;
import es.arenzana.autoescuela.model.*;
import es.arenzana.autoescuela.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de TestService.
 *
 * Cubre la lógica de negocio más importante:
 *   - completarTest: calcula la puntuación y es idempotente
 *   - guardarRespuesta: actualiza contadores correctamente al cambiar de respuesta
 *   - obtenerEstadisticas: calcula mejorPuntuacion y mediaAciertos por tema
 *
 * Patrón de mocks:
 *   @Mock       repositorio falso — no accede a la BD
 *   @InjectMocks  el servicio REAL al que inyectamos esos mocks
 *   when(repo.metodo()).thenReturn(...)  define qué devuelve el falso
 */
@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    @Mock TestRealizadoRepository testRealizadoRepository;
    @Mock RespuestaAlumnoRepository respuestaAlumnoRepository;
    @Mock PreguntaRepository preguntaRepository;
    @Mock TemaRepository temaRepository;
    @Mock RespuestaRepository respuestaRepository;

    @InjectMocks TestService testService;

    // completarTest
    @Test
    void completarTest_calcula_puntuacion_como_porcentaje() {
        TestRealizado test = testConDatos(1L, 18, 12, 30, false);
        when(testRealizadoRepository.findById(1L)).thenReturn(Optional.of(test));
        when(testRealizadoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TestRealizado resultado = testService.completarTest(1L);

        assertThat(resultado.getPuntuacionTotal()).isEqualTo(60);  // (18*100)/30
        assertThat(resultado.getCompletado()).isTrue();
        assertThat(resultado.getFechaFin()).isNotNull();
    }

    @Test
    void completarTest_no_modifica_un_test_ya_completado() {
        TestRealizado test = testConDatos(2L, 10, 5, 15, true);
        test.setPuntuacionTotal(66);
        when(testRealizadoRepository.findById(2L)).thenReturn(Optional.of(test));

        TestRealizado resultado = testService.completarTest(2L);

        assertThat(resultado.getPuntuacionTotal()).isEqualTo(66);
        verify(testRealizadoRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // guardarRespuesta
    // -----------------------------------------------------------------------

    @Test
    void guardarRespuesta_incrementa_aciertos_con_respuesta_correcta() {
        // Arrange
        TestRealizado test = testConDatos(3L, 0, 0, 5, false);
        Pregunta pregunta = preguntaConId(10L);
        Respuesta respuesta = respuestaConId(20L, true);

        when(testRealizadoRepository.findById(3L)).thenReturn(Optional.of(test));
        when(preguntaRepository.findById(10L)).thenReturn(Optional.of(pregunta));
        when(respuestaRepository.findById(20L)).thenReturn(Optional.of(respuesta));
        when(respuestaAlumnoRepository.findByTestAndPreguntaId(test, 10L)).thenReturn(Optional.empty());

        testService.guardarRespuesta(3L, 10L, 20L);

        assertThat(test.getAciertos()).isEqualTo(1);
        assertThat(test.getFallos()).isEqualTo(0);
        verify(testRealizadoRepository).save(test);
    }

    @Test
    void guardarRespuesta_actualiza_contadores_al_cambiar_de_correcta_a_incorrecta() {
        TestRealizado test = testConDatos(4L, 1, 0, 5, false);
        Pregunta pregunta = preguntaConId(11L);
        Respuesta nuevaRespuesta = respuestaConId(22L, false);  // incorrecta

        RespuestaAlumno respuestaPrevia = new RespuestaAlumno();
        respuestaPrevia.setCorrecta(true);
        respuestaPrevia.setRespuestaSeleccionada(respuestaConId(21L, true));

        when(testRealizadoRepository.findById(4L)).thenReturn(Optional.of(test));
        when(preguntaRepository.findById(11L)).thenReturn(Optional.of(pregunta));
        when(respuestaRepository.findById(22L)).thenReturn(Optional.of(nuevaRespuesta));
        when(respuestaAlumnoRepository.findByTestAndPreguntaId(test, 11L))
                .thenReturn(Optional.of(respuestaPrevia));

        testService.guardarRespuesta(4L, 11L, 22L);

        assertThat(test.getAciertos()).isEqualTo(0);
        assertThat(test.getFallos()).isEqualTo(1);
    }

    @Test
    void guardarRespuesta_rechaza_test_ya_completado() {
        TestRealizado test = testConDatos(5L, 10, 5, 15, true);
        when(testRealizadoRepository.findById(5L)).thenReturn(Optional.of(test));

        assertThatThrownBy(() -> testService.guardarRespuesta(5L, 1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("completado");
    }

    // obtenerEstadisticas
    @Test
    void obtenerEstadisticas_calcula_mejor_puntuacion_y_fecha_por_tema() {
        Usuario alumno = new Usuario();
        Tema tema = temaConId(1L, "Señales");

        TestRealizado t1 = testConDatos(10L, 6, 4, 10, true);
        t1.setTema(tema);
        t1.setPuntuacionTotal(60);
        t1.setFechaFin(LocalDateTime.of(2024, 3, 10, 12, 0));

        TestRealizado t2 = testConDatos(11L, 8, 2, 10, true);
        t2.setTema(tema);
        t2.setPuntuacionTotal(80);
        t2.setFechaFin(LocalDateTime.of(2024, 6, 15, 9, 0));

        when(testRealizadoRepository.countTestsCompletadosByAlumno(alumno)).thenReturn(2L);
        when(testRealizadoRepository.sumAciertosByAlumno(alumno)).thenReturn(14);
        when(testRealizadoRepository.sumFallosByAlumno(alumno)).thenReturn(6);
        when(testRealizadoRepository.findByAlumnoAndCompletadoTrue(alumno)).thenReturn(List.of(t1, t2));

        EstadisticasDTO stats = testService.obtenerEstadisticas(alumno);

        EstadisticasDTO.TemaEstadistica senales = stats.getEstadisticasPorTema().get("Señales");
        assertThat(senales).isNotNull();
        assertThat(senales.getMejorPuntuacion()).isEqualTo(80);
        assertThat(senales.getFechaMejorPuntuacion()).isEqualTo("15/06/2024");
    }

    @Test
    void obtenerEstadisticas_calcula_media_de_puntuaciones_individuales() {
        Usuario alumno = new Usuario();
        Tema tema = temaConId(2L, "Normas");

        TestRealizado t1 = testConDatos(20L, 6, 4, 10, true);
        t1.setTema(tema);
        t1.setPuntuacionTotal(60);
        t1.setFechaFin(LocalDateTime.now());

        TestRealizado t2 = testConDatos(21L, 8, 2, 10, true);
        t2.setTema(tema);
        t2.setPuntuacionTotal(80);
        t2.setFechaFin(LocalDateTime.now());

        when(testRealizadoRepository.countTestsCompletadosByAlumno(alumno)).thenReturn(2L);
        when(testRealizadoRepository.sumAciertosByAlumno(alumno)).thenReturn(14);
        when(testRealizadoRepository.sumFallosByAlumno(alumno)).thenReturn(6);
        when(testRealizadoRepository.findByAlumnoAndCompletadoTrue(alumno)).thenReturn(List.of(t1, t2));

        EstadisticasDTO stats = testService.obtenerEstadisticas(alumno);

        assertThat(stats.getEstadisticasPorTema().get("Normas").getMediaAciertos()).isEqualTo(70.0);
    }

    @Test
    void obtenerEstadisticas_excluye_tests_aleatorios_del_calculo_por_tema() {
        Usuario alumno = new Usuario();

        TestRealizado testAleatorio = testConDatos(30L, 7, 3, 10, true);
        testAleatorio.setTema(null);
        testAleatorio.setPuntuacionTotal(70);
        testAleatorio.setFechaFin(LocalDateTime.now());

        when(testRealizadoRepository.countTestsCompletadosByAlumno(alumno)).thenReturn(1L);
        when(testRealizadoRepository.sumAciertosByAlumno(alumno)).thenReturn(7);
        when(testRealizadoRepository.sumFallosByAlumno(alumno)).thenReturn(3);
        when(testRealizadoRepository.findByAlumnoAndCompletadoTrue(alumno))
                .thenReturn(List.of(testAleatorio));

        EstadisticasDTO stats = testService.obtenerEstadisticas(alumno);

        assertThat(stats.getEstadisticasPorTema()).isEmpty();
        assertThat(stats.getTotalAciertos()).isEqualTo(7);
    }

    // Helpers
    private TestRealizado testConDatos(Long id, int aciertos, int fallos, int total, boolean completado) {
        TestRealizado t = new TestRealizado();
        t.setId(id);
        t.setAciertos(aciertos);
        t.setFallos(fallos);
        t.setTotalPreguntas(total);
        t.setCompletado(completado);
        t.setFechaInicio(LocalDateTime.now());
        return t;
    }

    private Pregunta preguntaConId(Long id) {
        Pregunta p = new Pregunta();
        p.setId(id);
        return p;
    }

    private Respuesta respuestaConId(Long id, boolean correcta) {
        Respuesta r = new Respuesta();
        r.setId(id);
        r.setCorrecta(correcta);
        return r;
    }

    private Tema temaConId(Long id, String nombre) {
        Tema t = new Tema();
        t.setId(id);
        t.setNombre(nombre);
        return t;
    }
}
