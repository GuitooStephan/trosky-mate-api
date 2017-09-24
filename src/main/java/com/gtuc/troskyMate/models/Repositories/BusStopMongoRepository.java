package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.BusStopsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface BusStopMongoRepository extends MongoRepository<BusStopsMongo, Serializable>{

    //Find Area
    List<BusStopsMongo> findByArea(String area);


}
