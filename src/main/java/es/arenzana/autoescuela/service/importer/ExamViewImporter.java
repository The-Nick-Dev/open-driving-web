package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * Importa preguntas desde ExamView/TestGen XML.
 * Soporta el formato nativo ExamView y cae en QTI como fallback.
 */
public class ExamViewImporter implements QuestionImporter {

    private final QtiImporter qtiImporter = new QtiImporter();

    @Override
    public List<PreguntaImportada> importar(InputStream is) throws Exception {
        byte[] bytes = is.readAllBytes();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setExpandEntityReferences(false);
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
            doc.getDocumentElement().normalize();

            String root = doc.getDocumentElement().getTagName();
            if (root.toLowerCase().contains("examview") || root.equalsIgnoreCase("testgen")
                    || root.equalsIgnoreCase("bank") || root.equalsIgnoreCase("ExamViewBank")) {
                List<PreguntaImportada> nativo = parseExamViewNativo(doc);
                if (!nativo.isEmpty()) return nativo;
            }
            // Fallback a QTI
            return qtiImporter.parseDocument(doc);
        } catch (Exception e) {
            return qtiImporter.importar(new ByteArrayInputStream(bytes));
        }
    }

    private List<PreguntaImportada> parseExamViewNativo(Document doc) {
        List<PreguntaImportada> result = new ArrayList<>();

        // Intentar distintos nombres de elemento para preguntas
        NodeList questions = doc.getElementsByTagName("question");
        if (questions.getLength() == 0) questions = doc.getElementsByTagName("Question");

        for (int i = 0; i < questions.getLength(); i++) {
            Element q = (Element) questions.item(i);
            String type = q.getAttribute("type").toUpperCase();
            // Solo múltiple opción: MC, MULT, MULTIPLE_CHOICE o sin tipo especificado
            if (!type.isBlank() && !type.startsWith("MC") && !type.startsWith("MULT")) continue;

            String texto = strip(firstOf(q, "stem", "text", "Text", "Q_Text", "question_text"));
            if (texto.isBlank()) continue;

            String explicacion = strip(firstOf(q, "rationale", "Rationale", "explanation", "feedback", "F_Text"));

            // Respuesta correcta en elementos tipo <answer>a</answer> o <correctAnswer letter="b"/>
            String correctLetter = strip(firstOf(q, "answer", "Answer", "correctAnswer", "CorrectAnswer")).toLowerCase();
            String correctAttr = "";
            NodeList correctNodes = q.getElementsByTagName("correctAnswer");
            if (correctNodes.getLength() == 0) correctNodes = q.getElementsByTagName("correct_answer");
            if (correctNodes.getLength() > 0) {
                correctAttr = ((Element) correctNodes.item(0)).getAttribute("letter").toLowerCase();
                if (correctAttr.isBlank())
                    correctAttr = ((Element) correctNodes.item(0)).getAttribute("id").toLowerCase();
            }
            String correct = correctAttr.isBlank() ? correctLetter : correctAttr;

            List<PreguntaImportada.RespuestaImportada> respuestas = new ArrayList<>();

            // Formato A: <choices><choice id="a" correct="true">texto</choice></choices>
            NodeList choices = q.getElementsByTagName("choice");
            if (choices.getLength() == 0) choices = q.getElementsByTagName("Choice");
            if (choices.getLength() > 0) {
                for (int j = 0; j < choices.getLength(); j++) {
                    Element c = (Element) choices.item(j);
                    String t = strip(c.getTextContent());
                    if (t.isBlank()) continue;
                    String id = c.getAttribute("id").toLowerCase();
                    if (id.isBlank()) id = c.getAttribute("letter").toLowerCase();
                    boolean correcta = "true".equalsIgnoreCase(c.getAttribute("correct"))
                            || (!id.isBlank() && id.equals(correct));
                    respuestas.add(new PreguntaImportada.RespuestaImportada(t, correcta));
                }
            } else {
                // Formato B: <answer num="0" correct="True"><A_Text>texto</A_Text></answer>
                // o        : <Answer letter="A"><text>texto</text></Answer>
                NodeList answers = q.getElementsByTagName("answer");
                if (answers.getLength() == 0) answers = q.getElementsByTagName("Answer");
                for (int j = 0; j < answers.getLength(); j++) {
                    Element a = (Element) answers.item(j);
                    // Saltar el <answer> que solo contiene la letra correcta (nodo de texto único)
                    if (a.getChildNodes().getLength() == 1
                            && a.getFirstChild().getNodeType() == Node.TEXT_NODE
                            && a.getFirstChild().getTextContent().trim().length() <= 2) continue;

                    String t = strip(firstOf(a, "A_Text", "text", "Text", "answer_text"));
                    if (t.isBlank()) t = strip(a.getTextContent());
                    if (t.isBlank()) continue;

                    String id = a.getAttribute("id").toLowerCase();
                    if (id.isBlank()) id = a.getAttribute("letter").toLowerCase();
                    if (id.isBlank()) id = String.valueOf((char) ('a' + j));

                    boolean correcta = "true".equalsIgnoreCase(a.getAttribute("correct"))
                            || id.equals(correct);
                    respuestas.add(new PreguntaImportada.RespuestaImportada(t, correcta));
                }
            }

            if (respuestas.size() >= 2 && respuestas.stream().anyMatch(PreguntaImportada.RespuestaImportada::isCorrecta)) {
                result.add(new PreguntaImportada(texto, explicacion, respuestas));
            }
        }
        return result;
    }

    private String firstOf(Element parent, String... tags) {
        for (String tag : tags) {
            NodeList nodes = parent.getElementsByTagName(tag);
            if (nodes.getLength() > 0) {
                String val = nodes.item(0).getTextContent();
                if (val != null && !val.isBlank()) return val;
            }
        }
        return "";
    }

    private String strip(String html) {
        return MoodleXmlImporter.strip(html);
    }
}
