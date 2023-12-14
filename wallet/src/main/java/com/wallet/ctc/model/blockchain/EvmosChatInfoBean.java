

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class EvmosChatInfoBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public String from_address;
        public String node_address;
        public List<String> mobile;
        public EvmosAmountsBean chat_fee;

        public String address_book;
        public String chat_blacklist;
        public String chat_whitelist;

        public int pledge_level;
    }
}
