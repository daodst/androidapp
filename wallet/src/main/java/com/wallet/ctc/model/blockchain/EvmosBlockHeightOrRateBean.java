package com.wallet.ctc.model.blockchain;


public class EvmosBlockHeightOrRateBean extends EvmosHttpBean {
    public Data data;

   public static class Data {
        public String height;
        public double rate;
    }
}
