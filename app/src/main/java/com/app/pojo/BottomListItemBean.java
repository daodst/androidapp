

package com.app.pojo;



public class BottomListItemBean {
    public String id;
    public int iconId;
    public String img;
    public String title;

    public BottomListItemBean(String title) {
        this.title = title;
    }

    public BottomListItemBean(String img, String title) {
        this.img = img;
        this.title =title;
    }

    public BottomListItemBean(int imgId, String title) {
        this.iconId = imgId;
        this.title = title;
    }
}
