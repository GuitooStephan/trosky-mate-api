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

    //Routes
    private int circleLapazRoute;

    private int lapazCircleRoute;

    private int circlebyalajoLapazRoute;

    private int adentaLapazRoute;

    private int lapazMadinaRoute;

    private int accrautcLapazRoute;

    private int ashaiamanCircleoverheadRoute;

    private int circleoverheadAshaiamanRoute;

    private int domeLegonRoute;

    private int lapaz37Route;

    private int lapazDomeRoute;

    private int lapazTeshienunguaRoute;

    private int sakumonoestateLapazRoute;

    private int teshienunguaLapazRoute;

    private int lapazSakumonojunctionRoute;

    private int circleDomekwabenyaRoute;

    private int legonDomeRoute;

    private int nimaoverheadPokuaseamasamanRoute;



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

    public int getAccrautcLapazRoute() {
        return accrautcLapazRoute;
    }

    public void setAccrautcLapazRoute(int accrautcLapazRoute) {
        this.accrautcLapazRoute = accrautcLapazRoute;
    }

    public int getAshaiamanCircleoverheadRoute() {
        return ashaiamanCircleoverheadRoute;
    }

    public void setAshaiamanCircleoverheadRoute(int ashaiamanCircleoverheadRoute) {
        this.ashaiamanCircleoverheadRoute = ashaiamanCircleoverheadRoute;
    }

    public int getCircleoverheadAshaiamanRoute() {
        return circleoverheadAshaiamanRoute;
    }

    public void setCircleoverheadAshaiamanRoute(int circleoverheadAshaiamanRoute) {
        this.circleoverheadAshaiamanRoute = circleoverheadAshaiamanRoute;
    }

    public int getDomeLegonRoute() {
        return domeLegonRoute;
    }

    public void setDomeLegonRoute(int domeLegonRoute) {
        this.domeLegonRoute = domeLegonRoute;
    }

    public int getLapaz37Route() {
        return lapaz37Route;
    }

    public void setLapaz37Route(int lapaz37Route) {
        this.lapaz37Route = lapaz37Route;
    }

    public int getLapazDomeRoute() {
        return lapazDomeRoute;
    }

    public void setLapazDomeRoute(int lapazDomeRoute) {
        this.lapazDomeRoute = lapazDomeRoute;
    }

    public int getLapazTeshienunguaRoute() {
        return lapazTeshienunguaRoute;
    }

    public void setLapazTeshienunguaRoute(int lapazTeshienunguaRoute) {
        this.lapazTeshienunguaRoute = lapazTeshienunguaRoute;
    }

    public int getSakumonoestateLapazRoute() {
        return sakumonoestateLapazRoute;
    }

    public void setSakumonoestateLapazRoute(int sakumonoestateLapazRoute) {
        this.sakumonoestateLapazRoute = sakumonoestateLapazRoute;
    }

    public int getTeshienunguaLapazRoute() {
        return teshienunguaLapazRoute;
    }

    public void setTeshienunguaLapazRoute(int teshienunguaLapazRoute) {
        this.teshienunguaLapazRoute = teshienunguaLapazRoute;
    }

    public int getLapazSakumonojunctionRoute() {
        return lapazSakumonojunctionRoute;
    }

    public void setLapazSakumonojunctionRoute(int lapazSakumonojunctionRoute) {
        this.lapazSakumonojunctionRoute = lapazSakumonojunctionRoute;
    }

    public int getCircleDomekwabenyaRoute() {
        return circleDomekwabenyaRoute;
    }

    public void setCircleDomekwabenyaRoute(int circleDomekwabenyaRoute) {
        this.circleDomekwabenyaRoute = circleDomekwabenyaRoute;
    }

    public int getLegonDomeRoute() {
        return legonDomeRoute;
    }

    public void setLegonDomeRoute(int legonDomeRoute) {
        this.legonDomeRoute = legonDomeRoute;
    }

    public int getNimaoverheadPokuaseamasamanRoute() {
        return nimaoverheadPokuaseamasamanRoute;
    }

    public void setNimaoverheadPokuaseamasamanRoute(int nimaoverheadPokuaseamasamanRoute) {
        this.nimaoverheadPokuaseamasamanRoute = nimaoverheadPokuaseamasamanRoute;
    }
}
