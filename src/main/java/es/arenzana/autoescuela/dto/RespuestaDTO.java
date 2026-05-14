package es.arenzana.autoescuela.dto;

import es.arenzana.autoescuela.model.Respuesta;

public class RespuestaDTO {
    private Long id;
    private String texto;
    private Boolean correcta;
    private Integer orden;
    
    // Constructors
    public RespuestaDTO() {}
    
    public RespuestaDTO(Respuesta respuesta) {
        this.id = respuesta.getId();
        this.texto = respuesta.getTexto();
        this.correcta = respuesta.getCorrecta();
        this.orden = respuesta.getOrden();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    
    public Boolean getCorrecta() { return correcta; }
    public void setCorrecta(Boolean correcta) { this.correcta = correcta; }
    
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}