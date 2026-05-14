package es.arenzana.autoescuela.repository;

import es.arenzana.autoescuela.model.RespuestaAlumno;
import es.arenzana.autoescuela.model.TestRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaAlumnoRepository extends JpaRepository<RespuestaAlumno, Long> {
    List<RespuestaAlumno> findByTest(TestRealizado test);
    Optional<RespuestaAlumno> findByTestAndPreguntaId(TestRealizado test, Long preguntaId);
    void deleteByTest(TestRealizado test);
}