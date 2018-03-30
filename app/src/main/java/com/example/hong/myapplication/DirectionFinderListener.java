package com.example.hong.myapplication;

import java.util.List;

import okhttp3.Route;

/**
 * Created by hong on 2018-03-22.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Routes> route);
}
