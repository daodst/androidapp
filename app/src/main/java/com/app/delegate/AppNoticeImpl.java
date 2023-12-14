package com.app.delegate;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.app.R;
import com.app.anim.AnimationAssistanceHelper;
import com.app.anim.AnimationType;
import com.app.anim.DevicesAwardDialogFragment;
import com.app.anim.PledgeAwardDialogFragment;
import com.app.chain.ChainSyncActivity;
import com.app.chat_engine.ChatEngine;
import com.app.delegate.notice.PhoneNotice;

import common.app.RxBus;
import common.app.utils.AllUtils;
import im.wallet.router.app.IAppNotice;

public class AppNoticeImpl implements IAppNotice {

    private DevicesAwardDialogFragment mDevicesAwardDialogFragment;
    private PledgeAwardDialogFragment mPledgeAwardDialogFragment;

    @Override
    public void postPhone(String phone) {
        
        RxBus.getInstance().post(new PhoneNotice(phone));
    }

    @Override
    public Intent goWhiteListAcitivty(Context from, String userId, String mobile) {
        return ChainSyncActivity.getWhiteIntent(from, userId, mobile);
    }

    @Override
    public Intent goBlackListAcitivty(Context from, String userId, String mobile) {
        return ChainSyncActivity.getBlackIntent(from, userId, mobile);
    }

    @Override
    public Intent goRemarkListActivity(Context from) {
        return new Intent(from, ChainSyncActivity.class);
    }

    @Override
    public void chatDeviceBalance(FragmentManager manager, String balance, ICallBack iCallBack) {
    }

    
    @Override
    public void showBalanceDialog(FragmentActivity activity, int type, String roomId, String from, String to, Long time, ICallBack iCallBack) {
        if(type == AnimationType.TYPE_POS_DEVICES && null != time && time > 0 && null != iCallBack){
            
            if(!AllUtils.isNowDayTime(time)) {
                Toast.makeText(activity, activity.getString(R.string.pos_reward_has_burn_next_fast), Toast.LENGTH_SHORT).show();
                iCallBack.call(3, "");
                return;
            }
        }

        AnimationAssistanceHelper.getInstance(activity, to, from, roomId)
                .setAnimationType(type).setCallbackConsumer((p1, p2) -> {
                    
                    if (p1.equals("-1") && type == AnimationType.TYPE_POS_DEVICES) {
                        Toast.makeText(activity, R.string.anim_reward_string_1, Toast.LENGTH_SHORT).show();
                        iCallBack.call(2, p2);
                        return;
                    }
                    iCallBack.call(1, p2);
                })
                .init();
    }

    @Override
    public void chatDeviceBalanceSuccess() {
        if (null != mDevicesAwardDialogFragment) {
            mDevicesAwardDialogFragment.shareOnClick();
        }
    }

    @Override
    public void chatPledgeBalance(FragmentManager manager, String balance, ICallBack iCallBack) {
    }

    @Override
    public void chatPledgeBalanceSuccess() {
        if (null != mPledgeAwardDialogFragment) mPledgeAwardDialogFragment.shareOnClick();
    }

    @Override
    public void refreshChatEngine() {
        
        ChatEngine.getInstance().refresh();
    }

    @Override
    public void insertGroupVoteNotice(String groupId, String senderNickName) {
        
        ChatEngine.getInstance().insertGroupVoteNotice(groupId, senderNickName);
    }
}
