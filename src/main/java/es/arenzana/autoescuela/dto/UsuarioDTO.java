package es.arenzana.autoescuela.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import es.arenzana.autoescuela.model.Rol;
import es.arenzana.autoescuela.model.Usuario;

public class UsuarioDTO {
    private Long id;
    private String username;
    private String nombre;
    private String correo;
    private Rol rol;
    private LocalDate fechaExpiracion;
    private LocalDateTime fechaCreacion;
    
    // Constructors
    public UsuarioDTO() {}
    
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol();
        this.fechaExpiracion = usuario.getFechaExpiracion();
        this.fechaCreacion = usuario.getFechaCreacion();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDate fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}