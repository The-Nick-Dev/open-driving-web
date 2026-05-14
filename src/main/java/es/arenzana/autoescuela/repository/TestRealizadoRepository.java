package es.arenzana.autoescuela.repository;

import es.arenzana.autoescuela.model.TestRealizado;
import es.arenzana.autoescuela.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRealizadoRepository extends JpaRepository<TestRealizado, Long> {
    List<TestRealizado> findByAlumnoOrderByFechaInicioDesc(Usuario alumno);
    
    Optional<TestRealizado> findFirstByAlumnoAndTemaIdAndCompletadoFalseOrderByFechaInicioDesc(Usuario alumno, Long temaId);
    
    List<TestRealizado> findByAlumnoAndCompletadoTrue(Usuario alumno);
    
    @Query("SELECT COUNT(t) FROM TestRealizado t WHERE t.alumno = :alumno AND t.completado = true")
    long countTestsCompletadosByAlumno(@Param("alumno") Usuario alumno);
    
    @Query("SELECT COALESCE(SUM(t.aciertos), 0) FROM TestRealizado t WHERE t.alumno = :alumno AND t.completado = true")
    int sumAciertosByAlumno(@Param("alumno") Usuario alumno);
    
    @Query("SELECT COALESCE(SUM(t.fallos), 0) FROM TestRealizado t WHERE t.alumno = :alumno AND t.completado = true")
    int sumFallosByAlumno(@Param("alumno") Usuario alumno);
}