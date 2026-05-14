package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Importa preguntas desde Hot Potatoes JQuiz.
 * Soporta tanto .jqz como XML.
 */
public class HotPotatoesImporter implements QuestionImporter {

    @Override
    public List<PreguntaImportada> importar(InputStream is) throws Exception {
        byte[] bytes = is.readAllBytes();

        InputStream xmlStream;
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes));
            xmlStream = new ByteArrayInputStream(gzip.readAllBytes());
        } catch (IOException e) {
            xmlStream = new ByteArrayInputStream(bytes);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setExpandEntityReferences(false);
        Document doc = factory.newDocumentBuilder().parse(xmlStream);
        doc.getDocumentElement().normalize();

        List<PreguntaImportada> result = new ArrayList<>();
        NodeList records = doc.getElementsByTagName("question-record");

        for (int i = 0; i < records.getLength(); i++) {
            Element qr = (Element) records.item(i);
            String texto = strip(text(qr, "question"));
            if (texto.isBlank()) continue;

            String explicacion = strip(text(qr, "feedback"));

            List<PreguntaImportada.RespuestaImportada> respuestas = new ArrayList<>();
            NodeList answers = qr.getElementsByTagName("answer");
            for (int j = 0; j < answers.getLength(); j++) {
                Element a = (Element) answers.item(j);
                String textoR = strip(text(a, "a-text"));
                if (textoR.isBlank()) continue;
                String correct = text(a, "correct");
                boolean correcta = "True".equalsIgnoreCase(correct.trim());
                respuestas.add(new PreguntaImportada.RespuestaImportada(textoR, correcta));
            }

            if (respuestas.size() >= 2 && respuestas.stream().anyMatch(PreguntaImportada.RespuestaImportada::isCorrecta)) {
                result.add(new PreguntaImportada(texto, explicacion, respuestas));
            }
        }
        return result;
    }

    private String text(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent();
    }

    private String strip(String html) {
        return MoodleXmlImporter.strip(html);
    }
}
