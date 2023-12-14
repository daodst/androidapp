package im.wallet.router.app;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


public interface IAppNotice {

    void postPhone(String phone);

    
    Intent goWhiteListAcitivty(Context from, String userId, String mobile);

    
    Intent goBlackListAcitivty(Context from, String userId, String mobile);

    
    Intent goRemarkListActivity(Context from);


    
    void chatDeviceBalance(FragmentManager manager, String balance, ICallBack iCallBack);

    void showBalanceDialog(FragmentActivity activity, int type, String roomId, String from, String to, Long time, ICallBack iCallBack);

    void chatDeviceBalanceSuccess();

    
    void chatPledgeBalance(FragmentManager manager, String balance, ICallBack iCallBack);

    void chatPledgeBalanceSuccess();

    public interface ICallBack {

        
        void call(int status, String balance);
    }

    
    void refreshChatEngine();

    
    void insertGroupVoteNotice(String groupId, String senderNickName);
}
