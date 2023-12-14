

package com.wallet.ctc.model.blockchain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wallet.ctc.db.WalletEntity;

import java.math.BigDecimal;



public class TransferBean implements Parcelable {

    private String ruaddress;
    private String payaddress;
    private String price;
    private String kuanggong;
    private String fee;
    private String remark;
    private String tokenType;
    private int gascount;
    private String gasprice;
    private String maxFeePerGas;
    private String maxPriorityFeePerGas;
    private String tokenName;
    private int decimal;  
    private String tokenAddress;
    private String data;
    private int chainId;
    private String nonce;
    private int type;
    private String GasFeeCap;
    private String GasPremium;
    
    private String info;
    private long call_value;
    private long fee_limit;
    private long timestamp;
    private long expiration;
    private long frozen_duration;
    private long frozen_balance;
    private String resource;
    private String receiver_address;
    public String tokenId;
    public WalletEntity mWalletEntity;


    
    public TransferBean(String toAdress, String fromAddress, String account, String fee, int type, String tokenName, String kuanggong) {
        this.ruaddress = toAdress;
        this.payaddress = fromAddress;
        this.price = account;
        this.kuanggong = kuanggong;
        this.fee = fee;
        this.type = type;
        this.tokenName = tokenName;
    }

    public TransferBean(WalletEntity entity, String toAdress, String fromAddress, String account, String fee, int type, String tokenName, String kuanggong) {
        mWalletEntity = entity;
        this.ruaddress = toAdress;
        this.payaddress = fromAddress;
        this.price = account;
        this.kuanggong = kuanggong;
        this.fee = fee;
        this.type = type;
        this.tokenName = tokenName;
    }

    
    public TransferBean(String toAdress, String fromAddress, String account, int type, String tokenName, String tokenAddress, int decimal) {
        this.ruaddress = toAdress;
        this.payaddress = fromAddress;
        this.price = account;
        this.tokenAddress = tokenAddress;
        this.tokenName = tokenName;
        this.type = type;
        this.decimal = decimal;
    }

    public String getInfo() {
        if (TextUtils.isEmpty(info)) {
            info = "";
        }
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getGasFeeCap() {
        return GasFeeCap;
    }

    public void setGasFeeCap(String gasFeeCap) {
        GasFeeCap = gasFeeCap;
    }

    public String getGasPremium() {
        return GasPremium;
    }

    public void setGasPremium(String gasPremium) {
        GasPremium = gasPremium;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public int getGascount() {
        return gascount;
    }

    public void setGascount(int gascount) {
        this.gascount = gascount;
    }

    public String getGasprice() {
        if (TextUtils.isEmpty(gasprice)) {
            gasprice = "0";
        }
        return gasprice;
    }

    public void setGasprice(String gasprice) {
        this.gasprice = gasprice;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


    public String getAllAddress() {
        return ruaddress;
    }

    public String getRuaddress() {
        String address = ruaddress.substring(0, 10);
        int len = ruaddress.length();
        address = address + "..." + ruaddress.substring(len - 10, len);
        return address;
    }

    public void setRuaddress(String ruaddress) {
        this.ruaddress = ruaddress;
    }

    public String getPayaddress() {
        return payaddress;
    }

    public void setPayaddress(String payaddress) {
        this.payaddress = payaddress;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getKuanggong() {
        return kuanggong;
    }

    public void setKuanggong(String kuanggong) {
        this.kuanggong = kuanggong;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public String getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getMaxFeePerGas() {
        return maxFeePerGas;
    }

    public void setMaxFeePerGas(String maxFeePerGas) {
        this.maxFeePerGas = maxFeePerGas;
    }

    public String getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    public void setMaxPriorityFeePerGas(String maxPriorityFeePerGas) {
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
    }

    public long getCall_value() {
        return call_value;
    }

    public void setCall_value(long call_value) {
        this.call_value = call_value;
    }

    public long getFee_limit() {
        return fee_limit;
    }

    public void setFee_limit(long fee_limit) {
        this.fee_limit = fee_limit;
    }

    public long getFrozen_duration() {
        return frozen_duration;
    }

    public void setFrozen_duration(long frozen_duration) {
        this.frozen_duration = frozen_duration;
    }

    public long getFrozen_balance() {
        return frozen_balance;
    }

    public void setFrozen_balance(long frozen_balance) {
        this.frozen_balance = frozen_balance;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getReceiver_address() {
        return receiver_address;
    }

    public void setReceiver_address(String receiver_address) {
        this.receiver_address = receiver_address;
    }

    public long getTag() {
        if (TextUtils.isEmpty(remark)) {
            return 0;
        } else {
            try {
                BigDecimal tag = new BigDecimal(remark);
                return tag.intValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    @Override
    public String toString() {
        return "TransferBean{" +
                "ruaddress='" + ruaddress + '\'' +
                ", payaddress='" + payaddress + '\'' +
                ", price='" + price + '\'' +
                ", kuanggong='" + kuanggong + '\'' +
                ", fee='" + fee + '\'' +
                ", remark='" + remark + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", gascount=" + gascount +
                ", gasprice='" + gasprice + '\'' +
                ", GasFeeCap='" + GasFeeCap + '\'' +
                ", tokenName='" + tokenName + '\'' +
                ", decimal=" + decimal +
                ", tokenAddress='" + tokenAddress + '\'' +
                ", data='" + data + '\'' +
                ", maxFeePerGas='" + maxFeePerGas + '\'' +
                ", maxPriorityFeePerGas='" + maxPriorityFeePerGas + '\'' +
                ", nonce='" + nonce + '\'' +
                ", type=" + type +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ruaddress);
        dest.writeString(this.payaddress);
        dest.writeString(this.price);
        dest.writeString(this.kuanggong);
        dest.writeString(this.fee);
        dest.writeString(this.remark);
        dest.writeString(this.tokenType);
        dest.writeInt(this.gascount);
        dest.writeString(this.gasprice);
        dest.writeString(this.maxFeePerGas);
        dest.writeString(this.maxPriorityFeePerGas);
        dest.writeString(this.tokenName);
        dest.writeInt(this.decimal);
        dest.writeString(this.tokenAddress);
        dest.writeString(this.data);
        dest.writeInt(this.chainId);
        dest.writeString(this.nonce);
        dest.writeInt(this.type);
        dest.writeString(this.GasFeeCap);
        dest.writeString(this.GasPremium);
        dest.writeLong(this.call_value);
        dest.writeLong(this.fee_limit);
    }

    public void readFromParcel(Parcel source) {
        this.ruaddress = source.readString();
        this.payaddress = source.readString();
        this.price = source.readString();
        this.kuanggong = source.readString();
        this.fee = source.readString();
        this.remark = source.readString();
        this.tokenType = source.readString();
        this.gascount = source.readInt();
        this.gasprice = source.readString();
        this.maxFeePerGas = source.readString();
        this.maxPriorityFeePerGas = source.readString();
        this.tokenName = source.readString();
        this.decimal = source.readInt();
        this.tokenAddress = source.readString();
        this.data = source.readString();
        this.chainId = source.readInt();
        this.nonce = source.readString();
        this.type = source.readInt();
        this.GasFeeCap = source.readString();
        this.GasPremium = source.readString();
        this.call_value = source.readLong();
        this.fee_limit = source.readLong();
    }

    protected TransferBean(Parcel in) {
        this.ruaddress = in.readString();
        this.payaddress = in.readString();
        this.price = in.readString();
        this.kuanggong = in.readString();
        this.fee = in.readString();
        this.remark = in.readString();
        this.tokenType = in.readString();
        this.gascount = in.readInt();
        this.gasprice = in.readString();
        this.maxFeePerGas = in.readString();
        this.maxPriorityFeePerGas = in.readString();
        this.tokenName = in.readString();
        this.decimal = in.readInt();
        this.tokenAddress = in.readString();
        this.data = in.readString();
        this.chainId = in.readInt();
        this.nonce = in.readString();
        this.type = in.readInt();
        this.GasFeeCap = in.readString();
        this.GasPremium = in.readString();
        this.call_value = in.readLong();
        this.fee_limit = in.readLong();
    }

    public static final Creator<TransferBean> CREATOR = new Creator<TransferBean>() {
        @Override
        public TransferBean createFromParcel(Parcel source) {
            return new TransferBean(source);
        }

        @Override
        public TransferBean[] newArray(int size) {
            return new TransferBean[size];
        }
    };
}
