

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class EvmosGetIncomeHistoryBean extends EvmosHttpBean {

    public Data data;


    public List<Integer> getDeviceHistory() {
        if (null != data) {
            return data.device;
        }
        return null;
    }

    public List<Integer> getDvmHistory() {
        if (null != data) {
            return data.power;
        }
        return null;
    }


    public static class Data {
        public List<Integer> device;
        public List<Integer> power; 
    }
}
