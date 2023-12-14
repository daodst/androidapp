package com.app.home.pojo;



public class PledgedTokensDidListEntity {
    public String id;
    public String number;
    public boolean isDefaultSegment = false;
    public boolean selected = false;
    public boolean canSelect = true;

    public PledgedTokensDidListEntity() {
    }

    public PledgedTokensDidListEntity(String pId, String pNumber) {
        id = pId;
        number = pNumber;
    }

    public void setCanSelect(boolean pCanSelect) {
        
        if (selected) canSelect = true;
        else canSelect = pCanSelect;
    }
}
