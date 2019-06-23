package com.gtuc.troskyMate.helpers;

import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Services.BusesServices;
import com.gtuc.troskyMate.helpers.SegmentHelper;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BusHelper {
    //Logger for this class
    private final Logger logger = LoggerFactory.getLogger(BusHelper.class);

    @Autowired
    private SegmentHelper segmentHelper;

    @Autowired
    private BusesServices busesServices;

    /**
     * This method extract the name of the route from the bus name
     * @param busName
     * @return String -- route name
     */
    public String splitBusNameForRoute(String busName){
        String [] busNameParts = busName.split("-", 2);

        return busNameParts[0].toLowerCase() + busNameParts[1] + "Route";
    }

    //Get the other options for the buses

    /**
     * This method gets alternatives for the bus in a segment
     * @param busStopOrigin
     * @param busStopDestination
     * @return ArrayList<Buses> list of buses
     */
    public ArrayList<Buses> getOtherBuses(JSONObject busStopOrigin, JSONObject busStopDestination){
        logger.info("[INFO] Get the other buses");
        //Get the other options
        List<Buses> buses = busesServices.findBusStopingAtTwoBusStops(busStopOrigin.getString("busStopLocation"), busStopDestination.getString("busStopLocation"));

        //Get the options
        ArrayList<Buses> options = new ArrayList<Buses>();

        for (Buses bus : buses){
            //Get the route of the bus
            String route = splitBusNameForRoute(bus.getBusName());

            //Get the position of the origin and the destination on the route
            int originStopPositionOnRoute = busStopOrigin.getInt(route);
            int destinationStopPositionOnRoute = busStopDestination.getInt(route);

//          If the destination bus stop comes before the origin, then the bus is valid
            if (destinationStopPositionOnRoute > originStopPositionOnRoute){
                options.add(bus);
            }
        }

        return options;

    }
}
