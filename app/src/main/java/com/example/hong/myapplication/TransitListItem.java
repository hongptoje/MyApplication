package com.example.hong.myapplication;

import java.util.List;

/**
 * Created by hong on 2018-03-27.
 */

public class TransitListItem {
    public String totalTime;
    public String totalWalk;
    public String totalDistance;
    public String firstStartStation;
    public String lastEndStation;
    public String busStationCount;
    public String subwayStationCount;
    public String payment;
    public String mapObj;
    public String ArrivalTime;
    public String JsonInfo;


    public TransitListItem(String totalTime, String totalWalk, String totalDistance, String firstStartStation, String lastEndStation, String busStationCount, String subwayStationCount, String payment, String mapObj, String arrivalTime, String JsonInfo) {
        this.totalTime = totalTime;
        this.totalWalk = totalWalk;
        this.totalDistance = totalDistance;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.busStationCount = busStationCount;
        this.subwayStationCount = subwayStationCount;
        this.payment = payment;
        this.mapObj = mapObj;
        this.ArrivalTime = arrivalTime;
        this.JsonInfo = JsonInfo;
    }

    public String getJsonInfo() {
        return JsonInfo;
    }

    public void setJsonInfo(String jsonInfo) {
        JsonInfo = jsonInfo;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalWalk() {
        return totalWalk;
    }

    public void setTotalWalk(String totalWalk) {
        this.totalWalk = totalWalk;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getFirstStartStation() {
        return firstStartStation;
    }

    public void setFirstStartStation(String firstStartStation) {
        this.firstStartStation = firstStartStation;
    }

    public String getLastEndStation() {
        return lastEndStation;
    }

    public void setLastEndStation(String lastEndStation) {
        this.lastEndStation = lastEndStation;
    }

    public String getBusStationCount() {
        return busStationCount;
    }

    public void setBusStationCount(String busStationCount) {
        this.busStationCount = busStationCount;
    }

    public String getSubwayStationCount() {
        return subwayStationCount;
    }

    public void setSubwayStationCount(String subwayStationCount) {
        this.subwayStationCount = subwayStationCount;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getMapObj() {
        return mapObj;
    }

    public void setMapObj(String mapObj) {
        this.mapObj = mapObj;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }
}
