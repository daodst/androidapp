package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedeemGatewayInfoEntity extends EvmosHttpBean {
    public Data data;

    public static class Data {

        

        public boolean is_gateway;
        public GatewayEntity gateway;

        public static class GatewayEntity {
            

            public String gateway_address;
            public String gateway_name;
            public String gateway_url;
            public int gateway_quota;
            public int status;
            @SerializedName("package")
            public String packageX;
            public List<GatewayNumEntity> gateway_num;

            public static class GatewayNumEntity {
                

                public String gateway_address;
                public String number_index;
                public int status;
                public int validity;
                public boolean is_first;
                public List<String> number_end;
            }
        }
    }
}
