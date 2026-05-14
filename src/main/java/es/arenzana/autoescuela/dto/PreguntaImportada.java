package es.arenzana.autoescuela.dto;

import java.util.List;

public class PreguntaImportada {

    private final String texto;
    private final String explicacion;
    private final List<RespuestaImportada> respuestas;

    public PreguntaImportada(String texto, String explicacion, List<RespuestaImportada> respuestas) {
        this.texto = texto;
        this.explicacion = explicacion;
        this.respuestas = respuestas;
    }

    public String getTexto() { return texto; }
    public String getExplicacion() { return explicacion; }
    public List<RespuestaImportada> getRespuestas() { return respuestas; }

    public static class RespuestaImportada {
        private final String texto;
        private final boolean correcta;

        public RespuestaImportada(String texto, boolean correcta) {
            this.texto = texto;
            this.correcta = correcta;
        }

        public String getTexto() { return texto; }
        public boolean isCorrecta() { return correcta; }
    }
}
