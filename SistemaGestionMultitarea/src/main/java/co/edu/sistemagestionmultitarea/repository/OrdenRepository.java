package co.edu.sistemagestionmultitarea.repository;

import co.edu.sistemagestionmultitarea.enums.EstadoOrden;
import co.edu.sistemagestionmultitarea.model.Orden;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrdenRepository extends MongoRepository<Orden,String> {
    List<Orden> findByEstadoNot(EstadoOrden estado);
}
