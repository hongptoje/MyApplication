package com.example.hong.myapplication;

/**
 * Created by hong on 2018-03-25.
 */

public class FindDirectionItem {
    private String directionInfo;
    private int directionImage;

    public FindDirectionItem(String directionInfo, int directionImage) {
        this.directionInfo = directionInfo;
        this.directionImage = directionImage;
    }

    public String getDirectionInfo() {
        return directionInfo;
    }

    public void setDirectionInfo(String directionInfo) {
        this.directionInfo = directionInfo;
    }

    public int getDirectionImage() {
        return directionImage;
    }

    public void setDirectionImage(int directionImage) {
        this.directionImage = directionImage;
    }
}
