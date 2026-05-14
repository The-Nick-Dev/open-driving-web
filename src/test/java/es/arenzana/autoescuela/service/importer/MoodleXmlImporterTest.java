package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests UNITARIOS del importador Moodle XML.
 *
 * ¿Qué es un test unitario?
 *   Prueba UNA sola clase en total aislamiento, sin Spring, sin base de datos,
 *   sin red. Es el tipo de test más rápido y más fácil de entender.
 *
 * Estructura de cada test (patrón AAA):
 *   - Arrange   preparo los datos de entrada
 *   - Act       llamo al método que quiero probar
 *   - Assert    verifico que el resultado es el esperado
 */
class MoodleXmlImporterTest {

    private MoodleXmlImporter importer;

    @BeforeEach   // Spring ejecuta este método antes de cada @Test
    void setUp() {
        importer = new MoodleXmlImporter();
    }

    // Casos correctos
    @Test
    void importa_una_pregunta_multichoice_completa() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <quiz>
                  <question type="multichoice">
                    <questiontext format="html">
                      <text>¿Velocidad máxima en autopista?</text>
                    </questiontext>
                    <generalfeedback>
                      <text>El límite legal es 120 km/h.</text>
                    </generalfeedback>
                    <answer fraction="100"><text>120 km/h</text></answer>
                    <answer fraction="0"><text>100 km/h</text></answer>
                    <answer fraction="0"><text>90 km/h</text></answer>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).hasSize(1);

        PreguntaImportada p = resultado.get(0);
        assertThat(p.getTexto()).isEqualTo("¿Velocidad máxima en autopista?");
        assertThat(p.getExplicacion()).isEqualTo("El límite legal es 120 km/h.");
        assertThat(p.getRespuestas()).hasSize(3);

        long correctas = p.getRespuestas().stream()
                .filter(PreguntaImportada.RespuestaImportada::isCorrecta)
                .count();
        assertThat(correctas).isEqualTo(1);

        assertThat(p.getRespuestas().get(0).getTexto()).isEqualTo("120 km/h");
        assertThat(p.getRespuestas().get(0).isCorrecta()).isTrue();
    }

    @Test
    void importa_multiples_preguntas_del_mismo_archivo() throws Exception {
        String xml = """
                <quiz>
                  <question type="multichoice">
                    <questiontext><text>Pregunta 1</text></questiontext>
                    <answer fraction="100"><text>Correcta</text></answer>
                    <answer fraction="0"><text>Incorrecta</text></answer>
                  </question>
                  <question type="multichoice">
                    <questiontext><text>Pregunta 2</text></questiontext>
                    <answer fraction="100"><text>Correcta</text></answer>
                    <answer fraction="0"><text>Incorrecta</text></answer>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTexto()).isEqualTo("Pregunta 1");
        assertThat(resultado.get(1).getTexto()).isEqualTo("Pregunta 2");
    }

    @Test
    void elimina_etiquetas_html_del_texto() throws Exception {
        String xml = """
                <quiz>
                  <question type="multichoice">
                    <questiontext><text><![CDATA[<p>Pregunta con <strong>negrita</strong></p>]]></text></questiontext>
                    <answer fraction="100"><text>Opción A</text></answer>
                    <answer fraction="0"><text>Opción B</text></answer>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTexto()).isEqualTo("Pregunta con negrita");
        assertThat(resultado.get(0).getTexto()).doesNotContain("<p>", "<strong>");
    }

    @Test
    void ignora_preguntas_de_tipo_no_multichoice() throws Exception {
        String xml = """
                <quiz>
                  <question type="shortanswer">
                    <questiontext><text>Escribe la respuesta</text></questiontext>
                  </question>
                  <question type="truefalse">
                    <questiontext><text>Verdadero o falso</text></questiontext>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    @Test
    void ignora_pregunta_sin_ninguna_respuesta_correcta() throws Exception {
        String xml = """
                <quiz>
                  <question type="multichoice">
                    <questiontext><text>Sin respuesta correcta</text></questiontext>
                    <answer fraction="0"><text>Opción A</text></answer>
                    <answer fraction="0"><text>Opción B</text></answer>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    @Test
    void ignora_pregunta_con_una_sola_respuesta() throws Exception {
        String xml = """
                <quiz>
                  <question type="multichoice">
                    <questiontext><text>Solo una opción</text></questiontext>
                    <answer fraction="100"><text>Única respuesta</text></answer>
                  </question>
                </quiz>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    @Test
    void archivo_vacio_devuelve_lista_vacia() throws Exception {
        String xml = "<?xml version=\"1.0\"?><quiz></quiz>";

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    // Helper
    private ByteArrayInputStream toStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }
}
