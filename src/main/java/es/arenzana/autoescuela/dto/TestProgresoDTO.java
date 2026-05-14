package es.arenzana.autoescuela.dto;

import java.util.List;

public class TestProgresoDTO {
    private Long testId;
    private String temaNombre;
    private int totalPreguntas;
    private int preguntasRespondidas;
    private boolean completado;
    private int aciertos;
    private int fallos;
    private int puntuacion;
    private List<PreguntaConRespuesta> preguntas;

    public static class PreguntaConRespuesta {
        private PreguntaSimple pregunta;
        private List<RespuestaSimple> respuestas;
        private Long respuestaSeleccionadaId;
        private Boolean fueCorrecta;

        public static class PreguntaSimple {
            private Long id;
            private String texto;
            private String explicacion;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getTexto() { return texto; }
            public void setTexto(String texto) { this.texto = texto; }
            public String getExplicacion() { return explicacion; }
            public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
        }

        public static class RespuestaSimple {
            private Long id;
            private String texto;
            private Boolean esCorrecta;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getTexto() { return texto; }
            public void setTexto(String texto) { this.texto = texto; }
            public Boolean getEsCorrecta() { return esCorrecta; }
            public void setEsCorrecta(Boolean esCorrecta) { this.esCorrecta = esCorrecta; }
        }

        public PreguntaSimple getPregunta() { return pregunta; }
        public void setPregunta(PreguntaSimple pregunta) { this.pregunta = pregunta; }
        public List<RespuestaSimple> getRespuestas() { return respuestas; }
        public void setRespuestas(List<RespuestaSimple> respuestas) { this.respuestas = respuestas; }
        public Long getRespuestaSeleccionadaId() { return respuestaSeleccionadaId; }
        public void setRespuestaSeleccionadaId(Long id) { this.respuestaSeleccionadaId = id; }
        public Boolean getFueCorrecta() { return fueCorrecta; }
        public void setFueCorrecta(Boolean fueCorrecta) { this.fueCorrecta = fueCorrecta; }
    }

    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }
    public String getTemaNombre() { return temaNombre; }
    public void setTemaNombre(String temaNombre) { this.temaNombre = temaNombre; }
    public int getTotalPreguntas() { return totalPreguntas; }
    public void setTotalPreguntas(int totalPreguntas) { this.totalPreguntas = totalPreguntas; }
    public int getPreguntasRespondidas() { return preguntasRespondidas; }
    public void setPreguntasRespondidas(int n) { this.preguntasRespondidas = n; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    public int getAciertos() { return aciertos; }
    public void setAciertos(int aciertos) { this.aciertos = aciertos; }
    public int getFallos() { return fallos; }
    public void setFallos(int fallos) { this.fallos = fallos; }
    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    public List<PreguntaConRespuesta> getPreguntas() { return preguntas; }
    public void setPreguntas(List<PreguntaConRespuesta> preguntas) { this.preguntas = preguntas; }
}
