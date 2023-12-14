package im.wallet.router.wallet.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;


public class DeviceGroupMember{

    @SerializedName("member_address")
    public String memberAddress;
    @SerializedName("index_num")
    public String indexNum;
    @SerializedName("chat_address")
    public String chatAddress;


    
    public DeviceGroupMember(String userId, String chatAddress) {
        this.memberAddress = getAddressByUid(userId);
        this.indexNum = getIndexNumByUserId(userId);
        this.chatAddress = chatAddress;
    }

    
    public DeviceGroupMember(String address, String indexNum, String chatAddress) {
        this.memberAddress = address;
        this.indexNum = indexNum;
        this.chatAddress = chatAddress;
    }


    
    public static String getAddressByUid(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return userId;
        }
        String address = userId;
        if (address.startsWith("@")) {
            address = address.substring(1);
        }
        if (address.contains(":")) {
            String[] strs = address.split(":");
            address = strs[0];

        }
        return address;
    }


    
    public static String getIndexNumByUserId(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return userId;
        }
        String indexNum = "";
        if (userId.contains(":")) {
            String[] strs = userId.split(":");
            if (strs.length == 2) {
                String str = strs[1];
                int index = str.indexOf(".");
                if (index != -1 && index > 0) {
                    indexNum = str.substring(0, index);
                }
            }
        }
        return indexNum;
    }

    @Override
    public String toString() {
        return "DeviceGroupMember{" +
                "memberAddress='" + memberAddress + '\'' +
                ", indexNum='" + indexNum + '\'' +
                ", chatAddress='" + chatAddress + '\'' +
                '}';
    }
}
