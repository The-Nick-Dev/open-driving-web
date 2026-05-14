package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import org.w3c.dom.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

/**
 * Compatible con exportaciones de Blackboard, WebCT y ExamView (como en Moodle).
 */
public class QtiImporter implements QuestionImporter {

    @Override
    public List<PreguntaImportada> importar(InputStream is) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setExpandEntityReferences(false);
        Document doc = factory.newDocumentBuilder().parse(is);
        doc.getDocumentElement().normalize();
        return parseDocument(doc);
    }

    List<PreguntaImportada> parseDocument(Document doc) {
        List<PreguntaImportada> result = new ArrayList<>();
        NodeList items = doc.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            try {
                PreguntaImportada p = parseItem((Element) items.item(i));
                if (p != null) result.add(p);
            } catch (Exception ignored) {}
        }
        return result;
    }

    private PreguntaImportada parseItem(Element item) {
        String texto = "";
        NodeList presentations = item.getElementsByTagName("presentation");
        if (presentations.getLength() > 0) {
            Element pres = (Element) presentations.item(0);
            NodeList materials = pres.getElementsByTagName("material");
            for (int i = 0; i < materials.getLength(); i++) {
                Element mat = (Element) materials.item(i);
                // Ignorar materiales dentro de response_label
                if (isInsideResponseLabel(mat)) continue;
                NodeList mattext = mat.getElementsByTagName("mattext");
                if (mattext.getLength() > 0) {
                    texto = strip(mattext.item(0).getTextContent());
                    if (!texto.isBlank()) break;
                }
            }
        }
        if (texto.isBlank()) return null;

        Map<String, String> opciones = new LinkedHashMap<>();
        NodeList labels = item.getElementsByTagName("response_label");
        for (int i = 0; i < labels.getLength(); i++) {
            Element label = (Element) labels.item(i);
            String ident = label.getAttribute("ident");
            NodeList mattext = label.getElementsByTagName("mattext");
            if (mattext.getLength() > 0) {
                String t = strip(mattext.item(0).getTextContent());
                if (!t.isBlank()) opciones.put(ident, t);
            }
        }
        if (opciones.size() < 2) return null;

        Set<String> correctas = new HashSet<>();
        for (int i = 0; i < labels.getLength(); i++) {
            Element label = (Element) labels.item(i);
            if ("true".equalsIgnoreCase(label.getAttribute("correct"))) {
                correctas.add(label.getAttribute("ident"));
            }
        }

        NodeList conditions = item.getElementsByTagName("respcondition");
        for (int i = 0; i < conditions.getLength(); i++) {
            Element cond = (Element) conditions.item(i);
            if (!givesPoints(cond)) continue;
            NodeList varequals = cond.getElementsByTagName("varequal");
            for (int j = 0; j < varequals.getLength(); j++) {
                Node ve = varequals.item(j);
                if (!isInsideNot(ve, cond)) {
                    correctas.add(ve.getTextContent().trim());
                }
            }
        }
        if (correctas.isEmpty()) return null;

        String explicacion = "";
        NodeList feedbacks = item.getElementsByTagName("itemfeedback");
        if (feedbacks.getLength() > 0) {
            NodeList mattext = ((Element) feedbacks.item(0)).getElementsByTagName("mattext");
            if (mattext.getLength() > 0) explicacion = strip(mattext.item(0).getTextContent());
        }

        List<PreguntaImportada.RespuestaImportada> respuestas = new ArrayList<>();
        for (Map.Entry<String, String> e : opciones.entrySet()) {
            respuestas.add(new PreguntaImportada.RespuestaImportada(e.getValue(), correctas.contains(e.getKey())));
        }

        if (respuestas.stream().noneMatch(PreguntaImportada.RespuestaImportada::isCorrecta)) return null;
        return new PreguntaImportada(texto, explicacion, respuestas);
    }

    private boolean givesPoints(Element respcondition) {
        NodeList setvars = respcondition.getElementsByTagName("setvar");
        for (int i = 0; i < setvars.getLength(); i++) {
            try {
                double val = Double.parseDouble(setvars.item(i).getTextContent().trim());
                if (val > 0) return true;
            } catch (NumberFormatException ignored) {}
        }
        return false;
    }

    private boolean isInsideNot(Node node, Element boundary) {
        Node parent = node.getParentNode();
        while (parent != null && !parent.equals(boundary)) {
            if ("not".equalsIgnoreCase(parent.getNodeName())) return true;
            parent = parent.getParentNode();
        }
        return false;
    }

    private boolean isInsideResponseLabel(Node node) {
        Node parent = node.getParentNode();
        while (parent != null) {
            if ("response_label".equalsIgnoreCase(parent.getNodeName())) return true;
            parent = parent.getParentNode();
        }
        return false;
    }

    private String strip(String html) {
        return MoodleXmlImporter.strip(html);
    }
}
