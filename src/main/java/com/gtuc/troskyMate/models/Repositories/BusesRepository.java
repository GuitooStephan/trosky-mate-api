package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.Buses;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusesRepository extends Neo4jRepository<Buses, Long> {

    //Query all the buses
    List<Buses> findAll();

    @Query("MATCH (n:buses) WHERE n.busName = {0} RETURN n")
    Buses findBus(String busName);

    @Query("MATCH (bus:buses) WHERE (bus) - [:stopAt] -> (:busStops {busStopLocation:{0}}) AND (bus) - [:stopAt] -> (:busStops {busStopLocation:{1}}) RETURN bus;")
    List<Buses> findBusStopingAtTwoBusStops(String busStopOriginLocation, String busStopDestinationLocation);
}
