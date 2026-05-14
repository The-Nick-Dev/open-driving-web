package es.arenzana.autoescuela.repository;

import es.arenzana.autoescuela.model.Pregunta;
import es.arenzana.autoescuela.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByTemaAndActivoTrue(Tema tema);
    List<Pregunta> findByActivoTrue();
    List<Pregunta> findByTemaId(Long temaId);
    List<Pregunta> findByTemaIdAndActivoTrue(Long temaId);
    
    @Query("SELECT COUNT(p) FROM Pregunta p WHERE p.tema.id = :temaId AND p.activo = true")
    long countByTemaIdAndActivoTrue(@Param("temaId") Long temaId);

    @Query(value = "SELECT * FROM preguntas WHERE activo = 1 ORDER BY RAND() LIMIT :limite", nativeQuery = true)
    List<Pregunta> findRandomActive(@Param("limite") int limite);
}