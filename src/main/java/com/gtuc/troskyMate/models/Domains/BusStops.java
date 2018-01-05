package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "busStops")
public class BusStops {

    @GraphId
    private
    Long id ;

    private int busStopId;


    private String busStopName ;

    @Index
    private String [] busStopArea;

    //Routes
    private int circleLapazRoute;

    private int lapazCircleRoute;

    private int circleDomeRoute;

    private int circlebyalajoLapazRoute;

    private int adentaLapazRoute;

    private int lapazMadinaRoute;

    @Index(unique = true)
    private String busStopLocation;


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


    public int getCircleDomeRoute() {
        return circleDomeRoute;
    }

    public void setCircleDomeRoute(int circleDomeRoute) {
        this.circleDomeRoute = circleDomeRoute;
    }

    public int getCirclebyalajoLapazRoute() {
        return circlebyalajoLapazRoute;
    }

    public void setCirclebyalajoLapazRoute(int circlebyalajoLapazRoute) {
        this.circlebyalajoLapazRoute = circlebyalajoLapazRoute;
    }

    public int getAdentaLapazRoute() {
        return adentaLapazRoute;
    }

    public void setAdentaLapazRoute(int adentaLapazRoute) {
        this.adentaLapazRoute = adentaLapazRoute;
    }

    public int getLapazMadinaRoute() {
        return lapazMadinaRoute;
    }

    public void setLapazMadinaRoute(int lapazMadinaRoute) {
        this.lapazMadinaRoute = lapazMadinaRoute;
    }
}
