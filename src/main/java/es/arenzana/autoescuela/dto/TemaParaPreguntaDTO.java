package es.arenzana.autoescuela.dto;

import es.arenzana.autoescuela.model.Tema;

public class TemaParaPreguntaDTO {
    private Long id;
    private String nombre;
    
    // Constructors
    public TemaParaPreguntaDTO() {}
    
    public TemaParaPreguntaDTO(Tema tema) {
        this.id = tema.getId();
        this.nombre = tema.getNombre();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}