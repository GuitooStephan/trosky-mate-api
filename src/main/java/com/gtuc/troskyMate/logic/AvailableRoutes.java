package com.gtuc.troskyMate.logic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gtuc.troskyMate.helpers.BusStopHelper;
import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.helpers.PathHelper;

@Component
public class AvailableRoutes {
    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private BusStopHelper bsHelper;

    @Autowired
    private BusStopsServices busStopsServices;

    //Logger for this class
    private final Logger logger = LoggerFactory.getLogger(AvailableRoutes.class);

    //Method to retrieve available route to destination
    public JSONResponse getRoutes(String originCoordinates, String destinationCoordinates){

        logger.info("[INFO] routes requested");

        //Initializing the response
        JSONResponse response = new JSONResponse();

        try {
            logger.info("[INFO] Associating the location of the destination and origin to bus stops");
//            Get the four closest bus stops to the destination
            List<BusStops> closestBusStopDestinationList = bsHelper.findClosestBusStop(destinationCoordinates);

            //Get the origin bus stop info
            BusStops busStopOrigin = busStopsServices.findBusStop(originCoordinates);

            //Find out to which destination bus stop, does the user makes fewer stops
//          and the number of stops
            List<Integer> results = bsHelper.getEasyReachDestinationBusStop(closestBusStopDestinationList, busStopOrigin);

            //Initialize a path object for all the paths between origin and destination
            List<List<String>> pathsObject = new ArrayList<List<String>>();

            //Get the best destination stop
            BusStops destinationStop = closestBusStopDestinationList.get(results.get(0));
            //Get the number of stops
            int numberOfStops = results.get(1);

            //Finding paths between bus stops
            pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopLocation(), destinationStop.getBusStopLocation(), numberOfStops);

            //Getting the correct ones
            pathsObject = pathHelper.filterPathsForCorrectOnes(pathsObject);

            //Selecting the paths
            //If the number of paths is more than 4 or equal to, retrieve four paths
            //If the number of paths is less than 4, retrieve that much of paths
            int numberOfPathTobeRetrieved = pathHelper.selectedNumberOfPathTobeRetrieved(pathsObject.size());
            pathsObject = pathHelper.getPathsWithFewStops(numberOfPathTobeRetrieved, pathsObject);

            //In case there is no path
            logger.info("[INFO] Setting the response ");
            //In case there is still no paths, end the program
            if(pathsObject.size() == 0){
                response.setNotFound();
                return response;
            }

            //Setting the response for all the paths
            response.setAsSuccessful();
            Paths path = new Paths();

            for (int i = 0 ; i < pathsObject.size(); i++){
                //Get a path object out of this path from neo4j
                path = pathHelper.setPaths(pathsObject.get(i));
                JSONObject obj = new JSONObject(path);
                response.addResult(obj.toMap());
            }

            logger.info("[INFO] Successful");

        } catch (Exception e){
            logger.info(e.toString());
            logger.error("[ERROR] Getting route");
            response.setStatus(404);
            response.setMessage("Error occurred");
        }

        return response;
    }
}
