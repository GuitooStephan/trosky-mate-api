package com.gtuc.troskyMate.logic;


import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.helpers.BusStopHelper;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClosestStopsLogic {
    @Autowired
    private BusStopHelper bsHelper;

    private final Logger logger = LoggerFactory.getLogger(ClosestStopsLogic.class);

    public JSONResponse closestStopsRequest (String location){

        logger.info("[INFO] Request for closest stops around location");
        JSONResponse response = new JSONResponse();

        try{
            //Getting the 4 closest stops
            List<BusStops> closestStops = bsHelper.findClosestBusStop(location);

            //If there are no close stops
            if(closestStops.size() == 0){
                response.setNotFound();
                return response;
            }

            logger.info("[INFO] Setting response");
            response.setStatus(202);
            response.setMessage("Successful");
            //set the result
            for(BusStops stop : closestStops){
                JSONObject obj = new JSONObject(stop);
                response.addResult(obj.toMap());
            }
            logger.info("[INFO] Successful");

        } catch(Exception e){
            logger.error("[ERROR] Getting closest stops");
            logger.error(e.getMessage());
            response.setStatus(404);
            response.setMessage("Error Occurred");
        }

        return response;
    }
}
