package co.edu.sistemagestionmultitarea.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ordenes")
public class Orden {
    @Id
    private String id;
    private String titulo;
    private String descripcion;
    private String creador;
    private String estadoGlobal = "NUEVA";
    private Instant creadaEn = Instant.now();
    private Instant actualizadaEn = Instant.now();
    private List<String> areasAsignadas;
    private String estado;
}