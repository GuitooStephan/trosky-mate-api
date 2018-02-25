package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;

import java.util.ArrayList;
import java.util.List;

public class Paths {

    private int busId;

    private List<BusStops> busStopsList = new ArrayList<BusStops>();

    public Paths(int id, BusStops busStop){
        this.busStopsList.add(busStop);
        this.busId = id;
    }

    public Paths(int id, List<BusStops> busStopsList){
        this.busId = id;
        this.setBusStopsList(busStopsList);
    }


    public List<BusStops> getBusStopsList() {
        return busStopsList;
    }

    public void setBusStopsList(List<BusStops> busStopsList) {
        this.busStopsList = busStopsList;
    }

    public void addBusStop(BusStops stop){this.busStopsList.add(stop);}

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }
}