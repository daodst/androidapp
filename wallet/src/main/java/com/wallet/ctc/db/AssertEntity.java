

package com.wallet.ctc.db;

import android.text.TextUtils;

import com.wallet.ctc.util.SettingPrefUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.app.AppApplication;


@Entity
public class AssertEntity{
    @Id(autoincrement = true)
    private Long id;  
    private int logo;
    private String img_path;
    private String short_name;
    private String full_name;
    private String contract;
    private String gas;
    private String decimal;
    private String assertsNum="0";
    private String assertsSumPrice="0";
    private String walletAddress="0";
    private String userName;
    private int type=0;
    private int level;

    
    private String creator;
    private String desc;
    private String total;
    private String url;
    private String award;
    private String mineral;

    public AssertEntity(){

    }

    
    public AssertEntity(int logo, String name, String fullName, String address, String gascount, int decimal){
        this.logo=logo;
        this.short_name=name;
        this.full_name=fullName;
        this.contract=address;
        this.gas=gascount;
        this.decimal=decimal+"";
    }
    
    public AssertEntity(String logo, String name, String fullName, String address, String gascount, String decimal){
        if(!logo.startsWith("http")){
            String baseurl= SettingPrefUtil.getHostUrl(AppApplication.getContext());
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
        this.img_path=logo;
        this.short_name=name;
        this.full_name=fullName;
        this.contract=address;
        this.gas=gascount;
        this.decimal=decimal+"";
    }


    @Generated(hash = 1062024924)
    public AssertEntity(Long id, int logo, String img_path, String short_name, String full_name, String contract,
            String gas, String decimal, String assertsNum, String assertsSumPrice, String walletAddress, String userName,
            int type, int level, String creator, String desc, String total, String url, String award, String mineral) {
        this.id = id;
        this.logo = logo;
        this.img_path = img_path;
        this.short_name = short_name;
        this.full_name = full_name;
        this.contract = contract;
        this.gas = gas;
        this.decimal = decimal;
        this.assertsNum = assertsNum;
        this.assertsSumPrice = assertsSumPrice;
        this.walletAddress = walletAddress;
        this.userName = userName;
        this.type = type;
        this.level = level;
        this.creator = creator;
        this.desc = desc;
        this.total = total;
        this.url = url;
        this.award = award;
        this.mineral = mineral;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getImg_path() {
        if (null==img_path){
            return "";
        }
        return img_path;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
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

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getAssertsNum() {
        return assertsNum;
    }

    public void setAssertsNum(String assertsNum) {
        this.assertsNum = assertsNum;
    }

    public String getAssertsSumPrice() {
        return assertsSumPrice;
    }

    public void setAssertsSumPrice(String assertsSumPrice) {
        this.assertsSumPrice = assertsSumPrice;
    }

    public String getDecimal() {
        decimal=replaceBlank(decimal);
        if(decimal.endsWith(".0")){
            decimal=decimal.substring(0,decimal.length()-2);
        }
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public  String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            String baseurl= SettingPrefUtil.getHostUrl(AppApplication.getContext());
            if(!baseurl.startsWith("http")){
                baseurl="http://"+baseurl;
            }
            if(baseurl.indexOf(":",7)>=10){
            }else {
                baseurl=baseurl+":8888";
            }
            if(!url.startsWith("/")&&!baseurl.endsWith("/")){
                url="/"+url;
            }
            url = baseurl + url;
        }
        this.url = url;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getMineral() {
        return mineral;
    }

    public void setMineral(String mineral) {
        this.mineral = mineral;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "AssertEntity{" +
                "id=" + id +
                ", logo=" + logo +
                ", img_path='" + img_path + '\'' +
                ", short_name='" + short_name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", contract='" + contract + '\'' +
                ", gas='" + gas + '\'' +
                ", decimal='" + decimal + '\'' +
                ", assertsNum='" + assertsNum + '\'' +
                ", assertsSumPrice='" + assertsSumPrice + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", userName='" + userName + '\'' +
                ", type=" + type +
                ", level=" + level +
                ", creator='" + creator + '\'' +
                ", desc='" + desc + '\'' +
                ", total='" + total + '\'' +
                ", url='" + url + '\'' +
                ", award='" + award + '\'' +
                ", mineral='" + mineral + '\'' +
                '}';
    }
}
