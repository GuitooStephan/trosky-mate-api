package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.BusStops;
import com.gtuc.troskyMate.models.Domains.Buses;

import java.util.ArrayList;
import java.util.List;

public class Paths {

    private ArrayList<Buses> buses = new ArrayList<Buses>();

    private ArrayList<Segment> segments = new ArrayList<Segment>();

    private ArrayList<Options> options = new ArrayList<Options>();


    public Paths(){

    }

    public Paths(Buses bus, Segment segment){
        this.getSegments().add(segment);
        this.getBuses().add(bus);
    }

    public Paths(ArrayList<Buses> buses, ArrayList<Segment> segments){
        this.setBuses(buses);
        this.setSegments(segments);
    }


    public void addSegment(int busId, ArrayList<BusStops> busStops){
        Segment segment = new Segment(busId, busStops);
        this.getSegments().add(segment);
    }

    public void addBuses(Buses bus){
        this.getBuses().add(bus);
    }

    public void addOptions(int busId, ArrayList<Buses> buses){
        buses = filterOptions(buses);
        Options option = new Options(busId, buses);
        this.getOptions().add(option);
    }

    private ArrayList<Buses> filterOptions(ArrayList<Buses> optionsBuses){
        ArrayList<Buses> filteredBuses = new ArrayList<Buses>();
        boolean alreadyInside = false;
        for (Buses bus : optionsBuses){
            for (Buses originalBus : buses){
                if(bus.getBusName().equalsIgnoreCase(originalBus.getBusName())){
                    alreadyInside = true;
                    break;
                }
            }
            if(!alreadyInside){
                filteredBuses.add(bus);
            }
            alreadyInside = false;
        }

        return filteredBuses;
    }

    public ArrayList<Buses> getBuses() {
        return buses;
    }

    public void setBuses(ArrayList<Buses> buses) {
        this.buses = buses;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public ArrayList<Options> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Options> options) {
        this.options = options;
    }
}
