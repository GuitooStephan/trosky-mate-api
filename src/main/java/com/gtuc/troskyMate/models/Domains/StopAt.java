package com.gtuc.troskyMate.models.Domains;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "ACTED_IN")
public class StopAt {
    @GraphId
    private Long id;

    @StartNode
    private Buses bus;
    @EndNode
    private BusStops busStop;
}
