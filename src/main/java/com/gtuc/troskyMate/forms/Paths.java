package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;

import java.util.List;

public class Paths {

    private List<BusStops> busStopsList;

    public Paths(int busIndex, List<BusStops> busStopsList){
        this.setBusStopsList(busStopsList);
    }

    public Paths(List<BusStops> busStopsList){
        this.setBusStopsList(busStopsList);
    }


    public List<BusStops> getBusStopsList() {
        return busStopsList;
    }

    public void setBusStopsList(List<BusStops> busStopsList) {
        this.busStopsList = busStopsList;
    }

    public void addBusStop(BusStops stop){this.busStopsList.add(stop);}
}
