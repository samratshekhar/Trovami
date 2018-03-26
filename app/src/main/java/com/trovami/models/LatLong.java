package com.trovami.models;

/**
 * Created by samrat on 23/03/18.
 */

public class LatLong {

    public double lat;
    public double lon;
    public String timeStamp;

    public LatLong() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
}
