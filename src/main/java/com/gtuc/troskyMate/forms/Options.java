package com.gtuc.troskyMate.forms;

import com.gtuc.troskyMate.models.Domains.Buses;

import java.util.ArrayList;
import java.util.List;

public class Options {

    private int pathId;

    private List<Buses> otherBuses = new ArrayList<Buses>();


    public int getPathId() {
        return pathId;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }

    public List<Buses> getOtherBuses() {
        return otherBuses;
    }

    public void setOtherBuses(List<Buses> otherBuses) {
        this.otherBuses = otherBuses;
    }

    public void addOtherBuses(Buses newBus){
        boolean isSimilar = false;
        for (Buses bus : this.otherBuses){
            if(newBus.getBusName().equalsIgnoreCase(bus.getBusName())){
                isSimilar = true;
            }
        }

        if(!isSimilar){
            this.otherBuses.add(newBus);
        }
    }
}
