package com.example.hong.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hong on 2018-03-25.
 */

public class TransitDirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI";
    private static final String ODSAY_URL_API = "https://api.odsay.com/v1/api/searchPubTransPath?lang=0&OPT=0";
    private static final String ODSAY_API_KEY = "qZ53QAfoVn+Qbb1virh0sq2k8r0TLHSIlOqsVDTIypw";
    private TransitDirectionFinderListener listener;
    private String origin;
    private String destination;
    private Double endX;
    private Double endY;
    private Double startX;
    private Double startY;
    private String TAG = "TransitDirectionFinder";
    List<LatLng> latLngs = new ArrayList<>();

    public TransitDirectionFinder(TransitDirectionFinderListener listener, Double startX, Double startY, Double endX, Double endY){
        this.listener = listener;
        this.endX = endX;
        this.endY = endY;
        this.startX = startX;
        this.startY = startY;
    }

    public void transitExcute() throws UnsupportedEncodingException{
        listener.onTransitDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException{
//        String urlOrigin = URLEncoder.encode(origin, "utf-8");
//        String urlDestination = URLEncoder.encode(destination, "utf-8");
        destination = endX+","+endY;
        origin = startX+","+startY;
        return  ODSAY_URL_API + "&SX=" + startX + "&SY=" + startY +"&EX=" + endX + "&EY=" + endY;
//        return DIRECTION_URL_API + "origin=" + origin + "&destination=" + destination + "&mode=transit" +"&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try{
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line=reader.readLine()) != null){
                    buffer.append(line + "\n");
                }
                Log.d(TAG, "DownloadRawData: buffer: "+buffer.toString());
                return buffer.toString();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                parseJSon(s);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException{
        if (data ==null)
            return;;

                List<TransitRoute> routes = new ArrayList<TransitRoute>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            TransitRoute route = new TransitRoute();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.transitDistance = new TransitDistance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.transitDuration = new TransitDuration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.transitEndAddress = jsonLeg.getString("end_address");
            route.transitStartAddress = jsonLeg.getString("start_address");
            route.transitStartLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.transitEndLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
//            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        listener.onTransitDirectionFinderSuccess(routes);
    }
}
