package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public class MoodleXmlImporter implements QuestionImporter {

    @Override
    public List<PreguntaImportada> importar(InputStream is) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setExpandEntityReferences(false);
        Document doc = factory.newDocumentBuilder().parse(is);
        doc.getDocumentElement().normalize();

        List<PreguntaImportada> result = new ArrayList<>();
        NodeList questions = doc.getElementsByTagName("question");

        for (int i = 0; i < questions.getLength(); i++) {
            Element q = (Element) questions.item(i);
            if (!"multichoice".equalsIgnoreCase(q.getAttribute("type"))) continue;

            String texto = strip(childText(q, "questiontext", "text"));
            if (texto.isBlank()) continue;

            String explicacion = strip(childText(q, "generalfeedback", "text"));

            List<PreguntaImportada.RespuestaImportada> respuestas = new ArrayList<>();
            NodeList answers = q.getElementsByTagName("answer");
            for (int j = 0; j < answers.getLength(); j++) {
                Element a = (Element) answers.item(j);
                String textoR = strip(firstText(a, "text"));
                if (textoR.isBlank()) continue;
                String fraction = a.getAttribute("fraction");
                boolean correcta = false;
                try { correcta = !fraction.isBlank() && Double.parseDouble(fraction) > 0; }
                catch (NumberFormatException ignored) {}
                respuestas.add(new PreguntaImportada.RespuestaImportada(textoR, correcta));
            }

            if (respuestas.size() >= 2 && respuestas.stream().anyMatch(PreguntaImportada.RespuestaImportada::isCorrecta)) {
                result.add(new PreguntaImportada(texto, explicacion, respuestas));
            }
        }
        return result;
    }

    private String childText(Element parent, String childTag, String grandChildTag) {
        NodeList children = parent.getElementsByTagName(childTag);
        if (children.getLength() == 0) return "";
        return firstText((Element) children.item(0), grandChildTag);
    }

    private String firstText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent();
    }

    static String strip(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", " ")
                   .replace("&amp;", "&").replace("&lt;", "<")
                   .replace("&gt;", ">").replace("&nbsp;", " ")
                   .replace("&quot;", "\"").replace("&#39;", "'")
                   .replaceAll("\\s+", " ").trim();
    }
}
