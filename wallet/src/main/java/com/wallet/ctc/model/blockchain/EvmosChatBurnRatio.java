

package com.wallet.ctc.model.blockchain;


public class EvmosChatBurnRatio extends EvmosHttpBean {
    public BurnRatioData data;


    public static class BurnRatioData {
        
        public String burn_get_hash;
        
        public String pledge_hash_get;
        
        public String burn_address;
    }


}
