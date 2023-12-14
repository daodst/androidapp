package com.app.home.pojo.rpc;

import android.text.TextUtils;

import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class RPCVoteInfo<T> {

    
    public Amount fee;

    public String getShowFee(String units) {
        if (null == fee) {
            return "";
        }
        String unit = fee.denom.toUpperCase();
        if (TextUtils.isEmpty(unit)) {
            unit = units;
        }
        try {
            return new BigDecimal(fee.amount).divide(BigDecimal.valueOf(Math.pow(10, 18)), 18, RoundingMode.UP).stripTrailingZeros().toPlainString() +" "+ unit;
        } catch (Exception e) {
            return fee.amount + " " + unit;
        }

    }

    
    public String gas_used;
    
    public Amount gas_price;

    public T param;
    public WalletEntity mWalletEntity;

    public String consume;

    public EvmosSeqAcountBean mSeqAcountBean;


    public static class Amount {
        public String denom;
        public String amount;
    }
}
