package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.util.List;

import common.app.utils.AllUtils;


public class BlockDepositBean {

    private String height; 
    private DataResult deposit_params;

    public class DataResult {
        public List<EvmosAmountsBean> min_deposit; 
        private String max_deposit_period;          
    }

    public String getHeight() {
        return height;
    }

    public EvmosAmountsBean getMinDeposit() {
        
        if (null != deposit_params && deposit_params.min_deposit != null && deposit_params.min_deposit.size() > 0) {
            return deposit_params.min_deposit.get(0);
        }
        return null;
    }

    
    public String getMinAmount() {
        EvmosAmountsBean amountsBean = getMinDeposit();
        if (null != amountsBean && !TextUtils.isEmpty(amountsBean.amount)) {
            
            return AllUtils.getTenDecimalValue(amountsBean.amount, 18, 18);
        }
        return "";
    }

    
    public String getCoinName() {
        EvmosAmountsBean amountsBean = getMinDeposit();
        if (null != amountsBean) {
            return amountsBean.denom;
        }
        return "";
    }
}
