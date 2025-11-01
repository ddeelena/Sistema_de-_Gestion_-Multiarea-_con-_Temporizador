package co.edu.sistemagestionmultitarea.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "areas")
public class Area {
    @Id
    private String id;
    private String nombre;
    private String responsable;
}