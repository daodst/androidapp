package com.wallet.ctc.model.blockchain;

import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.R;

import java.util.List;


public class EvmosPledgeConfigBean {

    public String min_burn_amount;  
    
    public String hash_pledge;
    
    public String dst_burn;
    
    public int undelegate_level;

    public String pledgeFee; 
    public String hasNum; 


    public boolean isSuccess;
    public String errorInfo;
    public String available;
    
    public String ratio;
    public String pledge_hash_get;
    public String burn_address;

    public boolean isHasRegisted;

    private String levelName;
    public int level = 0;
    private int levelDrawableRes;
    public String levelDesc;

    public List<String> phoneStartList;
    public List<String> myMobileList;

    public String tokenName;  
    public String tokenNameDestory;  

    public String tokenBalance;

    public String prePledgeNum;
    public String totalHasPledgeNum;
    public String remainPledgeNum;

    public String minPledgeNum;
    public String minPledgeNumShow;
    public String pledgeAddress;

    public String canWithdrawNum;


    public int decimal = 18;
    public List<EvmosTotalPledgeBean.Delegation> delegations;

    public int getLevelDrawableRes(Context context) {
        String resName = "sm_pledge" + level;
        return context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
    }

    public String getLevelDesc(Context context) {
        return context.getString(R.string.sm_pledge_tip);
    }

    public String getLevelName() {
        return "LV." + level;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getShowTokenName() {
        if (TextUtils.isEmpty(tokenName)) {
            return tokenName;
        }
        return tokenName.toUpperCase();
    }
}
