package es.arenzana.autoescuela.service;
import es.arenzana.autoescuela.model.Ajustes;
import es.arenzana.autoescuela.repository.AjustesRepository;
import org.springframework.stereotype.Service;

@Service
public class AjustesService {
    private final AjustesRepository repository;

    public AjustesService(AjustesRepository repository) {
        this.repository = repository;
    }

    public Ajustes obtenerAjustes() {
        return repository.findById(1L).orElseGet(() -> {
            Ajustes def = new Ajustes();
            def.setNombreEmpresa("Mi Autoescuela");
            def.setMailHost("smtp.gmail.com");
            def.setMailPort(587);
            return repository.save(def);
        });
    }

    public void guardarAjustes(Ajustes nuevosAjustes) {
        nuevosAjustes.setId(1L);
        repository.save(nuevosAjustes);
    }
}