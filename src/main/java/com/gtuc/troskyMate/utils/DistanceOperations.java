package com.gtuc.troskyMate.utils;

import org.springframework.stereotype.Component;

import com.gtuc.troskyMate.utils.Constants;

import static java.lang.Double.parseDouble;

@Component
public class DistanceOperations {
    /**
     * This method calculates the distance between two coordinates
     * @param firstCoordinate
     * @param secondCoordinate
     * @return int
     */
    public int distance(String firstCoordinate, String secondCoordinate) {

        //Get the first and second latitude
        double lat1 = getLatitude(firstCoordinate);
        double lat2 = getLatitude(secondCoordinate);

        //Get the first and second longitude
        double lon1 = getLongitude(firstCoordinate);
        double lon2 = getLongitude(secondCoordinate);

        final int R = Constants.R; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters;

        return (int) distance;
    }

    /**
     * This method returns the latitude in coordinates
     * @param coordinates
     * @return double
     */
    private double getLatitude(String coordinates){
        String [] coordinatesParts = coordinates.split(",",2);
        return parseDouble(coordinatesParts[0]);
    }

    /**
     * This method returns the longitude in coordinates
     * @param coordinates
     * @return double
     */
    private double getLongitude(String coordinates){
        String [] coordinatesParts = coordinates.split(",", 2);
        return parseDouble(coordinatesParts[1]);
    }
}
