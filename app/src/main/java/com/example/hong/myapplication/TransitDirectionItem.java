package com.example.hong.myapplication;

/**
 * Created by hong on 2018-03-29.
 */

public class TransitDirectionItem {
    int transitDirectionImage;
    String transitDirectionTitleInfo;
    String transitDirectionSubtitleInfo;
    String transitDuration;

    public TransitDirectionItem(int transitDirectionImage, String transitDirectionTitleInfo, String transitDirectionSubtitleInfo, String transitDuration) {
        this.transitDirectionImage = transitDirectionImage;
        this.transitDirectionTitleInfo = transitDirectionTitleInfo;
        this.transitDirectionSubtitleInfo = transitDirectionSubtitleInfo;
        this.transitDuration = transitDuration;
    }

    public String getTransitDuration() {
        return transitDuration;
    }

    public void setTransitDuration(String transitDuration) {
        this.transitDuration = transitDuration;
    }

    public int getTransitDirectionImage() {
        return transitDirectionImage;
    }

    public void setTransitDirectionImage(int transitDirectionImage) {
        this.transitDirectionImage = transitDirectionImage;
    }

    public String getTransitDirectionTitleInfo() {
        return transitDirectionTitleInfo;
    }

    public void setTransitDirectionTitleInfo(String transitDirectionTitleInfo) {
        this.transitDirectionTitleInfo = transitDirectionTitleInfo;
    }

    public String getTransitDirectionSubtitleInfo() {
        return transitDirectionSubtitleInfo;
    }

    public void setTransitDirectionSubtitleInfo(String transitDirectionSubtitleInfo) {
        this.transitDirectionSubtitleInfo = transitDirectionSubtitleInfo;
    }
}
