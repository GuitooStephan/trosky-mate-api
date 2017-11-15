package com.gtuc.troskyMate.logic;


import com.google.common.base.Strings;
import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.forms.Paths;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Domains.Node;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            logger.info("[INFO] Associating the location and the destinations to bus stops");
            // Get the bus stop for the origin and destination coordinates
            List<BusStops> busStopOriginAndDestination = getClosestBusStop(origin,destination);

            //Check if the bus stop origin is different from destination
            if (busStopOriginAndDestination.get(0).equals(busStopOriginAndDestination.get(1))) throw new Exception();

            //Get bus stops
            BusStops busStopOrigin = busStopOriginAndDestination.get(0);
            BusStops busStopDestination = busStopOriginAndDestination.get(1);

            logger.info("Getting the path between origin and destination");
            //Initialize a path object for all the paths between origin and destination
            List<List<String>> pathsObject = new ArrayList<List<String>>();

            //Radius to determine the number of buses -- buses = radius / 2
            int radius = 2;

            //Run While loop until you get one or more paths
            while ( (busStopsServices.findNumberOfPathsByRadius(busStopOrigin.getBusStopName(), busStopDestination.getBusStopName(), radius)) < 1 ){
                radius = radius + 2;
                if (radius > 8){
                    break;
                }
            }

            boolean isDestinationClosed = false;
            //Get all paths taking user to his destination
            if (radius > 8){ //There is no path to the destination, It might be broken
                logger.info("[INFO] Checking for an break in the path");
                //Check if there is bus stop next to any bus stop leading to the origin where the user
                //can get a bus to the destination.
                //Getting the transit bus stops, The bus stop connected to the origin first then the bus
                //stop linked to the destination
                List<BusStops> transitBusStops = handleNonConnectedPath(busStopOrigin, busStopDestination);

                //Check if the stop leading to the destination is the destination
                if (transitBusStops.get(1).getBusStopName().equals(busStopDestination.getBusStopName() ) ) {
                    pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopName(), transitBusStops.get(0).getBusStopName(), radiusForPath(busStopOrigin.getBusStopName(), transitBusStops.get(0).getBusStopName()));
                    //Specify that the destination is closed
                    isDestinationClosed = true;
                } else { //else go ahead and get the paths with the trip with one break
                    List<List<String>> paths = getPathsFromNonConnectedRoute(transitBusStops, busStopOrigin, busStopDestination);
                    if (paths.size() >= 2){
                        //Prepare the response
                        response = setResponse(response, paths.get(0));
                        response = setResponse(response, paths.get(1));
                        return response;
                    } else return displayNoBusFound(response); //Return no bus found
                }

            } else pathsObject = busStopsServices.findPaths(busStopOrigin.getBusStopName(), busStopDestination.getBusStopName(), radius); //If the radius is less than 8 then there is a path to destination


            //Getting the correct paths with the correct buses
            pathsObject = filterPathsForCorrectOnes(pathsObject);

            logger.info("[INFO] Getting shortest path");
            //Getting the shortest path to take
            List<String> path = getShortestPath(pathsObject);

            logger.info("[INFO] Setting response");
            response = setResponse(response , path);

            if (isDestinationClosed){ //If the second transit stop is the destination
                Paths lastPath = new Paths(busStopDestination);
                response.setPaths(lastPath);
            }

        }catch (Exception e){
            logger.info(e.toString());
            logger.error("[ERROR] Getting route");
            response.setStatus(404);
            response.setMessage("Error occurred");
            response.setBuses(null);
        }

        return response;
    }


    //Sort a list of Bus stop to determine which one is closer to a coordinate
    private List<BusStops> sortBusStopsList(List<BusStops> busStopsList, String coordinates){
        try{
            List<BusStops> sortedBusStopList = new ArrayList<BusStops>();

            //Get the distance between the coordinates and each bus origin
            List<Integer> listDistance = new ArrayList<Integer>();
            for (BusStops busStops : busStopsList ){
                listDistance.add(distance(coordinates, busStops.getBusStopLocation()));
            }

            //Sort out the list of distance
            List<Integer> sortListDistance = quicksort(listDistance);

            //Fill the list to return
            int indexOfClosestBusStop ;
            for (int i = 0; i < sortListDistance.size(); i++){
                indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(i));
                sortedBusStopList.add(busStopsList.get(indexOfClosestBusStop));
            }

            return sortedBusStopList;

        } catch (Exception e){
            logger.error("[ERROR] Sorting Bus stop list");
            return new ArrayList<BusStops>();
        }

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

    //Find the closest bus stop according to neighborhood for one bus trips
    private BusStops findClosestBusStop(String neighborhood, String coordinates){
        try{
            List<BusStops> busStopsList = busStopsServices.findBusStopInArea(neighborhood);

            //Get the distance between the coordinates and each bus stops
            List<Integer> listDistance = new ArrayList<Integer>();
            for (BusStops busStops : busStopsList ){
                listDistance.add(distance(coordinates, busStops.getBusStopLocation()));
            }

            //Sort out the list of distance
            List<Integer> sortListDistance = quicksort(listDistance);

            //Get the closest bus stop
            int indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(0));

            return busStopsList.get(indexOfClosestBusStop);
        } catch (Exception e){
            logger.error("[ERROR] Getting a bus stop in an area");
            return findClosestBusStop("Accra", coordinates);
        }
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

    //Associate coordinates to the bus stop for the origin and the destination
    private List<BusStops> getClosestBusStop(String coordinatesForOrigin, String coordinatesForDestination){
        List<BusStops> busStops = new ArrayList<BusStops>();

        try{

            //Build the url for the API call
            String urlForOrigin = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + coordinatesForOrigin + "&result_type=neighborhood&key=" + apiKeys.getDistanceMatrixKey();
            String urlForDestination = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + coordinatesForDestination + "&result_type=neighborhood&key=" + apiKeys.getDistanceMatrixKey();

            //Getting the neighborhood of the coordinates
            String currentLocationForOrigin = getLocationOfCoordinates(urlForOrigin);
            String currentLocationForDestination = getLocationOfCoordinates(urlForDestination);

            //Find the bus Stops in the neighborhood
            BusStops busStopOrigin = findClosestBusStop(currentLocationForOrigin,coordinatesForOrigin);
            BusStops busStopDestination = findClosestBusStop(currentLocationForDestination, coordinatesForDestination);

            //Add the bus stops to the list
            busStops.add(busStopOrigin);
            busStops.add(busStopDestination);


        } catch(Exception e){
            logger.error("[ERROR] Associate closest bus stop");
        }

        return busStops;
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
        if (paths.size() == 0){
            return null;
        }
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
                        else break;
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

        if (paths.size() == 0){
            return null;
        }

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
            JSONArray objs = new JSONArray(path);
            for (int i = 0; i < objs.length(); i++){
                JSONObject pathSegments = objs.getJSONObject(i);

                //Whenever you land on a bus
                if (pathSegments.has("busName")) {

                    //Get his route
                    String route = splitBusNameForRoute(pathSegments.getString("busName"));

                    //Get the position of the bus stop before the bus and after the bus
                    JSONObject originBusStop = objs.getJSONObject(i - 2);
                    JSONObject destinationBusStop = objs.getJSONObject(i + 2);

                    int originBusStopPositionOnRoute = originBusStop.getInt(route);
                    int destinationBusStopPositionOnRoute = destinationBusStop.getInt(route);

                    //Generate another path for the response
                    Paths paths = new Paths(getBusStops(pathSegments.getString("busName"), route, originBusStopPositionOnRoute, destinationBusStopPositionOnRoute));

                    //Insert paths in response
                    response.setPaths(paths);
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

    //Handles the non connected path
    //Look each bus stops associated to the bus stop origin, is there any bus stop there leading to the destination
    private List<BusStops> handleNonConnectedPath(BusStops busStopOrigin, BusStops busStopDestination){
        //Initializing the bus stop list to return
        List<BusStops> transitStops = new ArrayList<BusStops>();

        //Getting the bus stop associated to the origin
        List<BusStops> busStopsAssociatedToOrigin = busStopsServices.findBusStopsConnectedToBusStop(busStopOrigin.getBusStopName());

        //Sorting the list above depending on the bus stop closest to the origin
        List<BusStops> closestBusStops = sortBusStopsList(busStopsAssociatedToOrigin, busStopOrigin.getBusStopLocation());

        //Getting all the bus stops in the database
        List<BusStops> busStopsList = busStopsServices.findAll();


        int index = 0;
        //List to receive the transit stop if there is
        List<BusStops> transitBusStop = new ArrayList<BusStops>();

        while (index < closestBusStops.size()){
            //For each bus stop associated to the origin, is there a stop next to it
            //radius 400 - 100 m
            transitBusStop = getClosestBusStopsToCoordinates(busStopsList, closestBusStops.get(index).getBusStopLocation());

            //Checking if the transit bus stop leads to the destination
            for (BusStops stop : transitBusStop){
                if(getRightPath(busStopOrigin.getBusStopName(), closestBusStops.get(index).getBusStopName()) && getRightPath(stop.getBusStopName(), busStopDestination.getBusStopName())){
                    //If yes, then add the stop associated to the origin then the one associated to
                    //Destination
                    transitStops.add(closestBusStops.get(index));
                    transitStops.add(stop);
                    return transitStops;
                }
            }

            index++;
        }
        return transitStops;
    }

    //Check if two bus stops are connected by buses
    private Boolean isConnected(String busStopOne, String busStopTwo){
        int radius = 2;
        while ( (busStopsServices.findNumberOfPathsByRadius(busStopOne, busStopTwo, radius)) < 1 ){
            radius = radius + 2;
            if (radius > 8){
                return false;
            }
        }
        return filterPathsForCorrectOnes(busStopsServices.findPaths(busStopOne, busStopTwo, radius)).size() >= 1;
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

    //Get Paths for path with breaks
    private List<List<String>> getPathsFromNonConnectedRoute(List<BusStops> transitStops, BusStops busStopOrigin, BusStops busStopDestination){
        //Initialize a list to get the final paths
        List<List<String>> finalPaths = new ArrayList<List<String>>();

        //Get the radius required for the shortest path between the origin and the first stop
        //Get the paths
        int radiusOne = radiusForPath(busStopOrigin.getBusStopName(), transitStops.get(0).getBusStopName());
        List<List<String>> pathsOne = busStopsServices.findPaths(busStopOrigin.getBusStopName(), transitStops.get(0).getBusStopName(), radiusOne);

        //Get the radius required for the shortest path between the second transit stop
        // and the destination.
        //Get the paths
        int radiusTwo = radiusForPath(transitStops.get(1).getBusStopName(), busStopDestination.getBusStopName());
        List<List<String>> pathsTwo = busStopsServices.findPaths(transitStops.get(1).getBusStopName(), busStopDestination.getBusStopName(), radiusTwo);

        //Filter the paths for the fastest
        pathsOne = filterPathsForCorrectOnes(pathsOne);
        pathsTwo = filterPathsForCorrectOnes(pathsTwo);

        //Get the appropriate paths
        List<String> pathOne = getShortestPath(pathsOne);
        List<String> pathTwo = getShortestPath(pathsTwo);

        //Add paths to the final list
        finalPaths.add(pathOne);
        finalPaths.add(pathTwo);

        return finalPaths;
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