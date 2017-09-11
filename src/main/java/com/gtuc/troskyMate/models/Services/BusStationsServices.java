package com.gtuc.troskyMate.models.Services;


import com.gtuc.troskyMate.models.Domains.BusStations;
import com.gtuc.troskyMate.models.Repositories.BusStationRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusStationsServices {

    @Autowired
    private BusStationRepositories repositories;

    //Query for the whole collection
    public List<BusStations> findAll() {return repositories.findAll();}

    public BusStations findByName(String name) {return repositories.findByName(name);}
}
