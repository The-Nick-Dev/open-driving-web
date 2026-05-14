package es.arenzana.autoescuela.service;

import es.arenzana.autoescuela.dto.PreguntaImportada;
import es.arenzana.autoescuela.model.Pregunta;
import es.arenzana.autoescuela.model.Respuesta;
import es.arenzana.autoescuela.model.Tema;
import es.arenzana.autoescuela.repository.PreguntaRepository;
import es.arenzana.autoescuela.repository.RespuestaRepository;
import es.arenzana.autoescuela.repository.TemaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;
    private final TemaRepository temaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository,
                           RespuestaRepository respuestaRepository,
                           TemaRepository temaRepository) {
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
        this.temaRepository = temaRepository;
    }

    public List<Pregunta> listarTodas() {
        return preguntaRepository.findByActivoTrue();
    }

    public List<Pregunta> listarPorTema(Long temaId) {
        return preguntaRepository.findByTemaIdAndActivoTrue(temaId);
    }

    public Pregunta obtenerPorId(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
    }

    @Transactional
    public Pregunta crearPregunta(String texto, Long temaId, String explicacion, List<RespuestaData> respuestasData) {
        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        Pregunta pregunta = new Pregunta();
        pregunta.setTexto(texto);
        pregunta.setTema(tema);
        pregunta.setExplicacion(explicacion);
        pregunta.setActivo(true);

        Pregunta saved = preguntaRepository.save(pregunta);

        // Crear respuestas
        for (int i = 0; i < respuestasData.size(); i++) {
            RespuestaData rd = respuestasData.get(i);
            Respuesta respuesta = new Respuesta();
            respuesta.setPregunta(saved);
            respuesta.setTexto(rd.getTexto());
            respuesta.setCorrecta(rd.isCorrecta());
            respuesta.setOrden(i);
            respuestaRepository.save(respuesta);
        }

        return saved;
    }

    @Transactional
    public Pregunta actualizarPregunta(Long id, String texto, Long temaId, String explicacion, List<RespuestaData> respuestasData) {
        Pregunta pregunta = obtenerPorId(id);
        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        pregunta.setTexto(texto);
        pregunta.setTema(tema);
        pregunta.setExplicacion(explicacion);

        // Eliminar respuestas antiguas y limpiar la colección en memoria
        respuestaRepository.deleteByPreguntaId(id);
        pregunta.getRespuestas().clear();

        // Crear nuevas respuestas
        for (int i = 0; i < respuestasData.size(); i++) {
            RespuestaData rd = respuestasData.get(i);
            Respuesta respuesta = new Respuesta();
            respuesta.setPregunta(pregunta);
            respuesta.setTexto(rd.getTexto());
            respuesta.setCorrecta(rd.isCorrecta());
            respuesta.setOrden(i);
            respuestaRepository.save(respuesta);
        }

        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public void eliminarPregunta(Long id) {
        Pregunta pregunta = obtenerPorId(id);
        pregunta.setActivo(false);
        preguntaRepository.save(pregunta);
    }

    @Transactional
    public void eliminarPreguntaFisicamente(Long id) {
        respuestaRepository.deleteByPreguntaId(id);
        preguntaRepository.deleteById(id);
    }

    @Transactional
    public int importarPreguntas(List<PreguntaImportada> preguntasImportadas, Long temaId) {
        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        int count = 0;
        for (PreguntaImportada pi : preguntasImportadas) {
            try {
                Pregunta pregunta = new Pregunta();
                pregunta.setTexto(pi.getTexto());
                pregunta.setTema(tema);
                pregunta.setExplicacion(pi.getExplicacion());
                pregunta.setActivo(true);
                Pregunta saved = preguntaRepository.save(pregunta);
                for (int i = 0; i < pi.getRespuestas().size(); i++) {
                    PreguntaImportada.RespuestaImportada ri = pi.getRespuestas().get(i);
                    Respuesta r = new Respuesta();
                    r.setPregunta(saved);
                    r.setTexto(ri.getTexto());
                    r.setCorrecta(ri.isCorrecta());
                    r.setOrden(i);
                    respuestaRepository.save(r);
                }
                count++;
            } catch (Exception ignored) {}
        }
        return count;
    }

    // Clase auxiliar para datos de respuesta
    public static class RespuestaData {
        private String texto;
        private boolean correcta;

        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
        public boolean isCorrecta() { return correcta; }
        public void setCorrecta(boolean correcta) { this.correcta = correcta; }
    }
}