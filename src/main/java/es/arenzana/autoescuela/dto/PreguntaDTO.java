package es.arenzana.autoescuela.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import es.arenzana.autoescuela.model.Pregunta;

public class PreguntaDTO {
    private Long id;
    private String texto;
    private String explicacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private List<RespuestaDTO> respuestas;
    
    // Constructors
    public PreguntaDTO() {}
    
    public PreguntaDTO(Pregunta pregunta) {
        this.id = pregunta.getId();
        this.texto = pregunta.getTexto();
        this.explicacion = pregunta.getExplicacion();
        this.activo = pregunta.getActivo();
        this.fechaCreacion = pregunta.getFechaCreacion();
        this.fechaModificacion = pregunta.getFechaModificacion();
        this.respuestas = pregunta.getRespuestas().stream()
                .map(RespuestaDTO::new)
                .collect(Collectors.toList());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
    
    public List<RespuestaDTO> getRespuestas() { return respuestas; }
    public void setRespuestas(List<RespuestaDTO> respuestas) { this.respuestas = respuestas; }
}