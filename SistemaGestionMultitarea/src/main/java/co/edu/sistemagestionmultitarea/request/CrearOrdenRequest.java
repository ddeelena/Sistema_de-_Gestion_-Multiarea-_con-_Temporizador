package co.edu.sistemagestionmultitarea.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearOrdenRequest {
    private String titulo;
    private String descripcion;
    private String creador;
    private List<String> areas;    // IDs de las áreas a las que se asigna la orden
    private String asignadaA;      // Usuario responsable de las áreas
    private int slaSeg;            // SLA en segundos para cada área
}