

package com.app.pojo;

import java.io.Serializable;



public class LocationBean implements Serializable{
    private int locationX;
    private int locationY;
    private int width;
    private int height;

    @Override
    public String toString() {
        return "LocationBean{" +
                "locationX=" + locationX +
                ", locationY=" + locationY +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
