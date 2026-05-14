package es.arenzana.autoescuela.dto;

public class EstadisticasDTO {
    private long totalTests;
    private int totalAciertos;
    private int totalFallos;
    private double porcentajeAciertos;
    private int totalPreguntasRespondidas;
    
    // Estadísticas por tema
    private java.util.Map<String, TemaEstadistica> estadisticasPorTema;
    
    public static class TemaEstadistica {
        private String temaNombre;
        private int testsRealizados;
        private int aciertos;
        private int fallos;
        private double porcentajeAciertos;
        private int mejorPuntuacion;
        private String fechaMejorPuntuacion;
        private double mediaAciertos;

        public String getTemaNombre() { return temaNombre; }
        public void setTemaNombre(String temaNombre) { this.temaNombre = temaNombre; }
        public int getTestsRealizados() { return testsRealizados; }
        public void setTestsRealizados(int testsRealizados) { this.testsRealizados = testsRealizados; }
        public int getAciertos() { return aciertos; }
        public void setAciertos(int aciertos) { this.aciertos = aciertos; }
        public int getFallos() { return fallos; }
        public void setFallos(int fallos) { this.fallos = fallos; }
        public double getPorcentajeAciertos() { return porcentajeAciertos; }
        public void setPorcentajeAciertos(double porcentajeAciertos) { this.porcentajeAciertos = porcentajeAciertos; }
        public int getMejorPuntuacion() { return mejorPuntuacion; }
        public void setMejorPuntuacion(int mejorPuntuacion) { this.mejorPuntuacion = mejorPuntuacion; }
        public String getFechaMejorPuntuacion() { return fechaMejorPuntuacion; }
        public void setFechaMejorPuntuacion(String fechaMejorPuntuacion) { this.fechaMejorPuntuacion = fechaMejorPuntuacion; }
        public double getMediaAciertos() { return mediaAciertos; }
        public void setMediaAciertos(double mediaAciertos) { this.mediaAciertos = mediaAciertos; }
    }
    
    // Getters y Setters
    public long getTotalTests() { return totalTests; }
    public void setTotalTests(long totalTests) { this.totalTests = totalTests; }
    
    public int getTotalAciertos() { return totalAciertos; }
    public void setTotalAciertos(int totalAciertos) { this.totalAciertos = totalAciertos; }
    
    public int getTotalFallos() { return totalFallos; }
    public void setTotalFallos(int totalFallos) { this.totalFallos = totalFallos; }
    
    public double getPorcentajeAciertos() { return porcentajeAciertos; }
    public void setPorcentajeAciertos(double porcentajeAciertos) { this.porcentajeAciertos = porcentajeAciertos; }
    
    public int getTotalPreguntasRespondidas() { return totalPreguntasRespondidas; }
    public void setTotalPreguntasRespondidas(int totalPreguntasRespondidas) { this.totalPreguntasRespondidas = totalPreguntasRespondidas; }
    
    public java.util.Map<String, TemaEstadistica> getEstadisticasPorTema() { return estadisticasPorTema; }
    public void setEstadisticasPorTema(java.util.Map<String, TemaEstadistica> estadisticasPorTema) { this.estadisticasPorTema = estadisticasPorTema; }
}