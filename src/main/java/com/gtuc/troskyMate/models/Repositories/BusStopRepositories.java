package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.BusStops;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface BusStopRepositories extends MongoRepository<BusStops, Serializable>{

    //Find Area
    List<BusStops> findByArea(String area);


}
