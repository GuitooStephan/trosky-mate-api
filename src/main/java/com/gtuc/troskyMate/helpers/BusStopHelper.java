package com.gtuc.troskyMate.helpers;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Services.BusStopsServices;
import com.gtuc.troskyMate.utils.DistanceOperations;
import com.gtuc.troskyMate.utils.Sorting;
import com.gtuc.troskyMate.helpers.PathHelper;
import com.gtuc.troskyMate.helpers.BusHelper;

@Component
public class BusStopHelper {
    @Autowired
    private BusHelper busHelper;

    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private BusStopsServices busStopsServices;

    @Autowired
    private DistanceOperations dOperation;

    @Autowired
    private Sorting sorting;

    private final Logger logger = LoggerFactory.getLogger(BusStopHelper.class);

    /**
     * This method finds the closest bus stops around some coordinates
     * @param coordinates
     * @return List<BusStops> -- List of the bus stops
     */
    public List<BusStops> findClosestBusStop(String coordinates){
        //Variable for the while loop
        int i = 0;
        //Create a list for the 4 closest bus stop
        List<BusStops> closestBusStop = new ArrayList<BusStops>();

        try{
            List<BusStops> busStopsList = busStopsServices.findAll();

            //Get the distance between the coordinates and each bus stops
            List<Integer> listDistance = new ArrayList<Integer>();
            for (BusStops busStops : busStopsList ){
                listDistance.add(dOperation.distance(coordinates, busStops.getBusStopLocation()));
            }

            //Sort out the list of distance
            List<Integer> sortListDistance = sorting.quicksort(listDistance);

            //Get the four closest bus stops
            while (i < 8 ){
                //Get the closest bus stop index
                int indexOfClosestBusStop = listDistance.indexOf(sortListDistance.get(i));
                if (closestBusStop.size() > 0){
                    if(!bsContains(closestBusStop, busStopsList.get(indexOfClosestBusStop))){
                        closestBusStop.add(busStopsList.get(indexOfClosestBusStop));
                    }
                } else {
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

    //Find out to which of the destination bus stop, does the user makes fewer stops and how many stops the user is to make

    /**
     * This method finds out which bus stop is the easiest to reach
     * @param destinationStops List<BusStops>
     * @param origin BusStops -- Origin bus stop
     * @return the number of stops required to reach each bus stop
     */
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
//                  10 is used as the number of stops just to ensure that this path comes last when the list is sorted
                    numberOfStopsForDestinationStops.add(10);
                    break;
                }

                numberOfStops = numberOfStops + 1;

                //Finding paths between bus stops
                pathsObject = busStopsServices.findPaths(origin.getBusStopLocation(), busStopDestination.getBusStopLocation(), numberOfStops);

                //Getting the correct ones
                pathsObject = pathHelper.filterPathsForCorrectOnes(pathsObject);

                if(pathsObject.size() > 0){
                    numberOfStopsForDestinationStops.add(numberOfStops);
                    break;
                }
            }
        }

//        Sort the list of the number of the stops for the destination stops
        List<Integer> sortedList = sorting.quicksort(numberOfStopsForDestinationStops);

        //Input the index of best destination stop
        results.add(numberOfStopsForDestinationStops.indexOf(sortedList.get(0)));
        //Input the number of stops
        results.add(sortedList.get(0));
        return results;
    }

    //Get the bus stops for the request

    /**
     * This method get the bus stops for a segment
     * @param busName the bus in the segment
     * @param routePositionOfOrigin the origin bus stop
     * @param routePositionOfDestination the destination bus stop
     * @return ArrayList<BusStops> the list of bus stops
     */
    @Cacheable(value = "busCache", key = "{#busName, #routePositionOfOrigin, #routePositionOfDestination}")
    public ArrayList<BusStops> getBusStops(String busName, int routePositionOfOrigin, int routePositionOfDestination){
        System.out.println("--------->>No caching");
        ArrayList<BusStops> listOfBusStopsForPath = new ArrayList<BusStops>();
        String route = busHelper.splitBusNameForRoute(busName);

        try {
            //Get the string for order by
            String orderBy = "busStop." + route;
            //Get the bus stop of the bus
            Sort sort = new Sort(Sort.Direction.ASC, orderBy);
            List<BusStops> listOfBusStopsOfBus = busStopsServices.findBusStopsForBusAndOrder(busName, sort);


            //Add the bus stops between the origin bus stop and the destination bus stop
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

    /**
     * This method checks if a bus stop is in a list of bus stops
     * @param bsStops List<BusStops>
     * @param bs BusStops
     * @return Boolean
     */
    public Boolean bsContains(List<BusStops> bsStops , BusStops bs){
        for(BusStops b : bsStops){
            if(b.getBusStopName().equalsIgnoreCase(bs.getBusStopName())){
                return true;
            }
        }
        return false;
    }
}
