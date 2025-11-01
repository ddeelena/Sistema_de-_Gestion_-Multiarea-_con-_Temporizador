package co.edu.sistemagestionmultitarea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaGestionMultitareaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaGestionMultitareaApplication.class, args);
    }

}
