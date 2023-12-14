

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.pojo.BlackWhiteBean;
import common.app.pojo.FriendsRemarkBean;
import common.app.utils.digest.EAICoderUtil;


public class EvmosChatFeeBean extends EvmosHttpBean {

    public Data data;

    
    public static class Data {
        public String from_address; 
        public String node_address; 
        public String chat_restricted_mode;
        public String address_book; 
        public String chat_blacklist;
        public String chat_whitelist;
        public String chat_black_enc_list;
        public String chat_white_enc_list;
        private EvmosAmountsBean chat_fee;
        public List<String> mobile;
        public long update_time;
        public int pledge_level;

        public EvmosAmountsBean getChatFee() {
            return chat_fee;
        }


        public String getChatFeeAmount() {
            if (null == chat_fee) {
                return "";
            }
            return chat_fee.amount;
        }

        public String getChatFeeDenom() {
            if (null == chat_fee) {
                return "";
            }
            return chat_fee.denom;
        }
    }


    public boolean isValidate() {
        return data != null;
    }

    
    public String getBlackListEncryStr() {
        if (isValidate()) {
            return data.chat_blacklist;
        } else {
            return null;
        }
    }

    
    public String getWhiteListEncryStr() {
        if (isValidate()) {
            return data.chat_whitelist;
        } else {
            return null;
        }
    }

    
    public boolean isHasAddressBookData() {
        if (null != data && !TextUtils.isEmpty(data.address_book)) {
            return true;
        }
        return false;
    }

    
    public String getAddressBookEncryStr() {
        if (null != data) {
            return data.address_book;
        }
        return null;
    }

    
    public boolean isHasBlackOrWhiteDatas() {
        String blackList = getBlackListEncryStr();
        String whiteList = getWhiteListEncryStr();
        if (!TextUtils.isEmpty(blackList) || !TextUtils.isEmpty(whiteList)) {
            return true;
        } else {
            return false;
        }
    }

    
    public String getBlackWhiteHash() {
        return getBlackWhiteHash(getBlackListEncryStr(), getWhiteListEncryStr());
    }

    
    public static String getBlackWhiteHash(String blackEncryStr, String whiteEncryStr) {
        if (!TextUtils.isEmpty(blackEncryStr) || !TextUtils.isEmpty(whiteEncryStr)) {
            String data = blackEncryStr+blackEncryStr;
            return EAICoderUtil.getMD5Code(data);
        } else {
            return "";
        }
    }



    
    private static Gson mGson;
    private static Gson gson() {
        if (null == mGson) {
            mGson = new Gson();
        }
        return mGson;
    }
    
    public static String encryList(WalletEntity wallet, String pwd, List<String> datas) {
        if (null == datas || datas.size() == 0) {
            return "";
        }
        
        String listJsonStr = gson().toJson(datas);
        
        return ChatSdk.encodeUPri(wallet.decodePrivateKey(pwd), listJsonStr);
    }

    
    public static String encryList(String publickKey, List<String> datas) {
        if (null == datas || datas.size() == 0) {
            return "";
        }
        
        String listJsonStr = gson().toJson(datas);
        
        return ChatSdk.encode(publickKey, listJsonStr);
    }


    
    public static String encryListGateWay(String publickKey, List<String> datas) {
        if (null == datas || datas.size() == 0) {
            return "";
        }
        List<String> addressList = new ArrayList<>();
        for (String userid : datas) {
            addressList.add(AllUtils.getAddressByUid(userid));
        }
        
        String listJsonStr = gson().toJson(addressList);
        
        return ChatSdk.encode(publickKey, listJsonStr);
    }

    
    public static List<String> decryList(WalletEntity wallet, String pwd, String encryedStr) {
        if (TextUtils.isEmpty(encryedStr)) {
            return null;
        }
        String listJsonStr = ChatSdk.decode(wallet.decodePrivateKey(pwd), encryedStr);
        if (!TextUtils.isEmpty(listJsonStr)) {
            List<String> result = gson().fromJson(listJsonStr, new TypeToken<List<String>>(){}.getType());
            return result;
        }
        return null;
    }

    
    public static String encryAddressBook(WalletEntity wallet, String pwd, FriendsRemarkBean data) {
        if (null == data || data.isEmpty()) {
            return "";
        }
        String objectJsonStr = gson().toJson(data);
        
        return ChatSdk.encodeUPri(wallet.decodePrivateKey(pwd), objectJsonStr);
    }

    
    public static String encryAddressBook(String publicKey, FriendsRemarkBean data) {
        if (null == data || data.isEmpty()) {
            return "";
        }
        String objectJsonStr = gson().toJson(data);
        
        return ChatSdk.encode(publicKey, objectJsonStr);
    }


    
    public static FriendsRemarkBean decryAddresBook(WalletEntity wallet, String pwd, String encryedStr) {
        if (TextUtils.isEmpty(encryedStr)) {
            return null;
        }
        String objectJsonStr = ChatSdk.decode(wallet.decodePrivateKey(pwd), encryedStr);
        if (!TextUtils.isEmpty(objectJsonStr)) {
            FriendsRemarkBean result = gson().fromJson(objectJsonStr, FriendsRemarkBean.class);
            return result;
        }
        return null;
    }


    
    public static BlackWhiteBean blackWhiteFromJson(String dataJsonStr) {
        if (TextUtils.isEmpty(dataJsonStr)) {
            return null;
        }
        BlackWhiteBean data = gson().fromJson(dataJsonStr, BlackWhiteBean.class);
        return data;
    }

    
    public static String convertBlackWhiteJsonStr(List<String> blackList, List<String> whiteList) {
        BlackWhiteBean data = new BlackWhiteBean();
        if (null != blackList && blackList.size() > 0) {
            data.blacks = blackList;
        }
        if (null != whiteList && whiteList.size() > 0) {
            data.whites = whiteList;
        }
        String objectJsonStr = gson().toJson(data);
        return objectJsonStr;
    }
}
