package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;

import java.util.ArrayList;
import java.util.List;

public class JSONResponse {

    private int status ;
    private String message;
    private List<Buses> buses = new ArrayList<Buses>();
    private List<Object> busStops = new ArrayList<Object>();


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

    public List<Buses> getBuses() {
        return buses;
    }

    public void setBuses(Buses buses) {
        this.buses.add(buses);
    }

    public List<Object> getBusStops() {
        return busStops;
    }

    public void setBusStops(Object busStops) {
        this.busStops.add(busStops);
    }
}
