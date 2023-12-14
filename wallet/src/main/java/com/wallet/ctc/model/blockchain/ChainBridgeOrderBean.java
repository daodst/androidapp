

package com.wallet.ctc.model.blockchain;

import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChainBridgeOrderBean{

    
    public boolean isExpand = false;

    
    public String order_key;
    public String seller;
    public String buyer;
    public String payer;
    public String sell_chain;
    public String buy_chain;
    public String amount;
    private String rate;
    public int decimals;
    public long lock_time;
    public long end_lock_time;
    public long cancel_time;
    public long update_time;
    public String deposit_amount;
    public long deposit_lock_time;
    public long deposit_lock_end_time;
    public int deposit_status;
    public int order_type;
    public int execute_status;
    public String hash;   
    public String remarks; 
    public String password;


    
    public long id;
    public long main_order_id;
    public String order_count;
    public String order_amount;
    public int status; 
    public long create_time;
    public String receive_amount;

    public long getMainOrderId(){
        if (main_order_id > 0){
            return main_order_id;
        } else if(id > 0){
            return id;
        } else {
            return main_order_id;
        }
    }

    
    public boolean isMainOrderExchangeIng() {
        return status == 0;
    }

    
    public Map<Integer,String> errorMap;
    public boolean hasErrors() {
        if (null != errorMap && !errorMap.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    
    public boolean hasNoPrivateKeyError(){
        return ChainBridgeErrorInfo.hasNoPrivateKeyError(errorMap);
    }

    
    public boolean hasNodeConnectError() {
        return ChainBridgeErrorInfo.hasNodeConnectError(errorMap);
    }

    
    public String getErrorsInfo(Context context){
        return ChainBridgeErrorInfo.getErrorInfo(context, errorMap);
    }


    
    public String getRate(){
        if(!TextUtils.isEmpty(rate)) {
            try {
                return new BigDecimal(rate).divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP).toPlainString();
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
            return rate;
        } else {
            return rate;
        }
    }

    
    public String getDepositAmount(int decimal){
        if (TextUtils.isEmpty(deposit_amount)){
            return "0";
        } else {
            String amount = AllUtils.getTenDecimalValue(deposit_amount, decimal, 4);
            if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) > 0){
                return amount;
            } else {
                return "0";
            }
        }
    }


    public String getShowBuyChain(){
        if (!TextUtils.isEmpty(buy_chain)) {
            return buy_chain.toUpperCase();
        } else {
            return buy_chain;
        }
    }

    public String getShowSellChain(){
        if (!TextUtils.isEmpty(sell_chain)) {
            return sell_chain.toUpperCase();
        } else {
            return sell_chain;
        }
    }

    
    public long getLockRemainTime() {
        long remainTime = end_lock_time - System.currentTimeMillis() / 1000;
        return  remainTime;
    }

    
    public String getChildStatusShowTitle(Context context) {
        if (order_type == 4){
            
            if(execute_status == 1){
                
                return context.getString(R.string.chain_b_step_status_complete);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_buyer_get_fail);
            } else {
                
                return context.getString(R.string.chain_b_step_status_buyer_confrim);
            }
        } else if(order_type == 5) {
            
            if(execute_status == 1){
                return context.getString(R.string.chain_b_step_status_buyer_has_cancel);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_buyer_cancel_fail);
            } else {
                return context.getString(R.string.chain_b_step_status_buyer_waite_cancel);
            }
        } else if(order_type == 6) {
            
            if(execute_status == 1){
                return context.getString(R.string.chain_b_step_status_seller_has_cancel);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_seller_cancel_fail);
            } else {
                return context.getString(R.string.chain_b_step_status_seller_waite_cancel);
            }

        } else if(order_type == 2){
            
            if(execute_status == 1){
                
                return context.getString(R.string.chain_b_step_status_wait_seller_confirm);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_seller_pay_fail);
            } else {
                return context.getString(R.string.chain_b_step_status_buyer_waite_pay);
            }
        } else if(order_type == 3) {
            
            if(execute_status == 1){
                return context.getString(R.string.chain_b_step_status_buyer_confrim);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_seller_get_fail);
            } else {
                return context.getString(R.string.chain_b_step_status_seller_waite_get);
            }
        } else if(order_type == 1){
            if(execute_status == 1){
                return context.getString(R.string.chain_b_step_status_wait_buyer_pay);
            } else if(execute_status == 3){
                return context.getString(R.string.chain_b_step_status_buyer_pre_fail);
            } else {
                return context.getString(R.string.chain_b_step_status_seller_pre_ing);
            }
        } else {
            return context.getString(R.string.unkown_status);
        }
    }

    
    public int getChildStatusTitleColorRes(){
        if (order_type == 4 && execute_status == 1){
            
            return R.color.default_theme_color;
        } else if(execute_status == 3){
            
            return R.color.default_hint_text_color;
        } else {
            
            return R.color.default_tip_color;
        }
    }

    
    public boolean isChildOrderOver(){
        if((order_type == 1 || order_type == 2 || order_type == 3) && execute_status == 3){
            
            return true;
        }
        if ((order_type == 4 || order_type == 5 || order_type == 6) && (execute_status == 1 || execute_status == 3)){
            return true;
        } else {
            return false;
        }
    }

    public int getChildStatusImgRes(){
        if (order_type == 4 && execute_status == 1){
            return R.mipmap.icon_chain_bridge_complete;
        } else if(execute_status == 3){
            return R.mipmap.icon_chain_bridge_cancel;
        } else {
            return R.mipmap.icon_chain_bridge_ing;
        }
    }


    
    public List<ChainBridgeDetailStepEntity> getTimeLineSteps(Context context, int index){
        List<ChainBridgeDetailStepEntity> stepList = new ArrayList<>();
        String orderIndexStr = context.getString(R.string.pipei_dingdan_title)+index+"：";
        stepList.add(new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_buyer_yuyue), "--", ""));
        ChainBridgeDetailStepEntity buyerPayStep = new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_buyer_pay),
                "--", "");
        ChainBridgeDetailStepEntity sellerGetPayStep = new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_seller_queren),
                "--", "");
        ChainBridgeDetailStepEntity buyerGetPayStep = new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_buyer_confirm),
                "--", "");
        if (order_type == 4){
            
            
            stepList.add(buyerPayStep);
            stepList.add(sellerGetPayStep);
            buyerGetPayStep.setWithdrawCode(hash);
            stepList.add(buyerGetPayStep);
        } else if(order_type == 5) {
            
            
            stepList.add(buyerPayStep);
            ChainBridgeDetailStepEntity buyerCancelStep = new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_buyer_cancel),
                    "--", hash);
            buyerCancelStep.setAlert(true);
            stepList.add(buyerCancelStep);

        } else if(order_type == 6){
            
            ChainBridgeDetailStepEntity sellerCancelStep = new ChainBridgeDetailStepEntity(true, orderIndexStr+context.getString(R.string.chain_b_step_seller_cancel),
                    "--", hash);
            sellerCancelStep.setAlert(true);
            stepList.add(sellerCancelStep);

        } else {
            
            
            if(order_type == 2){
                
                buyerPayStep.setCurrent(true);
                buyerPayStep.setWithdrawCode(hash);
                stepList.add(buyerPayStep);
            } else if(order_type == 3){
                
                buyerPayStep.setCurrent(true);
                sellerGetPayStep.setCurrent(true);
                sellerGetPayStep.setWithdrawCode(hash);

                stepList.add(buyerPayStep);
                stepList.add(sellerGetPayStep);
            }
        }
        return stepList;

    }

    
    public List<ChainBridgeDetailStepEntity> getChildOrderSteps(Context context) {
        List<ChainBridgeDetailStepEntity> stepList = new ArrayList<>();
        AssertBean buyExAssert = WalletUtil.getUsdtAssert(ChatSdk.chainNameToType(buy_chain));


        stepList.add(new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_buyer_yuyue),
                String.format(context.getString(R.string.chain_b_step_buyer_yuyue_tip),  getShowBuyChain()), ""));
        ChainBridgeDetailStepEntity buyerPayStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_buyer_pay),
                String.format(context.getString(R.string.chain_b_step_buyer_pay_tip), getShowBuyChain() , buyExAssert.getShort_name().toUpperCase()), "");
        ChainBridgeDetailStepEntity sellerGetPayStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_seller_queren),
                String.format(context.getString(R.string.chain_b_step_seller_queren_tip), getShowSellChain()), "");
        ChainBridgeDetailStepEntity buyerGetPayStep = new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_buyer_confirm),
                String.format(context.getString(R.string.chain_b_step_buyer_confirm_tip), getShowBuyChain()), "");

        if (order_type == 4){
            
            
            stepList.add(buyerPayStep);
            sellerGetPayStep.setWithdrawCode(password);
            stepList.add(sellerGetPayStep);
            if(execute_status == 0 || execute_status == 2 || execute_status == 3){
                
                buyerGetPayStep.setCurrent(false);
            }
            stepList.add(buyerGetPayStep);
        } else if(order_type == 5) {
            
            
            stepList.add(buyerPayStep);
            stepList.add(new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_buyer_cancel),
                    String.format(context.getString(R.string.chain_b_step_buyer_cancel_tip), getShowBuyChain()), ""));

        } else if(order_type == 6){
            
            stepList.add(new ChainBridgeDetailStepEntity(true, context.getString(R.string.chain_b_step_seller_cancel),
                    String.format(context.getString(R.string.chain_b_step_seller_cancel_tips), getShowSellChain()), ""));

        } else {
            
            
            if(order_type == 2){
                
                buyerPayStep.setCurrent(true);
                sellerGetPayStep.setCurrent(false);
                buyerGetPayStep.setCurrent(false);
            } else if(order_type == 3){
                
                buyerPayStep.setCurrent(true);
                sellerGetPayStep.setCurrent(true);
                sellerGetPayStep.setCountdown(getLockRemainTime());
                buyerGetPayStep.setCurrent(false);
            }
            stepList.add(buyerPayStep);
            stepList.add(sellerGetPayStep);
            stepList.add(buyerGetPayStep);
        }
        return stepList;
    }


    
    public String getStatusName(Context context){
        if(status == 0){
            return context.getString(R.string.chain_b_order_ing);
        } else if(status == 1){
            return context.getString(R.string.chain_b_order_some_over);
        } else if(status == 2){
            return context.getString(R.string.chain_b_order_all_over);
        } else if(status == 3){
            return context.getString(R.string.chain_b_order_has_cancel);
        } else {
            return context.getString(R.string.chain_b_order_unknow);
        }
    }
}
