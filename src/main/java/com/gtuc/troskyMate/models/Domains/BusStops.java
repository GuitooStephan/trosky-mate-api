package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.mongodb.core.index.Indexed;

@NodeEntity(label = "busStops")
public class BusStops {

    @GraphId
    private
    Long id ;

    private int busStopId;

    @Index(unique = true)
    private String busStopName ;

    private String [] busStopArea;

    @Index(unique = true)
    private int circleLapazRoute;

    @Index(unique = true)
    private int lapazCircleRoute;

    @Index(unique = true)
    private int lapazSakumonojunctionRoute;

    private String busStopLocation;

    private int onBusIndexRoute;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(int busStopId) {
        this.busStopId = busStopId;
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

    public int getCircleLapazRoute() {
        return circleLapazRoute;
    }

    public void setCircleLapazRoute(int circleLapazRoute) {
        this.circleLapazRoute = circleLapazRoute;
    }

    public int getLapazCircleRoute() {
        return lapazCircleRoute;
    }

    public void setLapazCircleRoute(int lapazCircleRoute) {
        this.lapazCircleRoute = lapazCircleRoute;
    }

    public String getBusStopLocation() {
        return busStopLocation;
    }

    public void setBusStopLocation(String busStopLocation) {
        this.busStopLocation = busStopLocation;
    }

    public int getLapazSakumonojunctionRoute() {
        return lapazSakumonojunctionRoute;
    }

    public void setLapazSakumonojunctionRoute(int lapazSakumonojunctionRoute) {
        this.lapazSakumonojunctionRoute = lapazSakumonojunctionRoute;
    }

    public int getOnBusIndexRoute() {
        return onBusIndexRoute;
    }

    public void setOnBusIndexRoute(int onBusIndexRoute) {
        this.onBusIndexRoute = onBusIndexRoute;
    }
}
