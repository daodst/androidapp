

package com.wallet.ctc.model.me;
import java.io.Serializable;


public class UserEntity implements Serializable{

    public static final int FEELING = 1;
    public static final int SEX = 2;
    public static final int AUTHORITY = 3;

    public static final String THIRD_LOGIN = "0";
    public static final String EMAIL_LOGIN = "1";

    private String signature;
    private String feeling;  
    private String vague_status;
    private String diary_pwd_status;
    private String password;
    private String userId;
    private String id;
    private String login_type;
    private String open_id;
    private String supplier;
    private String username;
    private String email;
    private String phone;
    private String usernick;
    private String online_status;
    private String avatar;
    private String avatar_thumb;
    private String community_cover;
    private String qr_code;
    private String address;
    private String country;
    private String langauge;
    private String longitude;
    private String latitude;
    private String push_channel;
    private String token;
    private String chat_username;
    private String chat_password;
    private String birthday;
    private String sex;
    private String status;
    private String reg_time;
    private String reg_ip;
    private String last_login_ip;
    private String last_login_time;
    private String favour_count;
    private String time_zone;
    private String key;
    private String countryCode;
    private String background_image;
    

    private String private_diary_password;
    private String province;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getVague_status() {
        return vague_status;
    }

    public void setVague_status(String vague_status) {
        this.vague_status = vague_status;
    }

    public String getDiary_pwd_status() {
        return diary_pwd_status;
    }

    public void setDiary_pwd_status(String diary_pwd_status) {
        this.diary_pwd_status = diary_pwd_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getCommunity_cover() {
        return community_cover;
    }

    public void setCommunity_cover(String community_cover) {
        this.community_cover = community_cover;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLangauge() {
        return langauge;
    }

    public void setLangauge(String langauge) {
        this.langauge = langauge;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPush_channel() {
        return push_channel;
    }

    public void setPush_channel(String push_channel) {
        this.push_channel = push_channel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChat_username() {
        return chat_username;
    }

    public void setChat_username(String chat_username) {
        this.chat_username = chat_username;
    }

    public String getChat_password() {
        return chat_password;
    }

    public void setChat_password(String chat_password) {
        this.chat_password = chat_password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getReg_ip() {
        return reg_ip;
    }

    public void setReg_ip(String reg_ip) {
        this.reg_ip = reg_ip;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getFavour_count() {
        return favour_count;
    }

    public void setFavour_count(String favour_count) {
        this.favour_count = favour_count;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPrivate_diary_password() {
        return private_diary_password;
    }

    public void setPrivate_diary_password(String private_diary_password) {
        this.private_diary_password = private_diary_password;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}

