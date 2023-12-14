

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChainBridgeMainOrdersBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public List<Data> data;

    
    public List<ChainBridgeOrderBean> getMainOrderList(){
        List<ChainBridgeOrderBean> list = new ArrayList<>();
        if (null != data && data.size() > 0){
            for (Data d : data){
                if (null != d.main_order){
                    ChainBridgeOrderBean order = d.main_order;
                    order.errorMap = d.error;
                    list.add(order);
                }
            }
        }
        return list;
    }


    
    public static class Data{
        public ChainBridgeOrderBean main_order;
        public Map<Integer,String> error;
    }
}
