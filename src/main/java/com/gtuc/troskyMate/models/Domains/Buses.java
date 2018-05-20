package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity(label = "buses")
public class Buses {
    @GraphId
    Long id;

    @Index(unique = true)
    private String busName;

    @Index(unique= true)
    private String busDestination;

//    @Relationship(type="hasBus", direction = "INCOMING")
//    Set<Buses> hasBus = new HashSet<Buses>();
//
//    @Relationship(type="stopAt")
//    Set<Buses> stopAt = new HashSet<Buses>();



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
