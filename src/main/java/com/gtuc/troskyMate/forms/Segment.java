package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;

import java.util.ArrayList;

public class Segment {

    private Integer busId;

    private ArrayList<BusStops> busStops = new ArrayList<BusStops>();

    public Segment(int busId, ArrayList<BusStops> busStops){
        this.busId = busId;
        this.busStops = busStops;
    }

    public Segment(int busId, BusStops busStops){
        this.busId = busId;
        this.busStops.add(busStops);
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public ArrayList<BusStops> getBusStops() {
        return busStops;
    }

    public void setBusStops(ArrayList<BusStops> busStops) {
        this.busStops = busStops;
    }
}
