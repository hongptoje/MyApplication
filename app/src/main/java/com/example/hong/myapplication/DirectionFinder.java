package com.example.hong.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

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
 * Created by hong on 2018-03-22.
 */

public class DirectionFinder {
    //google direction api 사용할때
//    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
//    private static final String GOOGLE_API_KEY = "AIzaSyDPXNrXi-7K3d4zZLy4EXIiJpmsWe1oWyI";
    //Tmap사용할 때
    private static final String DIRECTION_URL_API = "https://api2.sktelecom.com/tmap/routes?version=1&format=json&";
   private static final String TMAP_API_KEY = "e86e9219-ec30-466e-8741-4dc6b9d1d399";
    private DirectionFinderListener listener;
//    private String origin;
//    private String destination;
    private Double endY;
    private Double endX;
    private Double startY;
    private Double startX;
    private String TAG = "MapActivityDirectionFinder";
    List<LatLng> latLng = new ArrayList<>();

//    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
//        this.listener = listener;
//        this.origin = origin;
//        this.destination = destination;
//    }

    public DirectionFinder(DirectionFinderListener listener, Double endY, Double endX, Double startY, Double startX) {
        this.listener = listener;
        this.endY = endY;
        this.endX = endX;
        this.startY = startY;
        this.startX = startX;
    }

    public void executes() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        //google direction api 사용할 때
//        String urlOrigin = URLEncoder.encode(origin, "utf-8");
//        String urlDestination = URLEncoder.encode(destination, "utf-8");
//        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;

        //Tmap 사용할 때

        return DIRECTION_URL_API + "endY=" + endY + "&endX=" + endX + "&startY=" + startY + "&startX=" + startX +"&appKey=" + TMAP_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground");
            String link = params[0];
            try {
                URL url = new URL(link);
                Log.d(TAG, "DownloadRawData: link: "+link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                Log.d(TAG, "DownloadRawData: buffer: "+buffer.toString());
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            Log.d(TAG, "onPostExecute: "+res);
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Google Direction API JSon values
//    private void parseJSon(String data) throws JSONException {
//        if (data == null)
//            return;
//
//        List<Routes> routes = new ArrayList<Routes>();
//        JSONObject jsonData = new JSONObject(data);
//        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
//        for (int i = 0; i < jsonRoutes.length(); i++) {
//            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
//            Routes route = new Routes();
//
//            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
//            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
//            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
//            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
//            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
//            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
//            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
//
//            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
//            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
//            route.endAddress = jsonLeg.getString("end_address");
//            route.startAddress = jsonLeg.getString("start_address");
//            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
//            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
//            route.points = decodePolyLine(overview_polylineJson.getString("points"));
//
//            routes.add(route);
//        }
//
//        listener.onDirectionFinderSuccess(routes);
//    }



    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Routes> routes = new ArrayList<Routes>();
        JSONObject jAr = new JSONObject(data);
        JSONArray features = jAr.getJSONArray("features");
        Log.d(TAG,"featureLength "+features.length());
        for (int i = 0; i < features.length(); i++) {
            JSONObject test2 = features.getJSONObject(i);
            Routes route = new Routes();
            if (i == 0) {
                JSONObject properties = test2.getJSONObject("properties");
                int totalDistance = properties.getInt("totalDistance");
                int totalTime = properties.getInt("totalTime");
                int taxiFare = properties.getInt("taxiFare");
                Log.d(TAG, "parseJSon: index = 0");
                Log.d(TAG, "parseJSon: totalDistance: "+totalDistance);
                Log.d(TAG, "parseJSon: totalTime: "+totalTime);
                Log.d(TAG, "parseJSon: taxiFare: "+taxiFare);
                route.totalDistance = totalDistance;
                route.totalTime = totalTime;
                route.taxiFare = taxiFare;
                route.type = "init";
                routes.add(route);
            }else{
            JSONObject geometry = test2.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            JSONObject properties = test2.getJSONObject("properties");
            String name = properties.getString("name");

            Log.d(TAG, "parseJSon: name: "+name);

            route.routesName = name;

            route.type = "run";

            String geoType = geometry.getString("type");
            if (geoType.equals("Point")){
                double lonJson = coordinates.getDouble(0);
                double latJson = coordinates.getDouble(1);
                String description = properties.getString("description");
                Log.d(TAG, "description: "+ description);
                route.description = description;
                routes.add(route);
                Log.d(TAG, "parseJSon: geoType: Point: lonJson: "+lonJson+" latJson: "+latJson);
            }

            if (geoType.equals("LineString")){
                Log.d(TAG, "coordinatesLength: "+coordinates.length());
                for (int j=0;j<coordinates.length(); j++){
                    JSONArray JLinePoint = coordinates.getJSONArray(j);
                    double lonJson = JLinePoint.getDouble(0);
                    double latJson = JLinePoint.getDouble(1);
                    Log.d(TAG, "parseJSon: geoType: LineString: lonJson: "+lonJson+" latJson: "+latJson);
                    LatLng LL = new LatLng(latJson, lonJson);
                    latLng.add(LL);
//                    route.points = PolyLine(latLng);
                    route.points = latLng;
//                    com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
//                    Log.d(TAG, "parseJSon: point:"+point);
                } routes.add(route);
//                for (int k=0; k<latLng.size(); k++){
//                    Log.d(TAG, "코디네이트 배열 입력 테스트: "+latLng.get(k));
//                }
//                Log.d(TAG, "사이즈 확인: route.points: "+ route.points.size());
                Log.d(TAG, "사이즈 확인: routes.size: "+routes.size());
             }
            }
//            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
//            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
//            route.endAddress = jsonLeg.getString("end_address");
//            route.startAddress = jsonLeg.getString("start_address");
//            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
//            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
//            route.points = decodePolyLine(properties.getString("points"));
//
//            routes.add(route);
        }
        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> PolyLine(final LatLng latLng){
        List<LatLng> polyLine = new ArrayList<LatLng>();
        polyLine.add(new LatLng(latLng.latitude, latLng.longitude));
        Log.d(TAG, "PolyLine: latitude: "+latLng.latitude+" longitude: "+ latLng.longitude);
        return polyLine;
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
