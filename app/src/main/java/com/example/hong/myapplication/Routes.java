package com.example.hong.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hong on 2018-03-22.
 */

public class Routes {
    public String type;
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public int totalDistance;
    public int totalTime;
    public int taxiFare;
    public String routesName;
    public String description;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
//    public LatLng points;
//    public ArrayList<LatLng> points;
}

