

package com.wallet.ctc.model.blockchain;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.wallet.ctc.R;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;

import java.util.ArrayList;
import java.util.List;


public class ChainBridgeOrderDetailBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;

    
    public List<ChainBridgeOrderBean> getOrders(){
        if (null != data){
            return data.order;
        } else {
            return null;
        }
    }

    
    public ChainBridgeOrderBean getLastChildOrder(){
        List<ChainBridgeOrderBean> list = getOrders();
        if (list == null || list.size() == 0){
            return null;
        }
        return list.get(list.size()-1);
    }

    
    public ChainBridgeOrderBean getMainOrder() {
        if(null != data){
            return data.main_order;
        } else {
            return null;
        }
    }

    
    public boolean isMainOrderExchangeIng() {
        ChainBridgeOrderBean mainOrder = getMainOrder();
        if (mainOrder != null){
            return mainOrder.isMainOrderExchangeIng();
        } else {
            return false;
        }
    }

    
    public long getAllUseTimeSeconds(){
        ChainBridgeOrderBean mainOrder = getMainOrder();
        if (mainOrder == null){
            return -1;
        }
        ChainBridgeOrderBean lastChildOrder = getLastChildOrder();
        if (lastChildOrder == null){
            return -1;
        }
        return lastChildOrder.update_time-mainOrder.create_time;
    }


    
    public List<ChainBridgeDetailStepEntity> getTimeLineSteps(Context context){
        ChainBridgeOrderBean mainOrder = getMainOrder();
        if (null == mainOrder){
            return new ArrayList<>();
        }
        List<ChainBridgeDetailStepEntity> list = new ArrayList<>();
        list.add(new ChainBridgeDetailStepEntity(true, context.getString(R.string.status_started), "--", ""));
        list.add(new ChainBridgeDetailStepEntity(true, context.getString(R.string.status_xieshang_ex), "--", ""));
        List<ChainBridgeOrderBean> childOrders = getOrders();
        if (childOrders != null && childOrders.size() > 0){
            for(int i=0; i<childOrders.size(); i++){
                ChainBridgeOrderBean childOrder = childOrders.get(i);
                int index = i+1;
                List<ChainBridgeDetailStepEntity> childList = childOrder.getTimeLineSteps(context, index);
                if (childList != null && childList.size() > 0){
                    list.addAll(childList);
                }
            }
        }
        if (mainOrder.status == 0){
            
            if (mainOrder.hasNoPrivateKeyError()){
                
                ChainBridgeDetailStepEntity pausStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_bridge_status_pause), "--", "");
                pausStep.setAlert(true);
                list.add(pausStep);
            } else {
                
            }
        } else {
            
            if (mainOrder.status == 3){
                
                ChainBridgeDetailStepEntity cancelStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_order_has_cancel), "--", "");
                cancelStep.setAlert(true);
                list.add(cancelStep);
            } else {
                
                ChainBridgeDetailStepEntity completeStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.status_complete), "--", "");
                list.add(completeStep);
            }
        }
        return list;
    }

    public static class Data {
        public List<ChainBridgeOrderBean> order;
        public ChainBridgeOrderBean main_order; 
    }
}
