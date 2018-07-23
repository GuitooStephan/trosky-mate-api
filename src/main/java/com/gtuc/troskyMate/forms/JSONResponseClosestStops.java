package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;

import java.util.ArrayList;
import java.util.List;

public class JSONResponseClosestStops {

    private int status ;
    private String message;
    private List<BusStops> busStops = new ArrayList<BusStops>();


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BusStops> getBusStops() {
        return busStops;
    }

    public void setBusStops(List<BusStops> busStops) {
        this.busStops.addAll(busStops);
    }
}
