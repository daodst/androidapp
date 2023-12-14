

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class TrxSeachDeatilBean {


    private String type;
    private Integer count;
    private StatusBean status;
    private List<DataBean> data;

    public static class StatusBean {
        private Integer code;
        private String message;
    }

    public static class DataBean {
        private TokenInfoBean tokenInfo;
        public static class TokenInfoBean {
            private String tokenId;
            private String tokenAbbr;
            private String tokenName;
            private Integer tokenDecimal;
            private Integer tokenCanShow;
            private String tokenType;
            private String tokenLogo;
            private String tokenLevel;
            private Boolean vip;

            public String getTokenId() {
                return tokenId;
            }

            public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
            }

            public String getTokenAbbr() {
                return tokenAbbr;
            }

            public void setTokenAbbr(String tokenAbbr) {
                this.tokenAbbr = tokenAbbr;
            }

            public String getTokenName() {
                return tokenName;
            }

            public void setTokenName(String tokenName) {
                this.tokenName = tokenName;
            }

            public Integer getTokenDecimal() {
                return tokenDecimal;
            }

            public void setTokenDecimal(Integer tokenDecimal) {
                this.tokenDecimal = tokenDecimal;
            }

            public Integer getTokenCanShow() {
                return tokenCanShow;
            }

            public void setTokenCanShow(Integer tokenCanShow) {
                this.tokenCanShow = tokenCanShow;
            }

            public String getTokenType() {
                return tokenType;
            }

            public void setTokenType(String tokenType) {
                this.tokenType = tokenType;
            }

            public String getTokenLogo() {
                return tokenLogo;
            }

            public void setTokenLogo(String tokenLogo) {
                this.tokenLogo = tokenLogo;
            }

            public String getTokenLevel() {
                return tokenLevel;
            }

            public void setTokenLevel(String tokenLevel) {
                this.tokenLevel = tokenLevel;
            }

            public Boolean getVip() {
                return vip;
            }

            public void setVip(Boolean vip) {
                this.vip = vip;
            }
        }

        public TokenInfoBean getTokenInfo() {
            return tokenInfo;
        }

        public void setTokenInfo(TokenInfoBean tokenInfo) {
            this.tokenInfo = tokenInfo;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }
}
