package es.arenzana.autoescuela.dto;

import java.time.LocalDateTime;

import es.arenzana.autoescuela.model.Tema;

public class TemaSimpleDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer orden;
    private LocalDateTime fechaCreacion;
    
    // Constructors
    public TemaSimpleDTO() {}
    
    public TemaSimpleDTO(Tema tema) {
        this.id = tema.getId();
        this.nombre = tema.getNombre();
        this.descripcion = tema.getDescripcion();
        this.activo = tema.getActivo();
        this.orden = tema.getOrden();
        this.fechaCreacion = tema.getFechaCreacion();
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
}