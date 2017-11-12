package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;

import java.util.List;

public class Paths {

    private int busIndex;
    private List<BusStops> busStopsList;

    public Paths(int busIndex, List<BusStops> busStopsList){
        this.setBusIndex(busIndex);
        this.setBusStopsList(busStopsList);
    }

    public Paths(List<BusStops> busStopsList){
        this.setBusStopsList(busStopsList);
    }


    public int getBusIndex() {
        return busIndex;
    }

    public void setBusIndex(int busIndex) {
        this.busIndex = busIndex;
    }

    public List<BusStops> getBusStopsList() {
        return busStopsList;
    }

    public void setBusStopsList(List<BusStops> busStopsList) {
        this.busStopsList = busStopsList;
    }
}
