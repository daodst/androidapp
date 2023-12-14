package com.wallet.ctc.model.blockchain;

public class DaodstTipsBean  extends EvmosHttpBean{

    public Data data;

    public static class Data {
        
        public String hash_pledge;
        
        public String dst_burn;
        
        public String undelegate_level;


        
        public String cross_chain_min;
        
        public String cross_chain_fee;

    }

}
