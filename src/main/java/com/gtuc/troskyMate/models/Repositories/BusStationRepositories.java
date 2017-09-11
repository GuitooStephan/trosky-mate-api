package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.BusStations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface BusStationRepositories extends MongoRepository<BusStations, Serializable> {

    BusStations findByName(String name);

    //Repositories for the whole collection
    List<BusStations> findAll();
}
