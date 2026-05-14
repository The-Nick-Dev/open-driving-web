package es.arenzana.autoescuela.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import es.arenzana.autoescuela.model.Tema;

public class TemaConPreguntasDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer orden;
    private LocalDateTime fechaCreacion;
    private List<PreguntaDTO> preguntas;
    
    // Constructors
    public TemaConPreguntasDTO() {}
    
    public TemaConPreguntasDTO(Tema tema) {
        this.id = tema.getId();
        this.nombre = tema.getNombre();
        this.descripcion = tema.getDescripcion();
        this.activo = tema.getActivo();
        this.orden = tema.getOrden();
        this.fechaCreacion = tema.getFechaCreacion();
        this.preguntas = tema.getPreguntas().stream()
                .map(PreguntaDTO::new)
                .collect(Collectors.toList());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public List<PreguntaDTO> getPreguntas() { return preguntas; }
    public void setPreguntas(List<PreguntaDTO> preguntas) { this.preguntas = preguntas; }
}