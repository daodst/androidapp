package com.wallet.ctc.model.blockchain;

import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;


public class DeviceGroupPageData {

    public EvmosGroupDataBean groupData;
    public EvmosMyGroupDataBean myData;


    public boolean isSuccess() {
        if (null != groupData && groupData.isSuccess() && myData != null && myData.isSuccess()) {
            return true;
        } else {
            return false;
        }
    }

    public String getErrorInfo() {
        if (null == groupData) {
            return "get groupData is null";
        }
        if (!groupData.isSuccess()) {
            return groupData.getInfo();
        }

        if (null == myData) {
            return "get myGroupData is null";
        }
        if (!myData.isSuccess()) {
            return myData.getInfo();
        }
        return "get data fail";
    }
}
