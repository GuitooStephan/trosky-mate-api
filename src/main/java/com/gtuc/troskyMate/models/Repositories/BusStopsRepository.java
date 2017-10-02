package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.BusStops;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusStopsRepository extends GraphRepository<BusStops>{

    //Query all bus stops
    List<BusStops> findAll();

    //Query for bus stops
    @Query("match (stop:busStops) where stop.busStopName={0} return stop")
    BusStops findBusStop(String busStopName);

    @Query("match (bus:buses {busName:{0}}) - [:stopAt] -> (busStop:busStops) return busStop")
    List<BusStops> findBusStopsForBus(String busName);
}
