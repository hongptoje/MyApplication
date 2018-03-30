package com.example.hong.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by hong on 2018-03-25.
 */

public class TransitRoute {
    public TransitDistance transitDistance;
    public TransitDuration transitDuration;
    public String transitEndAddress;
    public LatLng transitEndLocation;
    public String transitStartAddress;
    public LatLng transitStartLocation;

    public List<LatLng> transitPoint;
}
