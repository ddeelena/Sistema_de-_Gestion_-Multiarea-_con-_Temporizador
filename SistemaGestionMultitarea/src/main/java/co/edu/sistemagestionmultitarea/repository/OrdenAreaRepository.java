package co.edu.sistemagestionmultitarea.repository;

import co.edu.sistemagestionmultitarea.model.OrdenArea;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrdenAreaRepository extends MongoRepository<OrdenArea,String> {
    List<OrdenArea> findByOrdenId(String ordenId);
    List<OrdenArea> findByEstadoIn(List<String> estados);
}
