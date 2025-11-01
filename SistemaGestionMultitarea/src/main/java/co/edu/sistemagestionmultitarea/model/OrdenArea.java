package co.edu.sistemagestionmultitarea.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orden_area")
public class OrdenArea {
    @Id
    private String id;
    private String ordenId;
    private String areaId;
    private String asignadaA;
    private String estadoParcial = "NUEVA";
    private String estado; // PENDIENTE, EN_PROGRESO, PAUSADA, COMPLETADA, TIMEOUT
    private int segAcumulados; // segundos acumulados trabajando
    private int slaSeg; // tiempo máximo permitido por SLA en segundos
}