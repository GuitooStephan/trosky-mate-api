package com.gtuc.troskyMate.helpers;

import com.gtuc.troskyMate.logic.ClosestStopsLogic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gtuc.troskyMate.helpers.BusHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class SegmentHelper {
    private final Logger logger = LoggerFactory.getLogger(SegmentHelper.class);

    @Autowired
    private BusHelper busHelper;
    /**
     * This method authenticate segments of paths
     * @param segment
     * @param index of the segment in the path
     * @param path
     * @return boolean
     */
    public boolean authenticateSegment(JSONArray path, JSONObject segment, int index){
        if (segment.has("busName")) {
            try {
                List<Integer> bsStopsPositions = getBsStopsPositionInRouteForBus(path, index, segment.getString("busName"));

                return (bsStopsPositions.get(0) < bsStopsPositions.get(1));
            } catch (Exception e) {
                logger.error("[ERROR] Getting Appropirate path");
                return false;
            }
        }
        return true;
    }

    /**
     * This method counts the number of bus stops in a segment
     * @param segment
     * @param path
     * @param index
     * @return int the number of stops
     */
    public Integer countBsStops(JSONObject segment, JSONArray path, int index){
        int numberOfStops = 0;
        //Check if the selected object is a bus stop object is not the last bus stop in the object
        if (segment.has("busName")) {
            List<Integer> bsStopsPositions = getBsStopsPositionInRouteForBus(path, index, segment.getString("busName"));

            //Add the number of stops between them to number of stops for this path
            numberOfStops = numberOfStops + (bsStopsPositions.get(1) - bsStopsPositions.get(0));

        } else if(segment.has("busStopName")){
            if(index >= 2){
                JSONObject closeByBusStop = path.getJSONObject(index-2);
                if(closeByBusStop.has("busStopName")){
                    numberOfStops = numberOfStops + 1 ;
                }
            }

        }
        return numberOfStops;
    }

    /**
     * This method gets the starting bus stop and the stopping bus stop of a bus in a path
     * @param path
     * @param index
     * @param busName
     * @return List<Integer> , 0 for origin and 1 for destination
     */
    public List<Integer> getBsStopsPositionInRouteForBus(JSONArray path, int index, String busName){
        List<Integer> bsStopsPositions = new ArrayList<Integer>();
        String route = busHelper.splitBusNameForRoute(busName);
        //Get the position of the starting bus stop and the stopping bus stop of a bus
        JSONObject originBusStop = path.getJSONObject(index - 2);
        JSONObject destinationBusStop = path.getJSONObject(index + 2);

        bsStopsPositions.add(originBusStop.getInt(route));
        bsStopsPositions.add(destinationBusStop.getInt(route));
        return bsStopsPositions;
    }
}
