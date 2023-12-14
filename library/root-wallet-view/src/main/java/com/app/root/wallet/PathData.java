package com.app.root.wallet;

import android.graphics.Path;
import android.graphics.Rect;


public class PathData {

    public Rect rect;
    public String type;
    public Path path;
    public int centerX;
    public int centerY;
    public int radios; 
    public boolean selected;
    public boolean isFirstSelected;
    public String toType;
    public double[] xyArray;
    public int viewIndex;
    public boolean isToEdge;
    public boolean isSelecteChanged;

    public PathData(String type, Rect rect, Path path) {
        this.type = type;
        this.rect = rect;
        this.path = path;
        this.centerX = rect.centerX();
        this.centerY = rect.centerY();
        this.radios = rect.height() /2;
    }

    public void setSelected(boolean selected) {
        if (selected && !this.selected) {
            this.isFirstSelected = true;
        } else {
            this.isFirstSelected = false;
        }
        if (selected != this.selected) {
            this.isSelecteChanged = true;
        } else {
            this.isSelecteChanged = false;
        }
        this.selected = selected;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public void updateData(PathData newData) {
        if (null == newData) {
            return;
        }
        this.rect = newData.rect;
        this.path = newData.path;
        this.centerX = newData.centerX;
        this.centerY = newData.centerY;
        this.radios = newData.radios;
        this.toType = newData.toType;
        this.isToEdge = newData.isToEdge;
        setSelected(newData.selected);
    }
}
