package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;


public class EvmosUserBean {
    public String address;
    public String comm_address;
    public String mobile;


    
    public String nickName;
    public String logo;
    public boolean isEffect = true;
    public long update_time;


    public EvmosUserBean() {

    }

    public EvmosUserBean(String address, String comm_address, String mobile, boolean isEffect) {
        this.address = address;
        this.comm_address = comm_address;
        this.mobile = mobile;
        this.isEffect = isEffect;
    }


    
    public String getAvatorUid() {
        if (!TextUtils.isEmpty(comm_address) && comm_address.startsWith("@")) {
            return comm_address;
        }
        return "@"+comm_address;
    }

    
    public boolean containsKey(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return false;
        }
        if (!TextUtils.isEmpty(nickName) && nickName.contains(keyword)) {
            return true;
        }
        if (!TextUtils.isEmpty(address) && address.contains(keyword)) {
            return true;
        }
        if (!TextUtils.isEmpty(mobile) && mobile.contains(keyword)) {
            return true;
        }
        return false;
    }



}
