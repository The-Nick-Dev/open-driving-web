package es.arenzana.autoescuela.service;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import es.arenzana.autoescuela.model.Pregunta;
import es.arenzana.autoescuela.model.Respuesta;
import es.arenzana.autoescuela.model.Tema;
import es.arenzana.autoescuela.repository.PreguntaRepository;
import es.arenzana.autoescuela.repository.RespuestaRepository;
import es.arenzana.autoescuela.repository.TemaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de PreguntaService con Mockito.
 *
 * ¿Qué es Mockito?
 *   Una librería que crea objetos "falsos" (mocks) de las dependencias.
 *   Así podemos probar PreguntaService sin necesitar una base de datos real.
 *
 * Anotaciones clave:
 *   @Mock            crea un objeto falso de esa clase/interfaz
 *   @InjectMocks     crea el objeto real (PreguntaService) e inyecta los @Mock
 *   when(...).thenReturn(...)  programa el comportamiento del mock
 *   verify(mock).metodo(...)  comprueba que se llamó ese método
 *   ArgumentCaptor   captura los argumentos con que se llamó a un mock
 */
@ExtendWith(MockitoExtension.class)  // activa Mockito sin cargar Spring
class PreguntaServiceTest {

    // Objetos falsos
    @Mock PreguntaRepository preguntaRepository;
    @Mock RespuestaRepository respuestaRepository;
    @Mock TemaRepository temaRepository;

    // Objeto REAL con los mocks inyectados
    @InjectMocks PreguntaService preguntaService;

    // crearPregunta
    @Test
    void crearPregunta_persiste_la_pregunta_y_sus_tres_respuestas() {
        Tema tema = temaConId(1L, "Señales");
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(preguntaRepository.save(any(Pregunta.class))).thenAnswer(inv -> {
            Pregunta p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        List<PreguntaService.RespuestaData> respuestas = List.of(
                rd("¿Qué indica el semáforo rojo?", true),
                rd("Acelera",                                false),
                rd("Gira",                                   false)
        );

        Pregunta resultado = preguntaService.crearPregunta(
                "¿Qué indica el semáforo rojo?", 1L, "Debes detenerte.", respuestas);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getTema().getNombre()).isEqualTo("Señales");
        assertThat(resultado.getActivo()).isTrue();

        verify(respuestaRepository, times(3)).save(any(Respuesta.class));
    }

    @Test
    void crearPregunta_lanza_excepcion_si_el_tema_no_existe() {
        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                preguntaService.crearPregunta("Texto", 99L, null, List.of())
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tema no encontrado");

        verify(preguntaRepository, never()).save(any());
    }

    // eliminarPregunta
    @Test
    void eliminarPregunta_pone_activo_a_false_sin_borrar_el_registro() {
        Pregunta pregunta = new Pregunta();
        pregunta.setId(5L);
        pregunta.setActivo(true);

        when(preguntaRepository.findById(5L)).thenReturn(Optional.of(pregunta));
        when(preguntaRepository.save(any())).thenReturn(pregunta);

        preguntaService.eliminarPregunta(5L);

        ArgumentCaptor<Pregunta> captor = ArgumentCaptor.forClass(Pregunta.class);
        verify(preguntaRepository).save(captor.capture());

        assertThat(captor.getValue().getActivo()).isFalse();
        verify(preguntaRepository, never()).deleteById(any());
    }

    // importarPreguntas
    @Test
    void importarPreguntas_importa_todas_y_devuelve_el_conteo() {
        Tema tema = temaConId(2L, "Normas");
        when(temaRepository.findById(2L)).thenReturn(Optional.of(tema));

        when(preguntaRepository.save(any())).thenAnswer(inv -> {
            Pregunta p = inv.getArgument(0);
            p.setId((long) (Math.random() * 1000 + 1));
            return p;
        });

        List<PreguntaImportada> importadas = List.of(
                pi("¿Qué es una señal de prohibición?"),
                pi("¿Qué es una señal de peligro?"),
                pi("¿Qué es una señal de obligación?")
        );

        int importadas2 = preguntaService.importarPreguntas(importadas, 2L);

        assertThat(importadas2).isEqualTo(3);
        verify(preguntaRepository, times(3)).save(any(Pregunta.class));
        verify(respuestaRepository, times(6)).save(any(Respuesta.class));
    }

    @Test
    void importarPreguntas_con_tema_inexistente_lanza_excepcion() {
        when(temaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                preguntaService.importarPreguntas(List.of(pi("Pregunta")), 999L)
        ).isInstanceOf(RuntimeException.class);
    }

    // Helpers
    private Tema temaConId(Long id, String nombre) {
        Tema t = new Tema();
        t.setId(id);
        t.setNombre(nombre);
        return t;
    }

    private PreguntaService.RespuestaData rd(String texto, boolean correcta) {
        PreguntaService.RespuestaData rd = new PreguntaService.RespuestaData();
        rd.setTexto(texto);
        rd.setCorrecta(correcta);
        return rd;
    }

    private PreguntaImportada pi(String texto) {
        return new PreguntaImportada(
                texto,
                "Explicación de prueba",
                List.of(
                        new PreguntaImportada.RespuestaImportada("Respuesta correcta", true),
                        new PreguntaImportada.RespuestaImportada("Respuesta incorrecta", false)
                )
        );
    }
}
