package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Repositories.BusesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusesServices {

    @Autowired
    private BusesRepository repositories;

    public List<Buses> findAll() {return repositories.findAll();}

    //Get Buses that stops at two bus stops
    public List<Buses> findBusStopingAtTwoBusStops(String busStopOrigin, String busStopDestination){return repositories.findBusStopingAtTwoBusStops(busStopOrigin, busStopDestination);}

    //Get Buses that stops at a bus stop and go to a station
    public Buses findBusStopingAtOneStopOneStation(String busStopOrigin, String busStationName){return repositories.findBusStopingAtOneStopOneStation(busStopOrigin, busStationName);}

    //Get bus that leave a station and stop at a stop
    public Buses findBusStopingAtOneStationOneStop(String busStopOrigin, String busStationName){return repositories.findBusStopingAtOneStationOneStop(busStopOrigin, busStationName);}
}
