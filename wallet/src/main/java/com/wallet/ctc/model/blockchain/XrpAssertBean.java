

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;



public class XrpAssertBean {
    private String issuer;
    private String currency;
    private String amount;
    private String logo;
    private String decimals;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getShortAddress() {
        if(null!=issuer&&issuer.length()>16){
            String add=issuer.substring(0,8);
            add=add+"..."+issuer.substring(issuer.length()-8,issuer.length());
            return add;
        } else if(null != issuer) {
            return issuer.toLowerCase();
        } else {
            return "";
        }

    }

    public String getDecimals() {
        if (TextUtils.isEmpty(decimals)) {
            return "6";
        }
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }
}
