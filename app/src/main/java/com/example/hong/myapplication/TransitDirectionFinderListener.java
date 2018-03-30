package com.example.hong.myapplication;

import java.util.List;

/**
 * Created by hong on 2018-03-25.
 */

public interface TransitDirectionFinderListener {
    void onTransitDirectionFinderStart();
    void onTransitDirectionFinderSuccess(List<TransitRoute> route);
}
