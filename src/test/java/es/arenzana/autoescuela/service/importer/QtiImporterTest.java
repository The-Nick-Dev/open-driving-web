package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests del importador QTI 1.x (Blackboard, WebCT, ExamView con exportación QTI).
 *
 * El formato QTI es más complejo que Moodle XML porque la respuesta correcta
 * no está directamente en el elemento, sino en un bloque <resprocessing>
 * separado que indica qué opción da puntuación positiva.
 */
class QtiImporterTest {

    private QtiImporter importer;

    @BeforeEach
    void setUp() {
        importer = new QtiImporter();
    }

    @Test
    void importa_pregunta_identificando_correcta_por_score_positivo() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <questestinterop>
                  <item ident="Q1" title="Velocidad">
                    <presentation>
                      <material>
                        <mattext>¿Velocidad máxima en autopista?</mattext>
                      </material>
                      <response_lid ident="resp" rcardinality="Single">
                        <render_choice>
                          <response_label ident="A">
                            <material><mattext>120 km/h</mattext></material>
                          </response_label>
                          <response_label ident="B">
                            <material><mattext>100 km/h</mattext></material>
                          </response_label>
                          <response_label ident="C">
                            <material><mattext>90 km/h</mattext></material>
                          </response_label>
                        </render_choice>
                      </response_lid>
                    </presentation>
                    <resprocessing>
                      <respcondition continue="No">
                        <conditionvar>
                          <varequal respident="resp">A</varequal>
                        </conditionvar>
                        <setvar action="Set" varname="SCORE">100</setvar>
                      </respcondition>
                    </resprocessing>
                  </item>
                </questestinterop>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).hasSize(1);
        PreguntaImportada p = resultado.get(0);
        assertThat(p.getTexto()).isEqualTo("¿Velocidad máxima en autopista?");
        assertThat(p.getRespuestas()).hasSize(3);

        PreguntaImportada.RespuestaImportada correcta = p.getRespuestas().stream()
                .filter(r -> r.getTexto().equals("120 km/h"))
                .findFirst()
                .orElseThrow();
        assertThat(correcta.isCorrecta()).isTrue();

        p.getRespuestas().stream()
                .filter(r -> !r.getTexto().equals("120 km/h"))
                .forEach(r -> assertThat(r.isCorrecta()).isFalse());
    }

    @Test
    void detecta_correcta_por_atributo_correct_en_response_label() throws Exception {
        String xml = """
                <questestinterop>
                  <item ident="Q1">
                    <presentation>
                      <material><mattext>¿Capital de España?</mattext></material>
                      <response_lid ident="resp">
                        <render_choice>
                          <response_label ident="A" correct="true">
                            <material><mattext>Madrid</mattext></material>
                          </response_label>
                          <response_label ident="B">
                            <material><mattext>Barcelona</mattext></material>
                          </response_label>
                        </render_choice>
                      </response_lid>
                    </presentation>
                  </item>
                </questestinterop>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRespuestas().get(0).isCorrecta()).isTrue();
        assertThat(resultado.get(0).getRespuestas().get(0).getTexto()).isEqualTo("Madrid");
    }

    @Test
    void ignora_varequal_dentro_de_not_no_lo_cuenta_como_correcta() throws Exception {
        String xml = """
                <questestinterop>
                  <item ident="Q1">
                    <presentation>
                      <material><mattext>Pregunta</mattext></material>
                      <response_lid ident="resp">
                        <render_choice>
                          <response_label ident="A">
                            <material><mattext>Opción A</mattext></material>
                          </response_label>
                          <response_label ident="B">
                            <material><mattext>Opción B</mattext></material>
                          </response_label>
                        </render_choice>
                      </response_lid>
                    </presentation>
                    <resprocessing>
                      <respcondition>
                        <conditionvar>
                          <not><varequal respident="resp">A</varequal></not>
                        </conditionvar>
                        <setvar varname="SCORE">100</setvar>
                      </respcondition>
                    </resprocessing>
                  </item>
                </questestinterop>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    @Test
    void ignora_pregunta_sin_respuestas_suficientes() throws Exception {
        String xml = """
                <questestinterop>
                  <item ident="Q1">
                    <presentation>
                      <material><mattext>Solo una opción</mattext></material>
                      <response_lid ident="resp">
                        <render_choice>
                          <response_label ident="A" correct="true">
                            <material><mattext>Única opción</mattext></material>
                          </response_label>
                        </render_choice>
                      </response_lid>
                    </presentation>
                  </item>
                </questestinterop>
                """;

        List<PreguntaImportada> resultado = importer.importar(toStream(xml));

        assertThat(resultado).isEmpty();
    }

    private ByteArrayInputStream toStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }
}
