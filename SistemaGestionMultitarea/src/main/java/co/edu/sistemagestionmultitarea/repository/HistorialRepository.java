package co.edu.sistemagestionmultitarea.repository;

import co.edu.sistemagestionmultitarea.model.Historial;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistorialRepository extends MongoRepository<Historial,String> {
    List<Historial> findByOrdenIdOrderByTimestampAsc(String ordenId);
}
