package com.app.root.wallet;


public interface OnClickDetectorListener {

    
    void onClick(String type);

    
    void onLongClick(String type);

    
    void onHit(String fromType, String toType);


    
    default void onTouchUp(String type){

    }


}
