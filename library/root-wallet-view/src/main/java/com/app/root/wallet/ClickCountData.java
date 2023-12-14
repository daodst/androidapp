package com.app.root.wallet;


public class ClickCountData {
    public String viewType;
    public int count;
    private float scale = 0;
    public boolean isUpdated = false;
    public ClickCountData(String type, int count) {
        this.viewType = type;
        this.count = count;
    }

    public boolean updateScale(float newScale) {
        boolean update = false;
        if (newScale > 0  && scale != newScale) {
            update = true;
            scale = newScale;
        }
        isUpdated = update;
        return update;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "ClickCountData{" +
                "viewType='" + viewType + '\'' +
                ", count=" + count +
                ", scale=" + scale +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
