package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Repositories.BusesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusesServices {

    @Autowired
    private BusesRepository repositories;

    //Get Buses that stops at two bus stops
    public List<Buses> findBusStopingAtTwoBusStops(String busStopOriginLocation, String busStopDestinationLocation){return repositories.findBusStopingAtTwoBusStops(busStopOriginLocation, busStopDestinationLocation);}

    //Get Buses from bus name
    @Cacheable(value = "busCache", key = "#busName")
    public Buses findBus(String busName){
        System.out.println("-------->>No caching");
        return repositories.findBus(busName);
    }
}
