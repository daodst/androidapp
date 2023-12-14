package com.benny.openlauncher.activity;

import com.benny.openlauncher.model.Item;


public interface HomeActivityListener {
    boolean isLogined();

    
    void showFloatingButton(int visible);

    
    void floatButtonClick(boolean needScroll);

    
    void showChatFragment();

    
    void showCallPhoneFragment();

    
    void showWalletFragment();

    
    void showMeFragment();

    void onStableItemClick(Item item);

    
    void startCycleTask();

    
    void onAlarmTick();


    
    void onRootWalletTouch(String from, String to);

}
