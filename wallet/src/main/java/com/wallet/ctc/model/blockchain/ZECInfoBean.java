

package com.wallet.ctc.model.blockchain;


public class ZECInfoBean {
    public BlockBook blockbook;
    public Backend backend;

    public String getBranchid() {
        if (null != backend && null != backend.consensus) {
            return backend.consensus.chaintip;
        } else {
            return "";
        }
    }


    public static class BlockBook {
        public String coin;

    }

    public static class Backend {
        public String chain;
        Consensus consensus;
    }

    public static class Consensus {
        public String chaintip;
        public String nextblock;
    }
}
