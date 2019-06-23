package com.gtuc.troskyMate.helpers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gtuc.troskyMate.helpers.BusHelper;
import com.gtuc.troskyMate.helpers.SegmentHelper;
import com.gtuc.troskyMate.helpers.BusStopHelper;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.utils.Sorting;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusesServices;

import java.util.ArrayList;
import java.util.List;

@Component
public class PathHelper {
    //Logger for this class
    private final Logger logger = LoggerFactory.getLogger(PathHelper.class);

    @Autowired
    private BusStopsServices busStopsServices;

    @Autowired
    private BusStopHelper busStopHelper;

    @Autowired
    private BusesServices busesServices;

    @Autowired
    private Sorting sorting;

    @Autowired
    private BusHelper busHelper;

    @Autowired
    private SegmentHelper segmentHelper;

    /**
     * This method filter a list of paths and return the correct ones
     * @param paths
     * @return List of path
     */
    public List<List<String>> filterPathsForCorrectOnes(List<List<String>> paths){
        //Create list to get appropriate paths
        List<List<String>> appropriatePaths = new ArrayList<List<String>>();

        for (List<String> path : paths) {
            //Get the array inside each path string

            JSONArray objs = new JSONArray(path);
            if(authenticatePath(objs)){
                appropriatePaths.add(path);
            }
        }

        return appropriatePaths;
    }

    /**
     * This method checks if a path is correct
     * @param path
     * @return boolean
     */
    public Boolean authenticatePath(JSONArray path){
        for (int i = 0; i < path.length(); i++) {
            JSONObject pathSegment = path.getJSONObject(i);
            if (!segmentHelper.authenticateSegment(path, pathSegment, i)){
                return false;
            }
        }
        return true;
    }

    /**
     * This method returns the number of paths to retrieve,
     * the maximum value to be returned is 4
     * @param numberOfPaths
     * @return
     */
    public Integer selectedNumberOfPathTobeRetrieved(int numberOfPaths){
        if(numberOfPaths >= 4){
            return 4;
        } else {
            return numberOfPaths;
        }
    }

    /**
     * Count the number bus stops in path
     * @param path
     * @return int the number of bus stops
     */
    public Integer countNumberOfBsStopsInPath(List<String> path){
        //There is a minimum of 2 stops
        int numberOfStops = 2;

        //Get the array inside each path string
        JSONArray objs = new JSONArray(path);

        for (int i = 0; i < objs.length(); i++) {
            JSONObject pathSegment = objs.getJSONObject(i);
            numberOfStops = numberOfStops + segmentHelper.countBsStops(pathSegment, objs, i);
        }

        return numberOfStops;
    }

    /**
     * This method get the paths with the fewer stops in the correct ones
     * @param requiredPathsNumber the number of paths to be returned
     * @param paths
     * @return the paths
     */
    public List<List<String>> getPathsWithFewStops(int requiredPathsNumber, List<List<String>> paths){
        //The paths to be returned
        List<List<String>> results = new ArrayList<List<String>>();

        try{
            //Create a list to get the number of stops for every path
            ArrayList<Integer> numberOfStopsForPaths = new ArrayList<Integer>();
            for (List<String> path : paths) {
                //Store the number of stops in our list
                numberOfStopsForPaths.add(countNumberOfBsStopsInPath(path));
            }

            //Sort the list of stops
            List<Integer> sortedListOfStops = sorting.quicksort(numberOfStopsForPaths);

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

    /**
     * This method set a path for a response
     * @param neo4jPath
     * @return Paths
     */
    public Paths setPaths(List<String> neo4jPath){
        Paths path = new Paths();

        try {
            int positionOfOptions = 0;

            //Get BusStops in path
            //Set the bus id
            int id = 0;

            JSONArray objs = new JSONArray(neo4jPath);
            for (int i = 0; i < objs.length(); i++){
                JSONObject pathSegments = objs.getJSONObject(i);

                //Whenever you land on a bus
                if (pathSegments.has("busName")) {
                    id++;
                    positionOfOptions++;

                    //Add the bus
                    path.addBuses(busesServices.findBus(pathSegments.getString("busName")));

                    //Get the bus route
                    List<Integer> originAndDestinationBsStopsPosition = new ArrayList<Integer>();
                    originAndDestinationBsStopsPosition = segmentHelper.getBsStopsPositionInRouteForBus(objs, i, pathSegments.getString("busName"));

                    //Get the bus stops where the bus will stop and the bus id of the bus on that route
                    path.addSegment(id, busStopHelper.getBusStops(pathSegments.getString("busName"), originAndDestinationBsStopsPosition.get(0), originAndDestinationBsStopsPosition.get(1)));

                    //Get the other options of bus
                    path.addOptions(id, busHelper.getOtherBuses(objs.getJSONObject(i - 2), objs.getJSONObject(i + 2)));

                } else if(pathSegments.has("busStopName")){
                    if(i >= 2){
                        JSONObject closeByBusStop = objs.getJSONObject(i-2);
                        if(closeByBusStop.has("busStopName")){
                            //we are dealing with two bus stops that are close by
                            ArrayList<BusStops> closeBusStops = new ArrayList<BusStops>();
                            closeBusStops.add(busStopsServices.findBusStop(closeByBusStop.getString("busStopLocation")));
                            closeBusStops.add(busStopsServices.findBusStop(pathSegments.getString("busStopLocation")));
                            path.addSegment(0, closeBusStops);
                        }
                    }
                }
            }
        } catch (Exception e){
            logger.error("[ERROR] Setting Response");
        }

        return path;
    }
}
