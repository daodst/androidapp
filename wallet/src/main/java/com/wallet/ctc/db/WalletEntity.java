

package com.wallet.ctc.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wallet.ctc.AppHolder;
import com.wallet.ctc.crypto.WalletUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.math.BigDecimal;
import java.util.Arrays;


@Entity
public class WalletEntity implements Parcelable {
    @Id(autoincrement = true)
    private Long id;  
    private byte[] mMnemonic;
    private byte[] mKeystore;
    private byte[] mPrivateKey;
    private byte[] mPublicKey;
    private String mAddress;
    private String mAddress2;
    private String chatAddress;
    private String chatEthAddress;
    private String chatPrivateKey;
    private String Name;
    public int Logo;
    private String mPassword;
    private String mPasswordHint;
    private String mCurrency;
    private String mQuotes;
    private String mBalance;
    private String mPrice;
    private String sumPrice;
    private String userName;
    private int Level;
    private String walletId;
    private int type;
    private int mMnemonicBackup;
    private int mBackup;
    private Integer defwallet=0;

    @Transient
    public String energy;
    @Transient
    public String broadband;
    @Transient
    public String energyMax;
    @Transient
    public String broadbandMax;
    @Transient
    public int select_flag;

    @Generated(hash = 138586853)
    public WalletEntity(Long id, byte[] mMnemonic, byte[] mKeystore, byte[] mPrivateKey, byte[] mPublicKey, String mAddress, String mAddress2, String chatAddress,
            String chatEthAddress, String chatPrivateKey, String Name, int Logo, String mPassword, String mPasswordHint, String mCurrency, String mQuotes, String mBalance,
            String mPrice, String sumPrice, String userName, int Level, String walletId, int type, int mMnemonicBackup, int mBackup, Integer defwallet) {
        this.id = id;
        this.mMnemonic = mMnemonic;
        this.mKeystore = mKeystore;
        this.mPrivateKey = mPrivateKey;
        this.mPublicKey = mPublicKey;
        this.mAddress = mAddress;
        this.mAddress2 = mAddress2;
        this.chatAddress = chatAddress;
        this.chatEthAddress = chatEthAddress;
        this.chatPrivateKey = chatPrivateKey;
        this.Name = Name;
        this.Logo = Logo;
        this.mPassword = mPassword;
        this.mPasswordHint = mPasswordHint;
        this.mCurrency = mCurrency;
        this.mQuotes = mQuotes;
        this.mBalance = mBalance;
        this.mPrice = mPrice;
        this.sumPrice = sumPrice;
        this.userName = userName;
        this.Level = Level;
        this.walletId = walletId;
        this.type = type;
        this.mMnemonicBackup = mMnemonicBackup;
        this.mBackup = mBackup;
        this.defwallet = defwallet;
    }

    @Generated(hash = 1363662176)
    public WalletEntity() {
    }

    public byte[] getmPublicKey() {
        return mPublicKey;
    }

    public void setmPublicKey(byte[] mPublicKey) {
        this.mPublicKey = mPublicKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDefwallet() {
        return defwallet;
    }

    public void setDefwallet(int defwallet) {
        this.defwallet = defwallet;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getmMnemonic() {
        return mMnemonic;
    }

    public void setmMnemonic(byte[] mMnemonic) {
        this.mMnemonic = mMnemonic;
    }

    public byte[] getmKeystore() {
        return mKeystore;
    }

    public void setmKeystore(byte[] mKeystore) {
        this.mKeystore = mKeystore;
    }

    public byte[] getmPrivateKey() {
        return mPrivateKey;
    }

    public void setmPrivateKey(byte[] mPrivateKey) {
        this.mPrivateKey = mPrivateKey;
    }

    
    public String getAllAddress() {
        if (type == WalletUtil.MCC_COIN && !TextUtils.isEmpty(mAddress2)) {
            return mAddress2;
        }
        return mAddress;
    }

    
    public String getDefaultAddress() {
        return mAddress;
    }

    
    public String getAllAddress2() {
        return mAddress2;
    }

    public String getmAddress() {
        String walletAddress = (type == WalletUtil.MCC_COIN && !TextUtils.isEmpty(mAddress2)) ? mAddress2 : mAddress;

        if(null==walletAddress||walletAddress.length()<10){
            return walletAddress+"";
        }
        String address=walletAddress.substring(0,10);
        int len=walletAddress.length();
        address=address+"..."+walletAddress.substring(len-10,len);
        return address;
    }

    
    public String getmDefaultAddress() {
        String walletAddress = mAddress;

        if(null==walletAddress||walletAddress.length()<10){
            return walletAddress+"";
        }
        String address=walletAddress.substring(0,10);
        int len=walletAddress.length();
        address=address+"..."+walletAddress.substring(len-10,len);
        return address;
    }

    public String getmAddress2() {
        if(null==mAddress2||mAddress2.length()<10){
            return mAddress2+"";
        }
        String address=mAddress2.substring(0,10);
        int len=mAddress2.length();
        address=address+"..."+mAddress2.substring(len-10,len);
        return address;
    }

    public String getChatAddress() {
        return chatAddress;
    }

    public void setChatAddress(String chatAddress) {
        this.chatAddress = chatAddress;
    }

    public String getChatEthAddress() {
        return chatEthAddress;
    }

    public void setChatEthAddress(String chatEthAddress) {
        this.chatEthAddress = chatEthAddress;
    }

    public String getChatPrivateKey() {
        return chatPrivateKey;
    }

    public void setChatPrivateKey(String chatPrivateKey) {
        this.chatPrivateKey = chatPrivateKey;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getSumPrice() {
        if(null==sumPrice|| TextUtils.isEmpty(sumPrice)){
            sumPrice="0.00";
        }
        return sumPrice;
    }

    public void setSumPrice(String sumPrice) {
        this.sumPrice = sumPrice;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
    public void setmAddress2(String mAddress) {
        this.mAddress2 = mAddress;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getmPassword() {
        if(mPassword==null){
            mPassword="";
        }
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmPasswordHint() {
        return mPasswordHint;
    }

    public void setmPasswordHint(String mPasswordHint) {
        this.mPasswordHint = mPasswordHint;
    }

    public String getmCurrency() {
        return mCurrency;
    }

    public void setmCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public String getmQuotes() {
        return mQuotes;
    }

    public void setmQuotes(String mQuotes) {
        this.mQuotes = mQuotes;
    }

    public int getmBackup() {
        return mBackup;
    }

    public void setmBackup(int mBackup) {
        this.mBackup = mBackup;
    }

    public int getLogo() {
        return Logo;
    }

    public String getTrueLogo() {
        return AppHolder.getLogoByAddress(getAllAddress());
    }

    public void setLogo(int logo) {
        Logo = logo;
    }

    public String getmBalance() {
        if(null==mBalance|| TextUtils.isEmpty(mBalance)||mBalance.equals("null")){
            mBalance="0";
        }
        return mBalance;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getBroadband() {
        return broadband;
    }

    public void setBroadband(String broadband) {
        this.broadband = broadband;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setmBalance(String mBalance) {
        this.mBalance = mBalance;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getEnergyMax() {
        return energyMax;
    }

    public int getEnergyIntMax() {
        int max=0;
        try {
            max=new BigDecimal(energyMax).intValue();
        }catch (Exception e){

        }
        return max;
    }

    public void setEnergyMax(String energyMax) {
        this.energyMax = energyMax;
    }

    public String getBroadbandMax() {
        return broadbandMax;
    }
    public int getBroadbandIntMax() {
        int max=0;
        try {
            max=new BigDecimal(broadbandMax).intValue();
        }catch (Exception e){

        }
        return max;
    }

    public void setBroadbandMax(String broadbandMax) {
        this.broadbandMax = broadbandMax;
    }

    public byte[] getMMnemonic() {
        return this.mMnemonic;
    }

    public void setMMnemonic(byte[] mMnemonic) {
        this.mMnemonic = mMnemonic;
    }

    public byte[] getMKeystore() {
        return this.mKeystore;
    }

    public void setMKeystore(byte[] mKeystore) {
        this.mKeystore = mKeystore;
    }

    public byte[] getMPrivateKey() {
        return this.mPrivateKey;
    }

    public void setMPrivateKey(byte[] mPrivateKey) {
        this.mPrivateKey = mPrivateKey;
    }

    public byte[] getMPublicKey() {
        return this.mPublicKey;
    }

    public void setMPublicKey(byte[] mPublicKey) {
        this.mPublicKey = mPublicKey;
    }

    public String getMAddress() {
        return this.mAddress;
    }

    public void setMAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getMAddress2() {
        return this.mAddress2;
    }

    public void setMAddress2(String mAddress) {
        this.mAddress2 = mAddress;
    }



    public String getMPassword() {
        return this.mPassword;
    }

    public void setMPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getMPasswordHint() {
        return this.mPasswordHint;
    }

    public void setMPasswordHint(String mPasswordHint) {
        this.mPasswordHint = mPasswordHint;
    }

    public String getMCurrency() {
        return this.mCurrency;
    }

    public void setMCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public String getMQuotes() {
        return this.mQuotes;
    }

    public void setMQuotes(String mQuotes) {
        this.mQuotes = mQuotes;
    }

    public String getMBalance() {
        return this.mBalance;
    }

    public void setMBalance(String mBalance) {
        this.mBalance = mBalance;
    }

    public String getMPrice() {
        return this.mPrice;
    }

    public void setMPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public int getLevel() {
        return this.Level;
    }

    public void setLevel(int Level) {
        this.Level = Level;
    }

    
    public boolean isHDWallet(){
        return this.Level ==1;
    }

    
    public boolean isObserveWallet(){
        return this.Level == -1;
    }

    public int getMMnemonicBackup() {
        return this.mMnemonicBackup;
    }

    public void setMMnemonicBackup(int mMnemonicBackup) {
        this.mMnemonicBackup = mMnemonicBackup;
    }

    public int getMBackup() {
        return this.mBackup;
    }

    public void setMBackup(int mBackup) {
        this.mBackup = mBackup;
    }

    public void setDefwallet(Integer defwallet) {
        this.defwallet = defwallet;
    }

    
    public String decodePrivateKey(String pwd) {
        String privateKey = WalletUtil.getDecryptionKey(getmPrivateKey(), pwd);
        return privateKey;
    }

    
    public String decodePrivateKey(String pwd, boolean removeOx){
        String privateKey = decodePrivateKey(pwd);
        if (!TextUtils.isEmpty(privateKey) && privateKey.startsWith("0x") && removeOx){
            privateKey = privateKey.substring(2);
        }
        return privateKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeByteArray(this.mMnemonic);
        dest.writeByteArray(this.mKeystore);
        dest.writeByteArray(this.mPrivateKey);
        dest.writeByteArray(this.mPublicKey);
        dest.writeString(this.mAddress);
        dest.writeString(this.mAddress2);
        dest.writeString(this.chatAddress);
        dest.writeString(this.chatEthAddress);
        dest.writeString(this.chatPrivateKey);
        dest.writeString(this.Name);
        dest.writeInt(this.Logo);
        dest.writeString(this.mPassword);
        dest.writeString(this.mPasswordHint);
        dest.writeString(this.mCurrency);
        dest.writeString(this.mQuotes);
        dest.writeString(this.mBalance);
        dest.writeString(this.mPrice);
        dest.writeString(this.sumPrice);
        dest.writeString(this.userName);
        dest.writeInt(this.Level);
        dest.writeString(this.walletId);
        dest.writeInt(this.type);
        dest.writeInt(this.mMnemonicBackup);
        dest.writeInt(this.mBackup);
        dest.writeValue(this.defwallet);
        dest.writeString(this.energy);
        dest.writeString(this.broadband);
        dest.writeString(this.energyMax);
        dest.writeString(this.broadbandMax);
    }

    protected WalletEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mMnemonic = in.createByteArray();
        this.mKeystore = in.createByteArray();
        this.mPrivateKey = in.createByteArray();
        this.mPublicKey = in.createByteArray();
        this.mAddress = in.readString();
        this.mAddress2 = in.readString();
        this.chatAddress = in.readString();
        this.chatEthAddress = in.readString();
        this.chatPrivateKey = in.readString();
        this.Name = in.readString();
        this.Logo = in.readInt();
        this.mPassword = in.readString();
        this.mPasswordHint = in.readString();
        this.mCurrency = in.readString();
        this.mQuotes = in.readString();
        this.mBalance = in.readString();
        this.mPrice = in.readString();
        this.sumPrice = in.readString();
        this.userName = in.readString();
        this.Level = in.readInt();
        this.walletId = in.readString();
        this.type = in.readInt();
        this.mMnemonicBackup = in.readInt();
        this.mBackup = in.readInt();
        this.defwallet = (Integer) in.readValue(Integer.class.getClassLoader());
        this.energy = in.readString();
        this.broadband = in.readString();
        this.energyMax = in.readString();
        this.broadbandMax = in.readString();
    }

    public static final Creator<WalletEntity> CREATOR = new Creator<WalletEntity>() {
        @Override
        public WalletEntity createFromParcel(Parcel source) {
            return new WalletEntity(source);
        }

        @Override
        public WalletEntity[] newArray(int size) {
            return new WalletEntity[size];
        }
    };

    @Override
    public String toString() {
        return "WalletEntity{" +
                "id=" + id +
                ", mMnemonic=" + Arrays.toString(mMnemonic) +
                ", mKeystore=" + Arrays.toString(mKeystore) +
                ", mPrivateKey=" + Arrays.toString(mPrivateKey) +
                ", mPublicKey=" + Arrays.toString(mPublicKey) +
                ", mAddress='" + mAddress + '\'' +
                ", mAddress2='" + mAddress2 + '\'' +
                ", chatAddress='" + chatAddress + '\'' +
                ", chatEthAddress='" + chatEthAddress + '\'' +
                ", chatPrivateKey='" + chatPrivateKey + '\'' +
                ", Name='" + Name + '\'' +
                ", Logo=" + Logo +
                ", mPassword='" + mPassword + '\'' +
                ", mPasswordHint='" + mPasswordHint + '\'' +
                ", mCurrency='" + mCurrency + '\'' +
                ", mQuotes='" + mQuotes + '\'' +
                ", mBalance='" + mBalance + '\'' +
                ", mPrice='" + mPrice + '\'' +
                ", sumPrice='" + sumPrice + '\'' +
                ", userName='" + userName + '\'' +
                ", Level=" + Level +
                ", walletId='" + walletId + '\'' +
                ", type=" + type +
                ", mMnemonicBackup=" + mMnemonicBackup +
                ", mBackup=" + mBackup +
                ", defwallet=" + defwallet +
                ", energy='" + energy + '\'' +
                ", broadband='" + broadband + '\'' +
                ", energyMax='" + energyMax + '\'' +
                ", broadbandMax='" + broadbandMax + '\'' +
                '}';
    }
}
