package es.arenzana.autoescuela.repository;

import es.arenzana.autoescuela.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByPreguntaIdOrderByOrdenAsc(Long preguntaId);
    void deleteByPreguntaId(Long preguntaId);
}