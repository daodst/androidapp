

package com.wallet.ctc.model.blockchain;

import android.net.Uri;
import android.text.TextUtils;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.util.SettingPrefUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.app.AppApplication;



public class AssertBean implements Serializable {
    private Long id;  
    private int logo;
    private String img_path;
    private String short_name;
    private String full_name;
    private String contract;
    private String gas;
    private String decimal;
    private String assertsNum="0";
    private String userName;
    private String assertsSumPrice="0.00";
    private int level;
    private int type;
    private String walletAddress="0";
    
    private String creator;
    private String desc;
    private String total;
    private String url;
    private String award;
    private String mineral;
    
    private BigDecimal tokenPriceInTrx;

    private String price = "0";

    public AssertBean(){

    }

    
    public AssertBean(int logo,String name,String fullName,String address,String gascount,int decimal,int type,int level){
        this.logo=logo;
        this.short_name=name;
        this.full_name=fullName;
        this.contract=address;
        this.gas=gascount;
        this.decimal=decimal+"";
        this.type=type;
        this.level=level;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getShortAddress(){
        if(null!=contract&&contract.length()>16){
            String add=contract.substring(0,8);
            add=add+"..."+contract.substring(contract.length()-8,contract.length());
            return add;
        }
        return contract;
    }


    
    public AssertBean(String logo,String name,String fullName,String address,String gascount,String decimal,int type,int level){
        if(!TextUtils.isEmpty(logo) && !logo.startsWith("http") && !logo.startsWith("res://")){
            logo= BuildConfig.ETH_HOST+logo;
        }
        this.img_path=logo;
        this.short_name=name;
        this.full_name=fullName;
        this.contract=address;
        this.gas=gascount;
        this.decimal=decimal+"";
        this.type=type;
        this.level=level;
    }

    public int getLogo() {
        if (logo > 0){
            return logo;
        }
        int resLogo = getResLogoId();
        if (resLogo > 0){
            return resLogo;
        }
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }
    public String getImg_path() {
        if (null==img_path||TextUtils.isEmpty(img_path)){
            return "";
        }
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }


    
    private int getResLogoId() {
        String resPath = img_path;
        if (!TextUtils.isEmpty(resPath) && resPath.startsWith("res://")) {
            Uri uri = Uri.parse(resPath);
            String host = uri.getHost(); 
            String path = uri.getPath(); 
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            int logo = AppApplication.getInstance().getApplicationContext().getResources().getIdentifier(path, host, AppApplication.getInstance().getApplicationContext().getPackageName());
            return logo;
        } else {
            return 0;
        }
    }
    public String getShort_name() {
        if (null == short_name) {
            return "";
        }
        return short_name;
    }

    public String getShortNameUpCase(){
        String shortName = getShort_name();
        if (!TextUtils.isEmpty(shortName)){
            return shortName.toUpperCase();
        } else {
            return shortName;
        }
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
        if(null==contract){
            contract="";
        }
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getAssertsNum() {
        if(!TextUtils.isEmpty(assertsNum)&&new BigDecimal(assertsNum).doubleValue()==0){
            assertsNum="0.00";
        }
        return assertsNum;
    }

    public void setAssertsNum(String assertsNum) {
        this.assertsNum = assertsNum;
    }

    public String getAssertsSumPrice() {
        if(null==assertsSumPrice||TextUtils.isEmpty(assertsSumPrice)){
            assertsSumPrice="0.00";
        }
        return assertsSumPrice;
    }

    public void setAssertsSumPrice(String assertsSumPrice) {
        this.assertsSumPrice = assertsSumPrice;
    }

    public int getDecimal() {
        decimal=replaceBlank(decimal);
        BigDecimal de=new BigDecimal(decimal);
        return de.intValue();
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
            dest = dest.replaceAll("\"","");
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTokenPriceInTrx() {
        if(null==tokenPriceInTrx){
            tokenPriceInTrx=new BigDecimal("0");
        }
        return tokenPriceInTrx;
    }

    public void setTokenPriceInTrx(BigDecimal tokenPriceInTrx) {
        this.tokenPriceInTrx = tokenPriceInTrx;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AssertBean{" +
                "id=" + id +
                ", logo=" + logo +
                ", img_path='" + img_path + '\'' +
                ", short_name='" + short_name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", contract='" + contract + '\'' +
                ", gas='" + gas + '\'' +
                ", decimal='" + decimal + '\'' +
                ", assertsNum='" + assertsNum + '\'' +
                ", userName='" + userName + '\'' +
                ", assertsSumPrice='" + assertsSumPrice + '\'' +
                ", level=" + level +
                ", type=" + type +
                ", walletAddress='" + walletAddress + '\'' +
                ", creator='" + creator + '\'' +
                ", desc='" + desc + '\'' +
                ", total='" + total + '\'' +
                ", url='" + url + '\'' +
                ", award='" + award + '\'' +
                ", mineral='" + mineral + '\'' +
                ", tokenPriceInTrx=" + tokenPriceInTrx +
                ", price=" + price +
                '}';
    }
}
