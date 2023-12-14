

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.wallet.ctc.BuildConfig;



public class EthAssertBean {

    @SerializedName("symbol")
    private String Symbol="";
    private String address="";
    @SerializedName("logo")
    private String Logo="";
    private String Name="";
    @SerializedName("decimals")
    private String Decimals="";

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public String getAddress() {
        return address;
    }

    public String getShortAddress() {
        if(null!=address&&address.length()>16){
            String add=address.substring(0,8);
            add=add+"..."+address.substring(address.length()-8,address.length());
            return add;
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        if(TextUtils.isEmpty(Logo)){
            Logo="";
            return Logo;
        }
        if(!Logo.startsWith("http") && !Logo.startsWith("res://")){
            Logo= BuildConfig.ETH_HOST+Logo;
        }
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDecimals() {
        return Decimals;
    }

    public void setDecimals(String decimals) {
        Decimals = decimals;
    }
}
