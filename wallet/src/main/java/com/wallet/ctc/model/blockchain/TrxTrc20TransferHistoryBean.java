

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class TrxTrc20TransferHistoryBean{


    

    private String transaction_id;
    private TokenInfoBean token_info;
    private long block_timestamp;
    private String from;
    private String to;
    private String type;
    private BigDecimal value;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public TokenInfoBean getToken_info() {
        return token_info;
    }

    public void setToken_info(TokenInfoBean token_info) {
        this.token_info = token_info;
    }

    public long getBlock_timestamp() {
        return block_timestamp;
    }

    public void setBlock_timestamp(long block_timestamp) {
        this.block_timestamp = block_timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public static class TokenInfoBean {
        

        private String symbol;
        private String address;
        private int decimals;
        private String name;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getDecimals() {
            return decimals;
        }

        public void setDecimals(int decimals) {
            this.decimals = decimals;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
