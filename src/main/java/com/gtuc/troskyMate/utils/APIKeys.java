package com.gtuc.troskyMate.utils;

import org.springframework.stereotype.Component;

@Component
//Class for the API keys
public class APIKeys {

    //Distance Matrix API Key
    private String distanceMatrixKey = "AIzaSyCUjekBM_xY-nzmgYVT6e44gMIKas8R-LM";


    public String getDistanceMatrixKey() {
        return distanceMatrixKey;
    }

    public void setDistanceMatrixKey(String distanceMatrixKey) {
        this.distanceMatrixKey = distanceMatrixKey;
    }
}
