

package com.wallet.ctc.model.blockchain;

import java.util.ArrayList;
import java.util.List;


public class EvmosTransRecordsBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public int total;
        public List<TransactionRecordBean> transaction;
    }


    
    public List<TransactionRecordBean> getRecords() {
        if (null != data && null != data.transaction) {
            return data.transaction;
        } else {
            return new ArrayList<>();
        }
    }
}
