package es.arenzana.autoescuela.service.importer;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import java.io.InputStream;
import java.util.List;

public interface QuestionImporter {
    List<PreguntaImportada> importar(InputStream inputStream) throws Exception;
}
