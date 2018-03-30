package com.example.hong.myapplication;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by hong on 2018-03-16.
 */

public class CustomClusterRenderer extends DefaultClusterRenderer<House>{
    private final Context mContext;
    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<House> clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(House item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
