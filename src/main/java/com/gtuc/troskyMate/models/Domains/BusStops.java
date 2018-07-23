package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "busStops")
public class BusStops {

    @GraphId
    private
    Long id ;


    private String busStopName ;

    @Index
    private String [] busStopArea;





    @Index(unique = true)
    private String busStopLocation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String[] getBusStopArea() {
        return busStopArea;
    }

    public void setBusStopArea(String[] busStopArea) {
        this.busStopArea = busStopArea;
    }

    public String getBusStopLocation() {
        return busStopLocation;
    }

    public void setBusStopLocation(String busStopLocation) {
        this.busStopLocation = busStopLocation;
    }
}
