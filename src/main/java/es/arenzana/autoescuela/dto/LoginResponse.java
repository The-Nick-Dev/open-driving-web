package es.arenzana.autoescuela.dto;

import es.arenzana.autoescuela.model.Rol;

public class LoginResponse {
    private String token;
    private String username;
    private String nombre;
    private Rol rol;

    public LoginResponse(String token, String username, String nombre, Rol rol) {
        this.token = token;
        this.username = username;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}