package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.Buses;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusesRepository extends GraphRepository<Buses> {

    //Query all the buses
    List<Buses> findAll();

    @Query("MATCH (bus:buses) WHERE (bus) - [:stopAt] -> (:busStops {busStopName:{0}}) AND (bus) - [:stopAt] -> (:busStops {busStopName:{1}}) RETURN bus;")
    List<Buses> findBusStopingAtTwoBusStops(String busStopOrigin, String busStopDestination);
}
