

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class EvmosGatewayBean extends EvmosHttpBean{
    public Data data;

    public static class Data {
        public String gateway_address;
        public String gateway_name;
        public String gateway_url;
        public long gateway_quota;
        public int status;
        public List<GatewayNum> gateway_num;
        public String token;
        public long online;


        public boolean isUsingNode; 
        public long pingTime;
        public String getGateWayUrl() {
            return gateway_url;
        }


    }

    public static class GatewayNum {
        public String gateway_address;
        public String number_index;
        public int status;
        public String validity;
    }
}
