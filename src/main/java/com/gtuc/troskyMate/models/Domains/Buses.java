package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity(label = "buses")
public class Buses {
    @GraphId
    Long id;
    @Index(unique = true)
    private Long busId ;

    @Index(unique = true)
    private String busName;

    private String busOrigin;

    private String busDestination;

//    @Relationship(type="hasBus", direction = "INCOMING")
//    Set<Buses> hasBus = new HashSet<Buses>();
//
//    @Relationship(type="stopAt")
//    Set<Buses> stopAt = new HashSet<Buses>();


    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusOrigin() {
        return busOrigin;
    }

    public void setBusOrigin(String busOrigin) {
        this.busOrigin = busOrigin;
    }

    public String getBusDestination() {
        return busDestination;
    }

    public void setBusDestination(String busDestination) {
        this.busDestination = busDestination;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
