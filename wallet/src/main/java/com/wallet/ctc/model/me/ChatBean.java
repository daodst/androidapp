

package com.wallet.ctc.model.me;



public class ChatBean {
    private int type;
    private String id;
    private String title;
    private boolean bussines = false;

    public ChatBean(int type, String id, String title, boolean bussines) {
        this.type = type;
        this.setId(id);
        this.title = title;
        this.bussines = bussines;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBussines() {
        return bussines;
    }

    public void setBussines(boolean bussines) {
        this.bussines = bussines;
    }
}
