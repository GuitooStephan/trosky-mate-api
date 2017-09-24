package com.gtuc.troskyMate.models.Services;

import com.gtuc.troskyMate.models.Domains.BusStopsMongo;
import com.gtuc.troskyMate.models.Repositories.BusStopMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class BusStopsMongoServices {

    @Autowired
    private BusStopMongoRepository repositories;

    //Query for the document of a certain area
    public List<BusStopsMongo> findByArea(String area){return repositories.findByArea(area);}
}
