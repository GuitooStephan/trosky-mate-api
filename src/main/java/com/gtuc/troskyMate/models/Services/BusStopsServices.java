package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Repositories.BusStopsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<BusStops> findBusStopsForBus(String busName, String route) {return repositories.findBusStopsForBus(busName, route);}
}
