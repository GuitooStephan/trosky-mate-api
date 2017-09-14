package com.gtuc.troskyMate.logic;


import com.gtuc.troskyMate.forms.JSONResponse;
import com.gtuc.troskyMate.models.Domains.BusStations;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStationsServices;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
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
    private BusStopsServices busStopsServices;
    @Autowired
    private APIKeys apiKeys;

    private final Logger logger = LoggerFactory.getLogger(RouteSelectionLogic.class);

    public String routeRequest(String origin, String destination){

        logger.info("[INFO] Route request received");
        List<String> closestBusStations = new ArrayList<String>();
        String closestBusStation = null;

        try{
            logger.info("[INFO] Fetching the bus stations information");
            //Fetching all the data about the bus stations
            List<BusStations> listOfBusStations = busStationsServices.findAll();

            logger.info("[INFO] Getting the closest bus station");
            //Get the closest Bus station
            closestBusStations = getClosestBusStation(listOfBusStations, origin, destination);

            logger.info("[INFO] Associating the location and the destinations to bus stops");
            //Associate a bus stop to the origin
            String busStopOrigin = getClosestBusStop(origin);

            //Associate a bus stop to the destination
            String busStopDestination = getClosestBusStop(destination);



            
        }catch (Exception e){
            logger.info("[ERROR] Route Request Processing failed");
        }

        return closestBusStation;
    }


    //Get the closest bus station
    public List<String> getClosestBusStation(List<BusStations> listOfBusStations, String origin, String destination){

        List<String> closestStationNames = new ArrayList<String>();
        double distanceLocationToBs = 0.0;
        double distanceDestinationToBs = 0.0;

        double temp = 0.0;
        double closestStation = 0.0;
        int i = 0;

        try{

            //Get the distance between the location and the bus stations and getting the closest
            for(BusStations busStation : listOfBusStations){
                //Get the distance between the location and the bus station
                distanceLocationToBs = getDistanceBetweenTwoCoordinates(origin,busStation.getLocation());

                //Get the distance between the bus station and the destination
                distanceDestinationToBs = getDistanceBetweenTwoCoordinates(destination, busStation.getLocation());

                //On the first loop
                if(i == 0){
                    temp = distanceDestinationToBs + distanceLocationToBs;
                    closestStation = temp;
                }

                //Make sure we don't get a station close to the destination
                if(distanceLocationToBs < distanceDestinationToBs){

                    if (temp <= closestStation){
                        closestStation = temp;
                        closestStationNames.add(busStation.getName());
                    }
                }

                i++;
            }

        } catch (Exception e){
            logger.error("[ERROR] Getting closest bus station");
        }

        return closestStationNames;
    }


    //Get the distance between two coordinates
    public double getDistanceBetweenTwoCoordinates(String origin, String destination ){

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
    public String getClosestBusStop(String coordinates){
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
            List<BusStops> busStopsList = busStopsServices.findByArea(currentLocation);

            //Get the closest bus stop and associate it to your location
            for(BusStops busStop : busStopsList){
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
}
