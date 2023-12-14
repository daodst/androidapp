

package com.wallet.ctc.model.blockchain;


public class EvmosPledgeParamsBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public EvmosAmountsBean minBurnCoin;
        public EvmosAmountsBean preAttCoin;
        public String preAttAccount;
        public String attDestroyPercent;
        public String attGatewayPercent;
        public String attDposPercent;


    }
}
