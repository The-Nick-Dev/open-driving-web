package es.arenzana.autoescuela.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_alumno")
public class RespuestaAlumno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private TestRealizado test;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respuesta_seleccionada_id")
    private Respuesta respuestaSeleccionada;
    
    private Boolean correcta = false;
    
    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;
    
    @PrePersist
    protected void onCreate() {
        fechaRespuesta = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public TestRealizado getTest() { return test; }
    public void setTest(TestRealizado test) { this.test = test; }
    
    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }
    
    public Respuesta getRespuestaSeleccionada() { return respuestaSeleccionada; }
    public void setRespuestaSeleccionada(Respuesta respuestaSeleccionada) { this.respuestaSeleccionada = respuestaSeleccionada; }
    
    public Boolean getCorrecta() { return correcta; }
    public void setCorrecta(Boolean correcta) { this.correcta = correcta; }
    
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}