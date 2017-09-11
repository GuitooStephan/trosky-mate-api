package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Repositories.BusStationRepositories;
import com.gtuc.troskyMate.models.Repositories.BusStopRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusStopsServices {

    @Autowired
    private BusStopRepositories repositories;

    //Query for the document of a certain area
    public List<BusStops> findByArea(String area){return repositories.findByArea(area);}
}
