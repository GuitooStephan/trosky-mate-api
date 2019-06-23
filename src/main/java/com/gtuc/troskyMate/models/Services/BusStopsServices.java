package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Repositories.BusStopsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "busStopCache", key = "#busStopLocation")
    public BusStops findBusStop(String busStopLocation){
        System.out.println("----------------->>no caching");
        return repositories.findBusStop(busStopLocation);
    }


    //Query all bus stops from origin to destination
    public List<BusStops> findBusStopsForBusAndOrder(String busName, Sort sort) {return repositories.findBusStopsForBusAndOrder(busName, sort);}

    //Get the stations leading to a specific busStation
    public List<String> findAllBusOrignOfBusesLeadingToDestinationBusStop(String busStopName) {return repositories.findAllBusOrignOfBusesLeadingToDestinationBusStop(busStopName);}


    //Get bus stop in an area
    public List<BusStops> findBusStopInArea(String areaName) {return repositories.findBusStopInArea(areaName);}

    //Get the bus stops for the bus leading to another bus stop
    public List<BusStops> findBusStopsConnectedToBusStop(String busStopDestinationName){ return  repositories.findBusStopsConnectedToBusStop(busStopDestinationName);}

    //Get bus that leave a station and stop at a stop
    public List<BusStops> findBusStopUsedByTwoBusStops(String busOneName, String busTwoName){return repositories.findBusStopUsedByTwoBusStops(busOneName, busTwoName);}

    //Get paths from the origin to destination by radius
    public List<List<String>> findPaths(String busStopOriginLocation, String busStopDestinationLocation, int radius){
        switch (radius){
            case 1:
                return repositories.findPathsForOneStop(busStopOriginLocation, busStopDestinationLocation);
            case 2:
                return repositories.findPathsForTwoStop(busStopOriginLocation, busStopDestinationLocation);
            case 3:
                return repositories.findPathsForThreeStop(busStopOriginLocation, busStopDestinationLocation);
            case 4:
                return repositories.findPathsForFourStop(busStopOriginLocation, busStopDestinationLocation);
            case 5:
                return repositories.findPathsForFiveStop(busStopOriginLocation, busStopDestinationLocation);
            case 6:
                return repositories.findPathsForSixStop(busStopOriginLocation, busStopDestinationLocation);
            default:
                return null;
        }
    }

}
