package co.edu.sistemagestionmultitarea.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "historial")
public class Historial {
    @Id
    private String id;
    private String ordenId;
    private String evento;
    private String detalle;
    private String estadoGlobal;
    private Instant timestamp = Instant.now();
    private String actor;
}