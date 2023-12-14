

package com.wallet.ctc.model.blockchain;

import java.util.ArrayList;
import java.util.List;

import wallet.core.jni.Base64;


public class EvMosTx {
    public String chain_id;
    public String account_number;
    public String sequence;
    public Fee fee;
    public List<Message> msgs;
    public String memo;


    public static class Fee {
        public String gas;
        public List<Amount> amount;
    }

    public static class Amount {
        public String denom;
        public String amount;
    }

    public static class Message {
        public String type;
        public byte[] value;
    }


    public static EvMosTx getTestBean() {
        EvMosTx bean = new EvMosTx();
        bean.chain_id = "sc_8888-1";
        bean.account_number = "9";
        bean.sequence = "9";

        Fee fee = new Fee();
        fee.gas = "2500000";
        Amount amount = new Amount();
        amount.denom = "att";
        amount.amount = "62500";
        fee.amount = new ArrayList<>();
        fee.amount.add(amount);

        bean.fee = fee;

        Message msg = new Message();
        msg.type = "comm/MsgGatewayUndelegation";
        msg.value = Base64.decode("CipkZXgxejl6azdhbjd6ZjhrN3Q1NDA3eXpzamt5MG4wc2NtcjYzdDRsenoSMWRleHZhbG9wZXIxejl6azdhbjd6ZjhrN3Q1NDA3eXpzamt5MG4wc2NtcjZod3hmbGwaDwoDZm1jEggxMDAwMDAwMA==");

        bean.msgs = new ArrayList<>();
        bean.msgs.add(msg);
        bean.memo = "";
        return bean;
    }

}
