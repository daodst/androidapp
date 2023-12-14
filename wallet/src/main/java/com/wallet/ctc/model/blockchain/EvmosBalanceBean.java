

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class EvmosBalanceBean extends EvmosHttpBean{
    public List<Data> data;

    public static class Data {
        public String denom;
        public String amount;
    }
}
