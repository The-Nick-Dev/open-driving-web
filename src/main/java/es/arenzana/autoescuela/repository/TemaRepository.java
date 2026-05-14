package es.arenzana.autoescuela.repository;

import es.arenzana.autoescuela.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {
    Optional<Tema> findByNombre(String nombre);
    List<Tema> findByActivoTrueOrderByOrdenAsc();
    List<Tema> findAllByOrderByOrdenAsc();
}