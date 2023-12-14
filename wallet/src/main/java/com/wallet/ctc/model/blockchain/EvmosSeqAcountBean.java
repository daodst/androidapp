

package com.wallet.ctc.model.blockchain;


public class EvmosSeqAcountBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public long account_number;
        public long sequence;
        public boolean not_found;
    }
}
