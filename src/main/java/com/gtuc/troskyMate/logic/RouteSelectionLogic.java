package com.gtuc.troskyMate.logic;


import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;
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
import org.springframework.web.client.RestTemplate;

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

            //Initialize a path object for all the paths between origin and destination
            List<List<String>> pathsObject = new ArrayList<List<String>>();

            //Check for each closest bus stop for a path to the destination
            for(BusStops busStopDestination : busStopDestinationList){

                //Radius to determine the number of transitions
                int radius = 2;
                boolean keepTrying = true;

                //Get the paths
                pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopLocation(), busStopDestination.getBusStopLocation(), radius);

                //Run While loop until you get one or more paths
                while ( keepTrying ){
                    if (radius >= 8){
                        break;
                    }

                    radius = radius + 2;

                    pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopLocation(), busStopDestination.getBusStopLocation(), radius);
                    pathsObject = filterPathsForCorrectOnes(pathsObject);

                    if(pathsObject.size() > 0){
                        keepTrying = false;
                    }
                }

                //Get all paths taking user to his destination
                if (pathsObject.size() > 0){ //There are paths to the destination
                    break;
                } //Else try with another bus stop
            }

            logger.info("[INFO] Getting the path ");
            //In case there is still no paths, end the program
            if(pathsObject.size() == 0){
                return displayNoBusFound(response);
            }

            //Getting the correct paths with the correct buses
//            pathsObject = filterPathsForCorrectOnes(pathsObject);

            //In case there is still no appropriate paths, end the program
//            if(pathsObject.size() == 0){
//                return displayNoBusFound(response);
//            }

            logger.info("[INFO] Getting shortest path");
            //Getting the shortest path to take
            List<String> path = getShortestPath(pathsObject);

            logger.info("[INFO] Setting response");
            response = setResponse(response , path);

        }catch (Exception e){
            logger.info(e.toString());
            logger.error("[ERROR] Getting route");
            response.setStatus(404);
            response.setMessage("Error occurred");
            response.setBuses(null);
        }

        return response;
    }

    //Get the bus stops in a 100 - 500 m radius of a specific bus stop
    private List<BusStops> getClosestBusStopsToCoordinates(List<BusStops> listOfBusOrigins, String originCoordinates){

        List<BusStops> closestStops = new ArrayList<BusStops>();

        try{

            //Get the distance between the coordinates and each bus origin
            List<Integer> listDistance = new ArrayList<Integer>();

            for (BusStops busStops : listOfBusOrigins ){
                listDistance.add(distance(originCoordinates, busStops.getBusStopLocation()));
            }

            //Sort out the list of distance
            List<Integer> sortListDistance = quicksort(listDistance);

            for (int i=0; i < sortListDistance.size(); i++){
                //Get the closest bus stop
                int indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(i));
                if(sortListDistance.get(i) <= 400 && sortListDistance.get(i) >= 100) closestStops.add(listOfBusOrigins.get(indexOfClosestBusStop));
            }

            return closestStops;

        } catch (Exception e){
            logger.error("[ERROR] Getting closest bus station");
        }

        return closestStops;
    }

    //Split bus name for route
    private String splitBusNameForRoute(String busName){
        String [] busNameParts = busName.split("-", 2);

        return busNameParts[0].toLowerCase() + busNameParts[1] + "Route";
    }

    //Get Position of a bus stop on a route
    private int getPositionOnRoute(BusStops busStops, String route){
        JSONObject obj = new JSONObject(busStops);

        return obj.getInt(route);
    }

    //Get the neighborhood of a coordinates by geocode
    private String getLocationOfCoordinates(String url){

        try{
            //Create a rest template
            RestTemplate restTemplate = new RestTemplate();

            //Get the result
            String response = restTemplate.getForObject(url, String.class);

            //Getting the information
            JSONObject obj = new JSONObject(response);
            JSONArray results = obj.getJSONArray("results");
            JSONObject addressComponentsObject = results.getJSONObject(0);
            JSONArray addressComponents = addressComponentsObject.getJSONArray("address_components");
            JSONObject infoObject = addressComponents.getJSONObject(0);
            return infoObject.getString("long_name");
        } catch (Exception e){
            return "Accra";
        }
    }

    //Find 4 closest bus stop according to neighborhood for one bus trips for origin
    private List<BusStops> findClosestBusStop(String coordinates){
        //Variable fo while loop
        int i = 0;
        //Create a list for the 4 closest bus stop
        List<BusStops> closestBusStop = new ArrayList<BusStops>();

        try{
            List<BusStops> busStopsList = busStopsServices.findAll();

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

    //Get the buses that passes from the first parameter then to the second parameter
    private Buses getRequestBuses(BusStops busStopOrigin, BusStops busStopDestination){

        Buses myBus = new Buses();
        //Find bus which stop at the two bus stops
        List<Buses> busesList = busesServices.findBusStopingAtTwoBusStops(busStopOrigin.getBusStopName(), busStopDestination.getBusStopName());
        for (Buses bus : busesList){
            //Get the route of the bus
            String route = splitBusNameForRoute(bus.getBusName());

            int routePositionOfOrigin = getPositionOnRoute(busStopOrigin, route);
            int routePositionOfDestination = getPositionOnRoute(busStopDestination, route);

            //If the origin comes before the destination then user can use this bus
            if(routePositionOfOrigin < routePositionOfDestination){
                return bus;
            }
        }

        return myBus;
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
    private List<String> getShortestPath(List<List<String>> paths){

        List<Integer> distanceBetweenOriginAndFirstTransitStopList = new ArrayList<Integer>();

        for (List<String> path : paths){
            JSONArray objs = new JSONArray(path);

            for (int i = 0; i < objs.length(); i++){

                JSONObject pathSegments = objs.getJSONObject(i);

                if (pathSegments.has("busName")) {
                    String route = splitBusNameForRoute(pathSegments.getString("busName"));

                    JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                    distanceBetweenOriginAndFirstTransitStopList.add(destinationBusStop.getInt(route));

                    break;
                }
            }
        }

        List<Integer> listOfShortestDistance = quicksort(distanceBetweenOriginAndFirstTransitStopList);

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

    //Set a response
    private JSONResponse setResponse(JSONResponse response, List<String> path){

        try {

            //Get Buses in path
            List<String> busNames = getBusesOnPath(path);
            for (String busName : busNames){
                response.setBuses(busesServices.findBus(busName));
            }

            //Get BusStops in path
            //Set the bus id
            int id = 0;

            JSONArray objs = new JSONArray(path);
            for (int i = 0; i < objs.length(); i++){
                JSONObject pathSegments = objs.getJSONObject(i);

                //Whenever you land on a bus
                if (pathSegments.has("busName")) {
                    id++;

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

    //Returned the radius required to get a path from one stop to the other
    private int radiusForPath(String busStopOne, String busStopTwo){
        int radius = 2;
        while ( (busStopsServices.findNumberOfPathsByRadius(busStopOne, busStopTwo, radius)) < 1 ){
            radius = radius + 2;
            if (radius > 8){
                return 0;
            }
        }
        return radius;
    }

    //Check if there is a correct path between two stops
    private Boolean getRightPath(String busStopOne, String busStopTwo){
        try{
            if (busStopOne.equals(busStopTwo)){
                return true;
            }

            List<List<String>> pathsObject = busStopsServices.findPaths(busStopOne, busStopTwo, radiusForPath(busStopOne, busStopTwo));


            if (filterPathsForCorrectOnes(pathsObject).size() >= 1){
                return true;
            } else return false;

        } catch (Exception e){
            return false;
        }

    }
}