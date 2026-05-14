package es.arenzana.autoescuela;

import es.arenzana.autoescuela.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AutoescuelaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoescuelaApplication.class, args);
    }

    // Este bean se ejecuta al arrancar la app
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {};
    }
}