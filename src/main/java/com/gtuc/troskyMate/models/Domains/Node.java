package com.gtuc.troskyMate.models.Domains;

import java.util.ArrayList;
import java.util.List;

public class Node{
    private List<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private BusStops data = null;

    public Node(BusStops data) {
        this.data = data;
    }

    public Node(BusStops data, Node parent) {
        this.data = data;
        this.parent = parent;
    }

    public void setParent(Node parent) {
        if (isRoot()){
            this.parent = parent;
        } else {
            parent.addChild(this);
            this.parent = parent;
        }
    }

    public void addChild(BusStops data) {
        Node child = new Node(data);
        child.setParent(this);
        this.getChildren().add(child);
    }

    public void addChild(Node child) {
        child.setParent(this);
        this.getChildren().add(child);
    }

    public BusStops getData() {
        return this.data;
    }

    public void setData(BusStops data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        if(this.getChildren().size() == 0)
            return true;
        else
            return false;
    }

    public void removeParent() {
        this.parent = null;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
