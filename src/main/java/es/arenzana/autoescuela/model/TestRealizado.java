package es.arenzana.autoescuela.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests_realizados")
public class TestRealizado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Usuario alumno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tema_id")
    private Tema tema;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    private Boolean completado = false;
    
    @Column(name = "puntuacion_total")
    private Integer puntuacionTotal;
    
    private Integer aciertos = 0;
    
    private Integer fallos = 0;
    
    @Column(name = "total_preguntas")
    private Integer totalPreguntas = 0;
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespuestaAlumno> respuestas = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "test_preguntas",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "pregunta_id")
    )
    private List<Pregunta> preguntasAsignadas = new ArrayList<>();

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    
    public Tema getTema() { return tema; }
    public void setTema(Tema tema) { this.tema = tema; }
    
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
    
    public List<RespuestaAlumno> getRespuestas() { return respuestas; }
    public void setRespuestas(List<RespuestaAlumno> respuestas) { this.respuestas = respuestas; }

    public List<Pregunta> getPreguntasAsignadas() { return preguntasAsignadas; }
    public void setPreguntasAsignadas(List<Pregunta> preguntasAsignadas) { this.preguntasAsignadas = preguntasAsignadas; }
}