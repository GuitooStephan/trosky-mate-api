package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@NodeEntity(label = "buses")
public class Buses implements Serializable {
    private static final long serialVersionUID = 1L;

    @GraphId
    Long id;

    @Index(unique = true)
    private String busName;

    @Index(unique= true)
    private String busDestination;

    @Relationship(type="stopAt")
    private List<BusStops> busStops = new ArrayList<>();



    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusDestination() {
        return busDestination;
    }

    public void setBusDestination(String busDestination) {
        this.busDestination = busDestination;
    }
}
