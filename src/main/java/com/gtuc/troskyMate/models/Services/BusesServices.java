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

}
