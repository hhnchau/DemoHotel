package com.appromobile.hotel.model.view;

import android.location.Location;

/**
 * Created by thanh on 2/14/2017.
 */

public class BroadcastForm {
    private Location currentLocation;
    private String address;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
