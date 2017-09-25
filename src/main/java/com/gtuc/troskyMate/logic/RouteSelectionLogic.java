package com.gtuc.troskyMate.logic;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.models.Domains.BusStations;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.BusStopsMongo;
import com.gtuc.troskyMate.models.Domains.Buses;
import com.gtuc.troskyMate.models.Services.BusStationsServices;
import com.gtuc.troskyMate.models.Services.BusStopsMongoServices;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.models.Services.BusesServices;
import com.gtuc.troskyMate.utils.APIKeys;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteSelectionLogic {

    @Autowired
    private BusStationsServices busStationsServices;
    @Autowired
    private BusStopsMongoServices busStopsMongoServices;
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

            //Associate a bus stop to the origin
            String busStopOrigin = getClosestBusStop(origin);

            //Associate a bus stop to the destination
            String busStopDestination = getClosestBusStop(destination);

            //Check if we have a bus stop for the origin and one for the destination
            if(busStopOrigin != null && busStopDestination != null){
                //Try to get a bus for the route
                logger.info("[INFO] Getting the route and the buses");
                Buses bus = getRequestBuses(busStopOrigin, busStopDestination);

                //Check if we found a bus then get the bus stops
                if (bus != null){

                    logger.info("[INFO] Getting the bus stops");
                    response = getBusStops(bus, busStopOrigin, busStopDestination);

                } else {

                    //Try to get buses for the route
                    logger.info("[INFO] There is no direct bus");

                    logger.info("[INFO] Fetching the bus stations information");
                    //Fetching all the data about the bus stations
                    List<BusStations> listOfBusStations = busStationsServices.findAll();

                    logger.info("[INFO] Getting the closest bus station");
                    //Get the closest Bus station
                    String closestBusStation = getClosestBusStation(listOfBusStations, origin);

                    logger.info("[INFO] Getting a bus to the closest bus station");
                    //Get a bus that stops at your bus stop and the closest bus station
                    Buses busOne = busesServices.findBusStopingAtOneStopOneStation(busStopOrigin, closestBusStation);

                    Buses busTwo = busesServices.findBusStopingAtOneStationOneStop(busStopDestination, closestBusStation);

                    response.setStatus(202);
                    response.setMessage("Successful");
                    response.setBuses(busOne);
                    response.setBuses(busTwo);
                    return response;
                }

                //Prepare Response
                response.setStatus(202);
                response.setMessage("Successful");
                response.setBuses(bus);
                return response;
            }

            response.setStatus(404);
            response.setMessage("No bus found");
            return response;

        }catch (Exception e){
            logger.error("[ERROR] Getting route");
            response.setStatus(404);
            response.setMessage("Error occurred");
            response.setBuses(null);
        }

        return response;
    }


    //Get the closest bus station
    private String getClosestBusStation(List<BusStations> listOfBusStations, String origin){

        String closestStationName = null;
        double distanceLocationToBs = 0.0;
        double closestStation = 0.0;
        int i = 0;

        try{

            //Get the distance between the location and the bus stations and getting the closest
            for(BusStations busStation : listOfBusStations){
                //Get the distance between the location and the bus station
                distanceLocationToBs = getDistanceBetweenTwoCoordinates(origin,busStation.getLocation());

                //On the first loop
                if(i == 0){
                    closestStation = distanceLocationToBs;
                }

                //Check if the current distance is smaller than the shortest distance
                if(distanceLocationToBs <= closestStation){
                    closestStation = distanceLocationToBs;
                    closestStationName = busStation.getName();
                }

                i++;
            }

        } catch (Exception e){
            logger.error("[ERROR] Getting closest bus station");
        }

        return closestStationName;
    }


    //Get the distance between two coordinates
    private double getDistanceBetweenTwoCoordinates(String origin, String destination ){

        double distanceBetweenTwoCoordinates = 0.0;

        try{

            //Url to the API
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origin + "&destinations=" + destination + "&mode=walking&key=AIzaSyCUjekBM_xY-nzmgYVT6e44gMIKas8R-LM";

            //Make a call to the Distance Matrix API
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Getting the information about the distance between the two points
            JSONObject obj = new JSONObject(response);
            JSONArray rows = obj.getJSONArray("rows");
            JSONObject elementHolder = rows.getJSONObject(0);
            JSONArray elements = elementHolder.getJSONArray("elements");
            JSONObject distanceHolder = elements.getJSONObject(0);
            JSONObject distance = distanceHolder.getJSONObject("distance");
            String distanceText = distance.getString("text");

            // Split distance text string
            String[] parts = distanceText.split(" ", 2);
            if(parts[1].equals("ft")){
                distanceBetweenTwoCoordinates = 0.0;
            } else{
                distanceBetweenTwoCoordinates = Double.parseDouble(parts[0]);
            }

        } catch (Exception e){
            logger.error("[ERROR] Check Distance Matrix Function");
        }

        return distanceBetweenTwoCoordinates;
    }

    //Associate coordinates to a bus stop
    private String getClosestBusStop(String coordinates){
        String busStopName = null;

        try{
            //Build the url for the API call
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + coordinates + "&result_type=locality&key=" + apiKeys.getDistanceMatrixKey();
            RestTemplate restTemplate = new RestTemplate();

            int i = 0;
            double temp = 0.0;
            double closestBusStop = 0.0;

            //Get the result
            String response = restTemplate.getForObject(url, String.class);

            //Getting the information
            JSONObject obj = new JSONObject(response);
            JSONArray results = obj.getJSONArray("results");
            JSONObject addressComponentsObject = results.getJSONObject(0);
            JSONArray addressComponents = addressComponentsObject.getJSONArray("address_components");
            JSONObject infoObject = addressComponents.getJSONObject(0);
            String currentLocation = infoObject.getString("long_name");

            //Find the bus Stops in the area
            List<BusStopsMongo> busStopsList = busStopsMongoServices.findByArea(currentLocation);

            //Get the closest bus stop and associate it to your location
            for(BusStopsMongo busStop : busStopsList){
                //Get distance between busStop and location
                temp = getDistanceBetweenTwoCoordinates(coordinates, busStop.getLocation());

                //For the first loop
                if(i == 0) {
                    closestBusStop = temp;
                }

                //Get the closest bus stop
                if(temp <= closestBusStop){
                    closestBusStop = temp;
                    busStopName = busStop.getName();
                }

                i++;
            }

        } catch(Exception e){
            logger.error("[ERROR] Associate closest bus stop");
        }

        return busStopName;
    }

    //Get the buses for the request
    private Buses getRequestBuses(String busStopOrigin, String busStopDestination){

        Buses myBus = new Buses();
        //Find bus which stop at the two bus stops
        List<Buses> busesList = busesServices.findBusStopingAtTwoBusStops(busStopOrigin, busStopDestination);
        for (Buses bus : busesList){
            //Get the route of the bus
            String route = splitStringForRoute(bus.getBusName());

            //Get the position of the bus stop in the route
            BusStops stopOrigin = busStopsServices.findBusStop(busStopOrigin);
            BusStops stopDestination = busStopsServices.findBusStop(busStopDestination);

            int routePositionOfOrigin = getPositionOnRoute(stopOrigin, route);
            int routePositionOfDestination = getPositionOnRoute(stopDestination, route);

            //If the origin comes before the destination then user can use this bus
            if(routePositionOfOrigin < routePositionOfDestination){
                return bus;
            }
        }

        return myBus;
    }

    //Get the bus stops for the request
    private JSONResponse getBusStops(Buses bus, String busStopOrigin, String busStopDestination){
        JSONResponse response = new JSONResponse();

        try {
            //Getting route
            String route = splitStringForRoute(bus.getBusName());

            //Get the position of the bus stop in the route
            BusStops stopOrigin = busStopsServices.findBusStop(busStopOrigin);
            BusStops stopDestination = busStopsServices.findBusStop(busStopDestination);

            int routePositionOfOrigin = getPositionOnRoute(stopOrigin, route);
            int routePositionOfDestination = getPositionOnRoute(stopDestination, route);

            //Get the bus stop of the bus
            List<BusStops> temp = busStopsServices.findBusStopsForBus(bus.getBusName(), route);

            //Add the first bus stop
            response.setBusStops(stopOrigin);

            //Create a JSON out of the bus stops
            JSONArray array = new JSONArray(temp);

            for(int i = routePositionOfOrigin + 1 ; i < routePositionOfDestination; i++){
                for (int j = 0 ; j < array.length(); j++){
                    JSONObject obj = array.getJSONObject(j);
                    if (obj.getInt(route) == i){
                        ObjectMapper mapper =  new ObjectMapper();
                        BusStops busStop = mapper.readValue(obj.toString(), BusStops.class);
                        response.setBusStops(busStop);
                    }
                }
            }

            //Add the last bus stop
            response.setBusStops(stopDestination);
        } catch (Exception e){
            logger.error("[ERROR] Getting the bus stops");
        }

        return response;
    }

    private String splitStringForRoute(String busName){
        String [] busNameParts = busName.split("-", 2);

        return busNameParts[0].toLowerCase() + busNameParts[1] + "Route";
    }

    private int getPositionOnRoute(BusStops busStops, String route){
        JSONObject obj = new JSONObject(busStops);

        return obj.getInt(route);
    }
}
