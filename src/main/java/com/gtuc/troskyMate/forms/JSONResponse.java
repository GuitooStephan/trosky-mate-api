package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;

import java.util.ArrayList;
import java.util.List;

public class JSONResponse {

    private int status ;
    private String message;
    private List<Buses> buses = new ArrayList<Buses>();
    private List<Paths> paths = new ArrayList<Paths>();


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

    public List<Paths> getPaths() {
        return paths;
    }

    public void setPaths(List<Paths> paths) {
        this.paths = paths;
    }

    public void setPaths(Paths paths) {
        this.paths.add(paths);
    }
}
