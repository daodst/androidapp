

package com.wallet.ctc.model.blockchain;

import java.util.List;

public class MarketPriceBean {

    

    private String currency;
    

    private List<TickersBean> tickers;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<TickersBean> getTickers() {
        return tickers;
    }

    public void setTickers(List<TickersBean> tickers) {
        this.tickers = tickers;
    }

    public static class TickersBean {
        private String change_24h;
        private String provider;
        private String price;
        private String id;
        private String coin;

        public String getChange_24h() {
            return change_24h;
        }

        public void setChange_24h(String change_24h) {
            this.change_24h = change_24h;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }
    }
}
