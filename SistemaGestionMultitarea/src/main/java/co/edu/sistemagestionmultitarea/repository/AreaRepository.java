package co.edu.sistemagestionmultitarea.repository;

import co.edu.sistemagestionmultitarea.model.Area;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AreaRepository  extends MongoRepository<Area,String> {

}
