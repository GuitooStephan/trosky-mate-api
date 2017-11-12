package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Repositories.BusStopsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusStopsServices {
    @Autowired
    private BusStopsRepository repositories;

    public List<BusStops> findAll() {return repositories.findAll();}

    //Query for a bus Stop
    public BusStops findBusStop(String busStopName){return repositories.findBusStop(busStopName);}


    //Query all bus stops from origin to destination
    public List<BusStops> findBusStopsForBusAndOrder(String busName, Sort sort) {return repositories.findBusStopsForBusAndOrder(busName, sort);}

    //Get the stations leading to a specific busStation
    public List<String> findAllBusOrignOfBusesLeadingToDestinationBusStop(String busStopName) {return repositories.findAllBusOrignOfBusesLeadingToDestinationBusStop(busStopName);}


    //Get bus stop in an area
    public List<BusStops> findBusStopInArea(String areaName) {return repositories.findBusStopInArea(areaName);}

    //Get the bus stops for the bus
    public List<BusStops> findBusStopsConnectedToBusStop(String busStopDestinationName){ return  repositories.findBusStopsConnectedToBusStop(busStopDestinationName);}

    //Get bus that leave a station and stop at a stop
    public List<BusStops> findBusStopUsedByTwoBusStops(String busOneName, String busTwoName){return repositories.findBusStopUsedByTwoBusStops(busOneName, busTwoName);}

    //Get paths from the origin to destination by radius
    public List<List<String>> findPaths(String busStopOriginName, String busStopDestinationName, int radius){
        switch (radius){
            case 2:
                return repositories.findPathsForOneBus(busStopOriginName, busStopDestinationName);
            case 4:
                return repositories.findPathsForTwoBus(busStopOriginName, busStopDestinationName);
            case 6:
                return repositories.findPathsForThreeBus(busStopOriginName, busStopDestinationName);
            case 8:
                return repositories.findPathsForFourBus(busStopOriginName, busStopDestinationName);
            default:
                return null;
        }
    }

    //Get the number paths between the origin and the destination by the radius
    //1 bus -- 2 radius
    //2 buses -- 4 radius
    public Integer findNumberOfPathsByRadius(String busStopOriginName, String busStopDestinationName, Integer radius){
        switch (radius){
            case 2 :
                return repositories.findNumberOfPathsForOneBus(busStopOriginName, busStopDestinationName);
            case 4:
                return repositories.findNumberOfPathsForTwoBus(busStopOriginName, busStopDestinationName);
            case 6:
                return repositories.findNumberOfPathsForThreeBus(busStopOriginName, busStopDestinationName);
            case 8:
                return repositories.findNumberOfPathsForFourBus(busStopOriginName, busStopDestinationName);
            default:
                return 0;
        }
    }

}
