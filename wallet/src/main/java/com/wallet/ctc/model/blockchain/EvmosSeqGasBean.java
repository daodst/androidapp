

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;


public class EvmosSeqGasBean extends EvmosHttpBean {

    public boolean reg;

    public EvmosGasBean gas;
    public EvmosSeqAcountBean.Data seqAccount;
    public EvmosChatFeeBean feeSetting;

    public String mHashPledgeNum;
    public String mUnPledgeHashNum;


    
    public String getShowFee() {
        if (!TextUtils.isEmpty(gasFee)) {
            
            return gasFee;
        }
        if (null != gas) {
            return gas.getShowFee();
        } else {
            return "";
        }
    }

    
    public String gasFee;
    public String gasPrice;
    public int gasCount;


    
    public String rewardAmount; 

}
