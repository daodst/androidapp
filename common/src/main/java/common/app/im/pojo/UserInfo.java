

package common.app.im.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;



public class UserInfo {
    private String id; 
    private String username;
    private String mobile;
    private String email;
    private String approve_mobile;
    private int approve_user;
    private int approve_supply;

    @SerializedName(value = "nickname",alternate = {"show_name"})
    private String nickname;

    private int rank;

    private String name;

    private String avatar; 

    @SerializedName(value = "logo",alternate = {"head_logo"})
    private String logo;
    private String password;
    private String pay_password;
    private String birthday;
    private int sex;
    private String province;
    private String city;
    private String county;
    private String town;
    private String address;
    private long last_login_time;
    private int login_num;
    private String img_card;
    private String truename;
    private String card_no;
    private String comment;
    private String isSign;
    private String qrcode;
    private String quick_pay;
    private String fav_product_num = "0";
    private String fav_supply_num = "0";
    private String history_num = "0";
    private String is_shop;

    public String getIs_shop() {
        return is_shop;
    }

    public void setIs_shop(String is_shop) {
        this.is_shop = is_shop;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    private String rank_name;
    public boolean isOfflineShop() {
        if (!TextUtils.isEmpty(is_shop) && is_shop.equals("offline")) {
            return true;
        } else {
            return false;
        }
    }

    public String getRank_name() {
        return rank_name;
    }

    public String getQuick_pay() {
        return quick_pay;
    }

    public void setQuick_pay(String quick_pay) {
        this.quick_pay = quick_pay;
    }

    public void setRank_name(String rank_name) {
        this.rank_name = rank_name;
    }
    public String getFav_product_num() {
        return fav_product_num;
    }

    public void setFav_product_num(String fav_product_num) {
        this.fav_product_num = fav_product_num;
    }

    public String getFav_supply_num() {
        return fav_supply_num;
    }

    public void setFav_supply_num(String fav_supply_num) {
        this.fav_supply_num = fav_supply_num;
    }

    public String getHistory_num() {
        return history_num;
    }

    public void setHistory_num(String history_num) {
        this.history_num = history_num;
    }

    private String logo_fandom_bg;

    public String getLogo_fandom_bg() {
        return logo_fandom_bg;
    }

    public void setLogo_fandom_bg(String logo_fandom_bg) {
        this.logo_fandom_bg = logo_fandom_bg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApprove_mobile() {
        return approve_mobile;
    }

    public void setApprove_mobile(String approve_mobile) {
        this.approve_mobile = approve_mobile;
    }

    public int getApprove_user() {
        return approve_user;
    }

    public void setApprove_user(int approve_user) {
        this.approve_user = approve_user;
    }

    public int getApprove_supply() {
        return approve_supply;
    }

    public void setApprove_supply(int approve_supply) {
        this.approve_supply = approve_supply;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPay_password() {
        return pay_password;
    }

    public void setPay_password(String pay_password) {
        this.pay_password = pay_password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(long last_login_time) {
        this.last_login_time = last_login_time;
    }

    public int getLogin_num() {
        return login_num;
    }

    public void setLogin_num(int login_num) {
        this.login_num = login_num;
    }

    public String getImg_card() {
        return img_card;
    }

    public void setImg_card(String img_card) {
        this.img_card = img_card;
    }

    public String getTruename() {
        return truename;
    }

    public String getSuoTruename() {
        if(truename==null){
            truename="";
        }
        if(truename.length()>1){
            truename= truename.substring(1,truename.length());
            truename="(*"+truename+")";
        }else{
            truename="(*)";
        }
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIsSign() {
        return isSign;
    }

    public void setIsSign(String isSign) {
        this.isSign = isSign;
    }
}
