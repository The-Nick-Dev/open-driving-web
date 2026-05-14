package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Importa preguntas desde paquetes Blackboard.
 * Soporta ZIP (paquete IMS con archivos QTI XML) y QTI XML directo.
 */
public class BlackboardImporter implements QuestionImporter {

    private final QtiImporter qtiImporter = new QtiImporter();

    @Override
    public List<PreguntaImportada> importar(InputStream is) throws Exception {
        byte[] bytes = is.readAllBytes();

        if (isZip(bytes)) {
            return importarDesdeZip(bytes);
        }
        return qtiImporter.importar(new ByteArrayInputStream(bytes));
    }

    private List<PreguntaImportada> importarDesdeZip(byte[] bytes) throws Exception {
        List<PreguntaImportada> result = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
                if (!name.endsWith(".xml") || name.contains("imsmanifest") || name.contains("manifest")) {
                    zis.closeEntry();
                    continue;
                }
                try {
                    byte[] xml = zis.readAllBytes();
                    Document doc = builder.parse(new ByteArrayInputStream(xml));
                    doc.getDocumentElement().normalize();
                    result.addAll(qtiImporter.parseDocument(doc));
                } catch (Exception ignored) {}
                zis.closeEntry();
            }
        }
        return result;
    }

    private boolean isZip(byte[] bytes) {
        return bytes.length > 3
            && bytes[0] == 0x50 && bytes[1] == 0x4B
            && bytes[2] == 0x03 && bytes[3] == 0x04;
    }
}
