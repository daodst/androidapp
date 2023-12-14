

package common.app.my;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



public class RxNotice {
    
    public static final int MSG_UPDATE_BANKCARD_LIST = 1;

    
    public static final int MSG_UPDATE_ADDRESS_LIST = 2;

    
    public static final int MSG_LOGOUT = 3;

    
    public static final int MSG_UPDATE_APPROVE_STATE = 4;

    
    public static final int MSG_WALLET_CHANGE = 5;

    
    public static final int MSG_MOBILE_BIND_CHANGE = 6;

    
    public static final int MSG_WEIXIN_LOGIN = 7;

    
    public static final int MSG_UPDATE_SKILL_LIST = 8;

    
    public static final int MSG_ORDERNUM = 9;

    
    public static final int MSG_COLLECTNUM = 10;
    
    public static final int MSG_ZUJI = 11;

    
    public static final int MSG_ONLINE_WALLET= 12;

    
    public static final int MSG_DELETE_WALLET= 13;

    
    public static final int MSG_WALLET_NUM_CHANGE = 14;

    
    public static final int MSG_SUBMIT_VOTE = 15;

    
    public static final int MSG_START_CHAIN_BRIDGE_TASK = 16;

    
    public static final int MSG_REFRESH_CHAT_ENGINE = 17;

    
    public static final int MSG_RPC_NODE_CHANGE = 18;

    @IntDef({MSG_UPDATE_BANKCARD_LIST, MSG_UPDATE_ADDRESS_LIST,MSG_ONLINE_WALLET, MSG_LOGOUT,MSG_UPDATE_APPROVE_STATE,
            MSG_WALLET_CHANGE,MSG_MOBILE_BIND_CHANGE,MSG_WEIXIN_LOGIN,MSG_UPDATE_SKILL_LIST,
            MSG_ORDERNUM,MSG_COLLECTNUM,MSG_ZUJI,MSG_DELETE_WALLET,MSG_WALLET_NUM_CHANGE, MSG_SUBMIT_VOTE,
            MSG_START_CHAIN_BRIDGE_TASK, MSG_REFRESH_CHAT_ENGINE, MSG_RPC_NODE_CHANGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NoticeType { }

    public int mType;

    public String mData;

    public RxNotice(@NoticeType int type) {
        mType = type;
    }

    public void setData(String data) {
        this.mData = data;
    }
}
