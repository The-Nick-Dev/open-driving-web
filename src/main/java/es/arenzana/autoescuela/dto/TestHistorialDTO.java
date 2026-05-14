package es.arenzana.autoescuela.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.arenzana.autoescuela.model.TestRealizado;

public class TestHistorialDTO {
    private Long id;
    private String temaNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean completado;
    private Integer puntuacionTotal;
    private Integer aciertos;
    private Integer fallos;
    private Integer totalPreguntas;
    
    // Constructor
    public TestHistorialDTO() {}
    
    public TestHistorialDTO(TestRealizado test) {
        this.id = test.getId();
        this.temaNombre = test.getTema() != null ? test.getTema().getNombre() : "Aleatorio";
        this.fechaInicio = test.getFechaInicio();
        this.fechaFin = test.getFechaFin();
        this.completado = test.getCompletado();
        this.puntuacionTotal = test.getPuntuacionTotal();
        this.aciertos = test.getAciertos();
        this.fallos = test.getFallos();
        this.totalPreguntas = test.getTotalPreguntas();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTemaNombre() { return temaNombre; }
    public void setTemaNombre(String temaNombre) { this.temaNombre = temaNombre; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    
    public Integer getPuntuacionTotal() { return puntuacionTotal; }
    public void setPuntuacionTotal(Integer puntuacionTotal) { this.puntuacionTotal = puntuacionTotal; }
    
    public Integer getAciertos() { return aciertos; }
    public void setAciertos(Integer aciertos) { this.aciertos = aciertos; }
    
    public Integer getFallos() { return fallos; }
    public void setFallos(Integer fallos) { this.fallos = fallos; }
    
    public Integer getTotalPreguntas() { return totalPreguntas; }
    public void setTotalPreguntas(Integer totalPreguntas) { this.totalPreguntas = totalPreguntas; }
    
    // Método auxiliar para formatear fecha (opcional)
    public String getFechaFinFormateada() {
        if (fechaFin == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaFin.format(formatter);
    }
}