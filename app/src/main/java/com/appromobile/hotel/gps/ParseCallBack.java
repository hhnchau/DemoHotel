package com.appromobile.hotel.gps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by xuan on 8/3/2016.
 */
public interface ParseCallBack {
    void onFinishedParser(PolylineOptions polylineOptions, LatLng fristPoint, LatLng endPoint);
}
