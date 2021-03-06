package com.gtuc.troskyMate.models.Repositories;

import com.gtuc.troskyMate.models.Domains.BusStops;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface BusStopsRepository extends Neo4jRepository<BusStops, Long>{

    //Query all bus stops
    List<BusStops> findAll();

    //Query for bus stops
    @Query("match (stop:busStops) where stop.busStopLocation={0} return stop")
    BusStops findBusStop(String busStopLocation);

    @Query("match (bus:buses {busName:{0}}) - [:stopAt] -> (busStop:busStops) return busStop")
    List<BusStops> findBusStopsForBusAndOrder(String busName, Sort sort);

    @Query("match (stop:busStops)<-[:stopAt]-(bus:buses) where stop.busStopName = {0} return bus.busOrigin")
    List<String> findAllBusOrignOfBusesLeadingToDestinationBusStop(String busStopName);

    @Query("match (stop:busStops) where {0} in stop.busStopArea return stop")
    List<BusStops> findBusStopInArea(String areaName);

    //Get all bus stops for bus
    @Query("match (bus:buses), (stop:busStops), (destination:busStops {busStopName:{0}}) where (bus) - [:stopAt] -> (stop) and (bus) - [:stopAt] -> (destination) and stop.busStopName <> {0} return stop")
    List<BusStops> findBusStopsConnectedToBusStop(String busStopDestinationName);

    @Query("match (stop:busStops) where (:buses{busName:{0}}) - [:stopAt] -> (stop) and (:buses{busName:{1}}) - [:stopAt] -> (stop) return stop")
    List<BusStops> findBusStopUsedByTwoBusStops(String busOneName, String busTwoName);


    //Get the path between two bus stops
    // TODO: 17/06/19 Optimize queries
    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..1]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForOneStop(String busStopOriginLocation, String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..2]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForTwoStop(String busStopOriginLocation, String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..3]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForThreeStop(String busStopOriginLocation, String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..4]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForFourStop(String busStopOriginLocation, String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..5]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForFiveStop(String busStopOriginLocation, String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{0}})-[*..6]-(stop:busStops{busStopLocation:{1}}) RETURN p")
    List<List<String>> findPathsForSixStop(String busStopOriginLocation, String busStopDestinationLocation);


    //Find the number of paths between two bus stops
    @Query("MATCH p=(n:busStops{busStopLocation:{origin}})-[*..2]-(stop:busStops{busStopLocation:{destination}}) RETURN Count(p)")
    Integer findNumberOfPathsForOneStop(@Param("origin") String busStopOriginLocation, @Param("destination") String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{origin}})-[*..4]-(stop:busStops{busStopLocation:{destination}}) RETURN Count(p)")
    Integer findNumberOfPathsForTwoBus(@Param("origin") String busStopOriginLocation, @Param("destination") String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{origin}})-[*..6]-(stop:busStops{busStopLocation:{destination}}) RETURN Count(p)")
    Integer findNumberOfPathsForThreeBus(@Param("origin") String busStopOriginLocation, @Param("destination") String busStopDestinationLocation);

    @Query("MATCH p=(n:busStops{busStopLocation:{origin}})-[*..8]-(stop:busStops{busStopLocation:{destination}}) RETURN Count(p)")
    Integer findNumberOfPathsForFourBus(@Param("origin") String busStopOriginLocation, @Param("destination") String busStopDestinationLocation);
}
