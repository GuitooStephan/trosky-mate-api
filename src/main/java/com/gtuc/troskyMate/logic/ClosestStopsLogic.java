package com.gtuc.troskyMate.logic;


import com.gtuc.troskyMate.forms.JSONResponseClosestStops;
import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

@Component
public class ClosestStopsLogic {

    @Autowired
    private BusStopsServices busStopsServices;

    private final Logger logger = LoggerFactory.getLogger(ClosestStopsLogic.class);

    public JSONResponseClosestStops closestStopsRequest (String location){

        logger.info("[INFO] Request for closest stops around location");
        JSONResponseClosestStops response = new JSONResponseClosestStops();

        try{
            //Getting the 4 closest stops
            List<BusStops> closestStops = getClosestBusStop(location);

            //If there are no close stops
            if(closestStops.size() == 0){
                return displayNoStopFound(response);
            }

            logger.info("[INFO] Setting response");
            response.setStatus(202);
            response.setMessage("Successful");
            response.setBusStops(closestStops);

        } catch(Exception e){
            logger.error("[ERROR] Getting closest stops");
            response.setStatus(404);
            response.setMessage("Error Occurred");
            response.setBusStops(null);
        }

        return response;
    }


    //Getting the closest stops to the location
    private List<BusStops> getClosestBusStop(String coordinatesForLocation){
        List<BusStops> busStops = new ArrayList<BusStops>();

        try{
            //Find the bus Stops 4 for location
            logger.info("[INFO] Query for the 4 closest bus stops");
            List<BusStops> closeBusStop = findClosestBusStop(coordinatesForLocation);

            //Add the bus stops to the list
            busStops.addAll(closeBusStop);


        } catch(Exception e){
            logger.error("[ERROR] Associate closest bus stop");
        }

        return busStops;
    }


    //Finding the 4 closest bus stops
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

            //Boolean to check if a bus stop with a similar name was in the list
            Boolean isSimilar = false;

            //Get the four closest bus stops
            while (i < 8 ){
                //Get the closest bus stop index
                int indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(i));

                //Check if a bus stop with a similar name was added before after a bus stop was added in the list
                if (i > 0){

                    //Check in the list of closest bus stops
                    for (BusStops busStop : closestBusStop){
                        if(busStop.getBusStopName().equalsIgnoreCase(busStopsList.get(indexOfClosestBusStop).getBusStopName())){
                            isSimilar = true;
                        }
                    }

                    //If isSimilar = true then there is a bus stop with the similar name else add it to the list
                    if(!isSimilar){
                        //Add the closest bus stop to the list
                        closestBusStop.add(busStopsList.get(indexOfClosestBusStop));
                    }

                    //Set boolean to false
                    isSimilar = false;
                } else {
                    //Add the closest bus stop to the list
                    closestBusStop.add(busStopsList.get(indexOfClosestBusStop));
                }

                //Break the while loop if the list has four bus stops
                if(closestBusStop.size() == 4){
                    break;
                }

                //increase i
                i++;
            }


        } catch (Exception e){
            logger.error("[ERROR] Getting close bus stops");
            System.out.println(e.getMessage());
        }
        return closestBusStop;
    }

    //Get the distance between two coordinates
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
    private JSONResponseClosestStops displayNoStopFound(JSONResponseClosestStops response){
        response.setStatus(404);
        response.setMessage("We don't cover this area");

        return response;
    }
}
