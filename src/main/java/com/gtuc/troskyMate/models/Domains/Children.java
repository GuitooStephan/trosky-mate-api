package com.gtuc.troskyMate.models.Domains;

import java.util.ArrayList;
import java.util.List;

public class Children {

    private List<Node> children = new ArrayList<Node>();


    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void add(Node child){
        children.add(child);
    }

    public int size(){
        return children.size();
    }
}
