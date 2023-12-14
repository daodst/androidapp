

package com.wallet.ctc.db;

import static com.wallet.ctc.crypto.WalletDBUtil.USER_ID;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;

import android.text.TextUtils;

import com.wallet.ctc.R;
import com.wallet.ctc.util.SettingPrefUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.math.BigDecimal;
import java.math.RoundingMode;

import common.app.AppApplication;


@Entity
public class CreatEthEntity {
    @Id(autoincrement = true)
    private Long id;  
    private String img_path;
    private String short_name;
    private String full_name;
    private String decimal;
    private String walletAddress="";
    private String address="";
    private String userName;
    private String assestNum;
    private String hexValue;
    private int type=0;
    private int statu=0;
    private int creatTime;
    public CreatEthEntity(){

    }
    public CreatEthEntity(String short_name,String full_name,String assestNum,String decimal,String walletAddress,int type,int statu,int creatTime){
        img_path="";
        this.short_name=short_name.toLowerCase();
        this.full_name=full_name.toLowerCase();
        this.decimal=decimal;
        this.walletAddress=walletAddress;
        userName= USER_ID;
        this.type=type;
        this.address="";
        this.statu=statu;
        this.assestNum=assestNum;
        this.creatTime=creatTime;
    }
    @Generated(hash = 1580880633)
    public CreatEthEntity(Long id, String img_path, String short_name, String full_name, String decimal, String walletAddress, String address,
            String userName, String assestNum, String hexValue, int type, int statu, int creatTime) {
        this.id = id;
        this.img_path = img_path;
        this.short_name = short_name;
        this.full_name = full_name;
        this.decimal = decimal;
        this.walletAddress = walletAddress;
        this.address = address;
        this.userName = userName;
        this.assestNum = assestNum;
        this.hexValue = hexValue;
        this.type = type;
        this.statu = statu;
        this.creatTime = creatTime;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String logo) {
        if(!logo.startsWith("http")){
            String baseurl;
            if(type==DM_COIN){
                baseurl = SettingPrefUtil.getDMHostApi(AppApplication.getContext());
            }else if(type==MCC_COIN){
                baseurl = SettingPrefUtil.getMCCHostApi(AppApplication.getContext());
            }else if(type==OTHER_COIN){
                baseurl = SettingPrefUtil.getOtherHostApi(AppApplication.getContext());
            }else {
                baseurl = SettingPrefUtil.getMCCHostApi(AppApplication.getContext());
            }
            int hosttype= SettingPrefUtil.getNodeType(AppApplication.getContext());
            if(!baseurl.startsWith("http")){
                baseurl="http://"+baseurl;
            }
            if(baseurl.indexOf(":",7)>=10||hosttype==1){
            }else {
                baseurl=baseurl+":8888";
            }
            if(!logo.startsWith("/")&&!baseurl.endsWith("/")){
                logo="/"+logo;
            }
            logo = baseurl + logo;
        }
        this.img_path = logo;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hexValue) {
        this.hexValue = hexValue;
    }

    public String getAssestNum() {
        if(null!=assestNum&&! TextUtils.isEmpty(assestNum)){
            try {
                BigDecimal bignum=new BigDecimal(assestNum);
                if(bignum.doubleValue()>=100000000){
                    bignum=bignum.divide(new BigDecimal("100000000"),2, RoundingMode.HALF_DOWN);
                    return bignum.toPlainString()+AppApplication.getContext().getString(R.string.yi);
                }else if(bignum.doubleValue()>=10000) {
                    bignum=bignum.divide(new BigDecimal("10000"), 2,RoundingMode.HALF_DOWN);
                    return bignum.toPlainString()+ AppApplication.getContext().getString(R.string.wan);
                }
            }catch (Exception e){

            }

        }
        return assestNum;
    }

    public void setAssestNum(String assestNum) {
        this.assestNum = assestNum;
    }

    public int getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(int creatTime) {
        this.creatTime = creatTime;
    }
}
