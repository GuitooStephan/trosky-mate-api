package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Repositories.BusesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusesServices {

    @Autowired
    private BusesRepository repositories;

    //Get Buses that stops at two bus stops
    public List<Buses> findBusStopingAtTwoBusStops(String busStopOrigin, String busStopDestination){return repositories.findBusStopingAtTwoBusStops(busStopOrigin, busStopDestination);}

    //Get Buses from bus name
    public Buses findBus(String busName){ return repositories.findBus(busName);}
}
