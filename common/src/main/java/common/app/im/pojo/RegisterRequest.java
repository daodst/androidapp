

package common.app.im.pojo;

import android.os.Parcel;
import android.os.Parcelable;



public class RegisterRequest implements Parcelable {
    private String intro;

    private String nickname;

    private String mobile;

    private String verify;

    private String passwd_one;

    private String username;

    private String address;

    private String province;
    private String province_code;
    private String city;
    private String city_code;
    private String county;
    private String county_code;
    private String town;
    private String town_code;
    private String pay_password;
    private int showPayPwd;

    public int getAgreement() {
        return agreement;
    }

    public void setAgreement(int agreement) {
        this.agreement = agreement;
    }

    private int agreement;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCounty_code() {
        return county_code;
    }

    public void setCounty_code(String county_code) {
        this.county_code = county_code;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getPasswd_one() {
        return passwd_one;
    }

    public void setPasswd_one(String passwd_one) {
        this.passwd_one = passwd_one;
    }

    public String getPay_password() {
        return pay_password;
    }

    public void setPay_password(String pay_password) {
        this.pay_password = pay_password;
    }

    public int getShowPayPwd() {
        return showPayPwd;
    }

    public void setShowPayPwd(int showPayPwd) {
        this.showPayPwd = showPayPwd;
    }

    public RegisterRequest() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.intro);
        dest.writeString(this.nickname);
        dest.writeString(this.mobile);
        dest.writeString(this.verify);
        dest.writeString(this.passwd_one);
        dest.writeString(this.username);
        dest.writeString(this.address);
        dest.writeString(this.province);
        dest.writeString(this.province_code);
        dest.writeString(this.city);
        dest.writeString(this.city_code);
        dest.writeString(this.county);
        dest.writeString(this.county_code);
        dest.writeString(this.town);
        dest.writeString(this.town_code);
        dest.writeString(this.pay_password);
        dest.writeInt(this.showPayPwd);
        dest.writeInt(this.agreement);
    }

    protected RegisterRequest(Parcel in) {
        this.intro = in.readString();
        this.nickname = in.readString();
        this.mobile = in.readString();
        this.verify = in.readString();
        this.passwd_one = in.readString();
        this.username = in.readString();
        this.address = in.readString();
        this.province = in.readString();
        this.province_code = in.readString();
        this.city = in.readString();
        this.city_code = in.readString();
        this.county = in.readString();
        this.county_code = in.readString();
        this.town = in.readString();
        this.town_code = in.readString();
        this.pay_password = in.readString();
        this.showPayPwd = in.readInt();
        this.agreement=in.readInt();
    }

    public static final Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };
}
