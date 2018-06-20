package com.gtuc.troskyMate.logic;


import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Options;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.models.Services.BusesServices;
import com.gtuc.troskyMate.utils.APIKeys;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

@Component
public class RouteSelectionLogic {

    @Autowired
    private BusesServices busesServices;
    @Autowired
    private APIKeys apiKeys;
    @Autowired
    private BusStopsServices busStopsServices;

    private final Logger logger = LoggerFactory.getLogger(RouteSelectionLogic.class);

    public JSONResponse routeRequest(String origin, String destination){

        logger.info("[INFO] Route request received");
        JSONResponse response = new JSONResponse();

        try{
            logger.info("[INFO] Associating the location of the destination and origin to bus stops");
            // Get 4 bus stops for the destination and the origin bus stop
            List<BusStops> closestBusStopDestinationList = getClosestBusStop(destination);
            BusStops busStopOrigin = getOriginBusStop(origin);

            logger.info("[INFO] Getting the bus stop for destination");
            //Get bus stops
            int i = 0;
            //Get the 4 stop for the destination
            List<BusStops> busStopDestinationList = new ArrayList<BusStops>();
            while (i < 4){
                busStopDestinationList.add(closestBusStopDestinationList.get(i));
                logger.info(closestBusStopDestinationList.get(i).getBusStopLocation());
                i++;
            }

            logger.info("Getting the path between origin and destination");

            //Find out to which destination bus stop, does the user makes fewer stops
//          and the number of stops
            List<Integer> results = getEasyReachDestinationBusStop(busStopDestinationList, busStopOrigin);

            //Initialize a path object for all the paths between origin and destination
            List<List<String>> pathsObject = new ArrayList<List<String>>();

            //Get the best destination stop
            BusStops destinationStop = busStopDestinationList.get(results.get(0));

            //Get the number of stops
            int numberOfStops = results.get(1);

            //Finding paths between bus stops
            pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopLocation(), destinationStop.getBusStopLocation(), numberOfStops);

            //Getting the correct ones
            pathsObject = filterPathsForCorrectOnes(pathsObject);

            logger.info("[INFO] Getting the path ");
            //In case there is still no paths, end the program
            if(pathsObject.size() == 0){
                return displayNoBusFound(response);
            }

            logger.info("[INFO] Getting shortest path");
            //Getting the shortest path to take
            List<String> path = getShortestPath(pathsObject);


            logger.info("[INFO] Setting response");
            response = setBusesAndPaths(response , path, pathsObject);

        }catch (Exception e){
            logger.info(e.toString());
            logger.error("[ERROR] Getting route");
            response.setStatus(404);
            response.setMessage("Error occurred");
            response.setBuses(null);
        }

        return response;
    }

    //Split bus name for route
    private String splitBusNameForRoute(String busName){
        String [] busNameParts = busName.split("-", 2);

        return busNameParts[0].toLowerCase() + busNameParts[1] + "Route";
    }

    //Find 4 closest bus stop according to neighborhood for one bus trips for origin
    private List<BusStops> findClosestBusStop(String coordinates){
        //Variable fo while loop
        int i = 0;
        //Create a list for the 4 closest bus stop
        List<BusStops> closestBusStop = new ArrayList<BusStops>();

        try{

            logger.info("[INFO] Requesting all bus stops");
            List<BusStops> busStopsList = busStopsServices.findAll();

            logger.info("[INFO] Calculating Distance");
            //Get the distance between the coordinates and each bus stops
            List<Integer> listDistance = new ArrayList<Integer>();
            for (BusStops busStops : busStopsList ){
                listDistance.add(distance(coordinates, busStops.getBusStopLocation()));
            }

            //Sort out the list of distance
            List<Integer> sortListDistance = quicksort(listDistance);

            //Get the four closest bus stops
            while (i < 4 ){
                //Get the closest bus stop index
                int indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(i));

                //Add the closest bus stop to the list
                closestBusStop.add(busStopsList.get(indexOfClosestBusStop));

                //increase i
                i++;
            }


        } catch (Exception e){
            logger.error("[ERROR] Getting close bus stops");
            System.out.println(e.getMessage());
        }
        return closestBusStop;
    }

    //Get the distance between two points
    private int distance(String firstCoordinate, String secondCoordinate) {

        //Get the first and second latitude
        double lat1 = getLatitude(firstCoordinate);
        double lat2 = getLatitude(secondCoordinate);

        //Get the first and second longitude
        double lon1 = getLongitude(firstCoordinate);
        double lon2 = getLongitude(secondCoordinate);

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters;

        return (int) distance;
    }

    //Get Latitude
    private double getLatitude(String coordinates){
        String [] coordinatesParts = coordinates.split(",",2);
        return parseDouble(coordinatesParts[0]);
    }

    //Get Longitude
    private double getLongitude(String coordinates){
        String [] coordinatesParts = coordinates.split(",", 2);
        return parseDouble(coordinatesParts[1]);
    }

    //Quick Sort
    private List<Integer> quicksort(List<Integer> input){

        if(input.size() <= 1){
            return input;
        }

        int middle = (int) Math.ceil((double)input.size() / 2);
        int pivot = input.get(middle);

        List<Integer> less = new ArrayList<Integer>();
        List<Integer> greater = new ArrayList<Integer>();

        for (int i = 0; i < input.size(); i++) {
            if(input.get(i) <= pivot){
                if(i == middle){
                    continue;
                }
                less.add(input.get(i));
            }
            else{
                greater.add(input.get(i));
            }
        }

        return concatenate(quicksort(less), pivot, quicksort(greater));
    }

    /**
     * Join the less array, pivot integer, and greater array
     * to single array.
     * @param less integer ArrayList with values less than pivot.
     * @param pivot the pivot integer.
     * @param greater integer ArrayList with values greater than pivot.
     * @return the integer ArrayList after join.
     */
    private List<Integer> concatenate(List<Integer> less, int pivot, List<Integer> greater){

        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < less.size(); i++) {
            list.add(less.get(i));
        }

        list.add(pivot);

        for (int i = 0; i < greater.size(); i++) {
            list.add(greater.get(i));
        }

        return list;
    }

    //Display No bus Stops
    private JSONResponse displayNoBusFound(JSONResponse response){
        response.setStatus(404);
        response.setMessage("No bus found");

        return response;
    }

    //Associate coordinates to the bus stop for the destination
    private List<BusStops> getClosestBusStop(String coordinatesForDestination){
        List<BusStops> busStops = new ArrayList<BusStops>();

        try{
            //Find the bus Stops 4 for destination
            List<BusStops> busStopDestination = findClosestBusStop(coordinatesForDestination);

            //Add the bus stops to the list
            busStops.addAll(busStopDestination);


        } catch(Exception e){
            logger.error("[ERROR] Associate closest bus stop");
        }

        return busStops;
    }

    //Getting the origin information
    private BusStops getOriginBusStop(String coordinatesForOrigin){
        BusStops busStopOrigin = new BusStops();

        busStopOrigin = busStopsServices.findBusStop(coordinatesForOrigin);

        return busStopOrigin;
    }

    //Go through the paths and identify the correct ones
    private List<List<String>> filterPathsForCorrectOnes(List<List<String>> paths){
        //Create list to get appropriate paths
        List<List<String>> appropriatePaths = new ArrayList<List<String>>();

        for (List<String> path : paths) {
            //Get the array inside each path string

            JSONArray objs = new JSONArray(path);

            Boolean isAppropriate = false;
            for (int i = 0; i < objs.length(); i++) {

                JSONObject pathSegments = objs.getJSONObject(i);

                if (pathSegments.has("busName")) {
                    try {
                        String route = splitBusNameForRoute(pathSegments.getString("busName"));

                        JSONObject originBusStop = objs.getJSONObject(i - 2);
                        JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                        int originBusStopPositionOnRoute = originBusStop.getInt(route);
                        int destinationBusStopPositionOnRoute = destinationBusStop.getInt(route);

                        if (originBusStopPositionOnRoute < destinationBusStopPositionOnRoute) isAppropriate = true;
                        else {
                            isAppropriate = false;
                            break;
                        }
                    } catch (Exception e) {
                        logger.error("[ERROR] Getting Appropirate path");
                    }
                }
            }

            if (isAppropriate) appropriatePaths.add(path);
        }

        return appropriatePaths;
    }

    //Get the shortest path
    //It select the first bus and find the path in which the bus stop the user is to
    //alight to first has the smallest position in the route of bus
    private List<String> getShortestPath(List<List<String>> paths){

        //Create a list for finding the closest ne bus stop
        List<Integer> distanceBetweenOriginAndFirstTransitStopList = new ArrayList<Integer>();

        //Loop through the correct paths
        for (List<String> path : paths){
            JSONArray objs = new JSONArray(path);

            //Loop through each path
            for (int i = 0; i < objs.length(); i++){

                JSONObject pathSegments = objs.getJSONObject(i);

                //For the first bus
                if (pathSegments.has("busName")) {

                    //Find the route for the bus
                    String route = splitBusNameForRoute(pathSegments.getString("busName"));

                    //Find the bus stop, the user is supposed to alight to
                    JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                    //Find the position of the bus stop in the bus route then store it in the list
                    distanceBetweenOriginAndFirstTransitStopList.add(destinationBusStop.getInt(route));

                    break;
                }
            }
        }

        //Sort the list
        List<Integer> listOfShortestDistance = quicksort(distanceBetweenOriginAndFirstTransitStopList);

        //Get the index of the path in which the user will get down faster
        int shortestRouteIndex = distanceBetweenOriginAndFirstTransitStopList.indexOf(listOfShortestDistance.get(0));

        return paths.get(shortestRouteIndex);

    }

    //Get buses on path
    private List<String> getBusesOnPath(List<String> path){
        List<String> busNames = new ArrayList<String>();

        JSONArray objs = new JSONArray(path);

        for (int i = 0; i < objs.length(); i++){

            JSONObject pathSegments = objs.getJSONObject(i);

            if (pathSegments.has("busName")) {
                busNames.add(pathSegments.getString("busName"));
            }
        }

        return busNames;
    }

    //Set the buses and the paths
    private JSONResponse setBusesAndPaths(JSONResponse response, List<String> path, List<List<String>> pathObject){

        try {

            //Get Buses in response
            List<String> busNames = getBusesOnPath(path);
            for (String busName : busNames){
                response.setBuses(busesServices.findBus(busName));
            }

            int positionOfOptions = 0;

            //Get BusStops in path
            //Set the bus id
            int id = 0;

            JSONArray objs = new JSONArray(path);
            for (int i = 0; i < objs.length(); i++){
                JSONObject pathSegments = objs.getJSONObject(i);

                //Whenever you land on a bus
                if (pathSegments.has("busName")) {
                    id++;
                    positionOfOptions++;

                    //Get his route
                    String route = splitBusNameForRoute(pathSegments.getString("busName"));

                    //Get the position of the bus stop before the bus and after the bus
                    JSONObject originBusStop = objs.getJSONObject(i - 2);
                    JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                    int originBusStopPositionOnRoute = originBusStop.getInt(route);
                    int destinationBusStopPositionOnRoute = destinationBusStop.getInt(route);

                    //Generate another path for the response
                    Paths paths = new Paths(id, getBusStops(pathSegments.getString("busName"), route, originBusStopPositionOnRoute, destinationBusStopPositionOnRoute));

                    //Insert paths in response
                    response.setPaths(paths);

                    //Set the other options for this bus
                    Options option = setOptionsForBus(response.numberOfPaths(), positionOfOptions, pathObject);

                    //Insert options in the response
                    response.setOptions(option);

                } else if(pathSegments.has("busStopName")){
                    if(i >= 2){
                        JSONObject closeByBusStop = objs.getJSONObject(i-2);
                        if(closeByBusStop.has("busStopName")){
                            Paths closeByPath = new Paths(0, busStopsServices.findBusStop(closeByBusStop.getString("busStopLocation")));
                            closeByPath.addBusStop(busStopsServices.findBusStop(pathSegments.getString("busStopLocation")));
                            response.setPaths(closeByPath);
                        }
                    }
                }
            }

            //Set response status and message
            response.setStatus(202);
            response.setMessage("Successful Processing");

            return response;
        } catch (Exception e){
            logger.error("[ERROR] Setting Response");
            response.setStatus(404);
            response.setMessage("No Bus Found");
            return response;
        }
    }

    //Set options for a bus
    private Options setOptionsForBus(int pathId, int positionOfOption, List<List<String>> pathObject){
        //Set new options
        Options option = new Options();

        int indexOfBusName = 0;

        //Set the path ID
        option.setPathId(pathId);

        //Set the bus in the options
        //Loop thru the paths
        for (List<String> path : pathObject){
            indexOfBusName = 1;

            //Create an array
            JSONArray obj = new JSONArray(path);

            //Loop thru the elements in the array
            for(int i = 0; i < obj.length() ; i++){

                JSONObject pathSegments = obj.getJSONObject(i);

                if (pathSegments.has("busName")){
                    if(indexOfBusName == positionOfOption){
                        option.addOtherBuses(busesServices.findBus(pathSegments.getString("busName")));
                        break;
                    }
                    indexOfBusName++;
                }
            }

        }

        return option;

    }

    //Get the bus stops for the request
    private List<BusStops> getBusStops(String busName, String route, int routePositionOfOrigin, int routePositionOfDestination){
        List<BusStops> listOfBusStopsForPath = new ArrayList<BusStops>();

        try {
            //Get the string for order by
            String orderBy = "busStop." + route;
            //Get the bus stop of the bus
            Sort sort = new Sort(Sort.Direction.ASC, orderBy);
            List<BusStops> listOfBusStopsOfBus = busStopsServices.findBusStopsForBusAndOrder(busName, sort);


            //Add the bus stops to the response
            for (int i = routePositionOfOrigin - 1 ; i < routePositionOfDestination; i++){
                try {
                    listOfBusStopsForPath.add(listOfBusStopsOfBus.get(i));
                } catch (Exception e){
                    logger.error("[ERROR] Wrong Index");
                }
            }
        } catch (Exception e){
            logger.info(e.toString());
            logger.error("[ERROR] Getting the bus stops");
        }

        return listOfBusStopsForPath;
    }

    //Find out to which of the destination bus stop, does the user makes fewer stops and how many stops the user is to make
    private List<Integer> getEasyReachDestinationBusStop(List<BusStops> destinationStops, BusStops origin){
        //Initialize a list for the index of the best destination bus stop and the number of stops
        List<Integer> results = new ArrayList<Integer>();

        //Initialize a path object for all the paths between origin and destination
        List<List<String>> pathsObject = new ArrayList<List<String>>();

        //List to keep track of the number of stops required for each destination stop
        List<Integer> numberOfStopsForDestinationStops = new ArrayList<Integer>();

        //For each destination bus stop, check if how many stops do we need to get there
        for(BusStops busStopDestination : destinationStops){

            //Radius to determine the number of stops
            int numberOfStops = 0;

            //Run While loop until the number of stops is more than 6 or you have found the number of
//          stop required
            while ( true ){
                if (numberOfStops >= 6){
                    numberOfStopsForDestinationStops.add(10);
                    break;
                }

                numberOfStops = numberOfStops + 1;

                //Finding paths between bus stops
                pathsObject = busStopsServices.findPaths(origin.getBusStopLocation(), busStopDestination.getBusStopLocation(), numberOfStops);

                //Getting the correct ones
                pathsObject = filterPathsForCorrectOnes(pathsObject);

                if(pathsObject.size() > 0){
                    numberOfStopsForDestinationStops.add(numberOfStops);
                    break;
                }
            }
        }

//        Sort the list of the number of the stops for the destination stops
        List<Integer> sortedList = quicksort(numberOfStopsForDestinationStops);

        //Input the index of best destination stop
        results.add(numberOfStopsForDestinationStops.indexOf(sortedList.get(0)));
        //Input the number of stops
        results.add(sortedList.get(0));

        return results;

    }
}