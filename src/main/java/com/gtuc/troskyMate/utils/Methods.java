package com.gtuc.troskyMate.utils;

import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Options;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.logic.AvailableRoutes;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.models.Services.BusesServices;
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
public class Methods {

    @Autowired
    private BusesServices busesServices;

    @Autowired
    private BusStopsServices busStopsServices;

    //Logger for this class
    private final Logger logger = LoggerFactory.getLogger(Methods.class);


    //Associate the destination coordinates to four closest bus stops
    public List<BusStops> getFourClosestBusStop(String coordinatesForDestination){
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


    //Find 4 closest bus stop to some coordinates
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

    //Quick Sort
    public List<Integer> quicksort(List<Integer> input){

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


    //Find out to which of the destination bus stop, does the user makes fewer stops and how many stops the user is to make
    public List<Integer> getEasyReachDestinationBusStop(List<BusStops> destinationStops, BusStops origin){
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

    //Go through the paths and identify the correct ones
    public List<List<String>> filterPathsForCorrectOnes(List<List<String>> paths){
        //Create list to get appropriate paths
        List<List<String>> appropriatePaths = new ArrayList<List<String>>();

        for (List<String> path : paths) {
            //Get the array inside each path string

            JSONArray objs = new JSONArray(path);

            boolean isAppropriate = false;
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

    //Split bus name for route
    public String splitBusNameForRoute(String busName){
        String [] busNameParts = busName.split("-", 2);

        return busNameParts[0].toLowerCase() + busNameParts[1] + "Route";
    }

    //Display No bus Stops
    public JSONResponse displayNoBusFound(JSONResponse response){
        response.setStatus(404);
        response.setMessage("No bus found");

        return response;
    }

    //Set the paths object
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
                    String route = splitBusNameForRoute(pathSegments.getString("busName"));

                    //Get the position of the bus stop before the bus and after the bus
                    JSONObject originBusStop = objs.getJSONObject(i - 2);
                    JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                    int originBusStopPositionOnRoute = originBusStop.getInt(route);
                    int destinationBusStopPositionOnRoute = destinationBusStop.getInt(route);

                    //Get the bus stops where the bus will stop and the bus id of the bus on that route
                    path.addSegment(id, getBusStops(pathSegments.getString("busName"), route, originBusStopPositionOnRoute, destinationBusStopPositionOnRoute));

                    //Get the other options of bus
                    path.addOptions(id, getOtherBuses(originBusStop, destinationBusStop));

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

    //Get the other options for the buses
    private ArrayList<Buses> getOtherBuses(JSONObject busStopOrigin, JSONObject busStopDestination){
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

    //Get the bus stops for the request
    private ArrayList<BusStops> getBusStops(String busName, String route, int routePositionOfOrigin, int routePositionOfDestination){
        ArrayList<BusStops> listOfBusStopsForPath = new ArrayList<BusStops>();

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
}
