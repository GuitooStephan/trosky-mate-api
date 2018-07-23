package com.gtuc.troskyMate.logic;

import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.utils.Methods;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AvailableRoutes {

    @Autowired
    private Methods methods;

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
            List<BusStops> closestBusStopDestinationList = methods.getFourClosestBusStop(destinationCoordinates);

            //Get the origin bus stop info
            BusStops busStopOrigin = busStopsServices.findBusStop(originCoordinates);

            //Find out to which destination bus stop, does the user makes fewer stops
//          and the number of stops
            List<Integer> results = methods.getEasyReachDestinationBusStop(closestBusStopDestinationList, busStopOrigin);

            //Initialize a path object for all the paths between origin and destination
            List<List<String>> pathsObject = new ArrayList<List<String>>();

            //Get the best destination stop
            BusStops destinationStop = closestBusStopDestinationList.get(results.get(0));

            //Get the number of stops
            int numberOfStops = results.get(1);

            //Finding paths between bus stops
            pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopLocation(), destinationStop.getBusStopLocation(), numberOfStops);

            //Getting the correct ones
            pathsObject = methods.filterPathsForCorrectOnes(pathsObject);

            //Selecting the paths
            //If the number of paths is more than 4 or equal to, retrieve four paths
            //If the number of paths is less than 4, retrieve that much of paths
            int fecthedPaths = selectedNumberOfPathTobeRetrieved(pathsObject.size());

            pathsObject = getPathsWithFewStops(fecthedPaths, pathsObject);

            //In case there is no path
            logger.info("[INFO] Setting the response ");
            //In case there is still no paths, end the program
            if(pathsObject.size() == 0){
                return methods.displayNoBusFound(response);
            }


            //Setting the response for all the paths
            //Set status
            response.setStatus(202);
            //set message
            response.setMessage("Successful");
            //set results
            Paths path = new Paths();

            for (int i = 0 ; i < fecthedPaths; i++){
                //Get a path object out of this path from neo4j
                path = methods.setPaths(pathsObject.get(i));
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

//    Select the number of paths to fetch
    private Integer selectedNumberOfPathTobeRetrieved(int numberOfPaths){
        if(numberOfPaths >= 4){
            return 4;
        } else {
            return numberOfPaths;
        }
    }

    //Sort the correct paths in order to get the shorter ones (The ones that make
    //the user pass through few stops
    private List<List<String>> getPathsWithFewStops(int requiredPathsNumber, List<List<String>> paths){
        //The paths to be returned
        List<List<String>> results = new ArrayList<List<String>>();

        try{
            //Create a list to get the number of stops for every path
            ArrayList<Integer> numberOfStopsForPaths = new ArrayList<Integer>();
            int numberOfStops = 0;
            String route;
            for (List<String> path : paths) {
                numberOfStops = 2;

                //Get the array inside each path string
                JSONArray objs = new JSONArray(path);

                for (int i = 0; i < objs.length(); i++) {

                    JSONObject pathSegments = objs.getJSONObject(i);

                    //Check if the selected object is a bus stop object is not the last bus stop in the object
                    if (pathSegments.has("busName")) {
                        //Get the bus route
                        route = methods.splitBusNameForRoute(pathSegments.getString("busName"));

                        //Get the position of the bus stop before the bus and after the bus
                        JSONObject originBusStop = objs.getJSONObject(i - 2);
                        JSONObject destinationBusStop = objs.getJSONObject(i + 2);


                        //Add the number of stops between them to number of stops for this path
                        numberOfStops = numberOfStops + (destinationBusStop.getInt(route) - originBusStop.getInt(route));

                    } else if(pathSegments.has("busStopName")){
                        if(i >= 2){
                            JSONObject closeByBusStop = objs.getJSONObject(i-2);
                            if(closeByBusStop.has("busStopName")){
                                numberOfStops = numberOfStops + 1 ;
                            }
                        }

                    }
                }

                //Store the number of stops in our list
                numberOfStopsForPaths.add(numberOfStops);
            }

            //Sort the list of stops
            List<Integer> sortedListOfStops = methods.quicksort(numberOfStopsForPaths);

            int index;
            //Depending on the number of paths required, return them
            for (int i = 0; i < requiredPathsNumber; i++){
                //Get the index of the element of the sorted list
                index = numberOfStopsForPaths.indexOf(sortedListOfStops.get(i));

                //Get the element
                results.add(paths.get(index));

                numberOfStopsForPaths.remove(index);
                paths.remove(index);

            }
        } catch (Exception e){
            logger.error("[ERROR] Getting short paths");
            logger.error("[ERROR] " + e.getMessage());
        }
        return results;
    }
}
