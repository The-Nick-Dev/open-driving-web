package es.arenzana.autoescuela.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import es.arenzana.autoescuela.model.Pregunta;

public class PreguntaConTemaDTO {
    private Long id;
    private String texto;
    private String explicacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private TemaSimpleDTO tema;
    private List<RespuestaDTO> respuestas;
    
    // Constructors
    public PreguntaConTemaDTO() {}

    public PreguntaConTemaDTO(Pregunta pregunta) {
        this(pregunta, true);
    }

    public PreguntaConTemaDTO(Pregunta pregunta, boolean incluirRespuestas) {
        this.id = pregunta.getId();
        this.texto = pregunta.getTexto();
        this.explicacion = pregunta.getExplicacion();
        this.activo = pregunta.getActivo();
        this.fechaCreacion = pregunta.getFechaCreacion();
        this.fechaModificacion = pregunta.getFechaModificacion();
        if (pregunta.getTema() != null) {
            this.tema = new TemaSimpleDTO(pregunta.getTema());
        }
        if (incluirRespuestas) {
            this.respuestas = pregunta.getRespuestas().stream()
                    .map(RespuestaDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.respuestas = List.of();
        }
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
    
    public TemaSimpleDTO getTema() { return tema; }
    public void setTema(TemaSimpleDTO tema) { this.tema = tema; }
    
    public List<RespuestaDTO> getRespuestas() { return respuestas; }
    public void setRespuestas(List<RespuestaDTO> respuestas) { this.respuestas = respuestas; }
}