package com.app.chat_engine.notice;

import static com.app.chat_engine.ChatEngine.PRE;
import static org.matrix.android.sdk.api.session.room.send.SendServiceKt.CUS_TEXT_TYPE_NORMAL;

import android.annotation.SuppressLint;
import android.text.SpannableString;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatEngine;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.ChatNoticeSp;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;
import com.wallet.ctc.model.blockchain.RpcApi2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GetAirDropWalletNotice extends BaseChatNotice {

    RpcApi2 mRpcApi;
    public static final String KEY = "airdropWallet";
    public static String getKey() {
        return PRE+KEY;
    }

    public GetAirDropWalletNotice() {
        super(KEY);
        mRpcApi = new RpcApi2();
    }

    @SuppressLint("CheckResult")
    @Override
    ChatCStateBean doCheckState() {
        List<DeviceGroupBean> systemNoticeGroups = ChatData.getInstance().httpGetSystemGroups();
        DeviceGroupBean systemNoticeGroup = null;
        if (systemNoticeGroups != null && systemNoticeGroups.size() > 0) {
            systemNoticeGroup = systemNoticeGroups.get(0);
        }
        if (systemNoticeGroup == null) {
            return null;
        }

        long senconds = ChatNoticeSp.getHasLoginTime(getLoginUserAddress());
        logi("seconds ="+senconds);
        if (senconds > 5*60) {
            DeviceGroupNoticeEntity notice = findNotice(systemNoticeGroup.groupId, key);
            if (notice != null && notice.getState() == EVENT_STATE_WAITE_NOTICE) {
                
                return null;
            }

            
            AtomicInteger canWithdraw = new AtomicInteger(0);
            mRpcApi.checkCanAirDrop(getLoginUserAddress()).subscribe(data->{
                if (null != data && data) {
                    canWithdraw.set(1);
                } else {
                    canWithdraw.set(2);
                }
            }, throwable -> {
                throwable.printStackTrace();
                canWithdraw.set(2);
            });
            if (canWithdraw.get() != 1) {
                
                ChatEngine.getInstance().resetLoginTime();
                return null;
            }

            
            
            SpannableString content = new SpannableString(getString(R.string.cn_airdrop_notice_content));
            String systemNoticeGroupId = systemNoticeGroup.groupId;
            ChatCStateBean state = createImportentNoticeState(content, getString(R.string.cn_airdrop_notice_title), "Go", view -> {
                startRoomActivity(systemNoticeGroupId);
            });
            state.headName = getString(R.string.cn_system_notice_title);
            state.headRes = R.drawable.icon_notice_airdrop;
            List<DeviceGroupBean> groups = new ArrayList<>();
            groups.add(systemNoticeGroup);
            state.deviceGroups = groups;
            state.chatMsgType = ChatCStateBean.CHAT_GET_AIRDROP_INCOME;
            state.chatTips = getString(R.string.cn_airdrop_notice_title);
            state.msgTitle = "";
            state.msgContent = content.toString();
            state.chatMsgSubType = CUS_TEXT_TYPE_NORMAL;
            return state;
        }
        return null;
    }


    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE && null != notice && null != notice.deviceGroups) {
            if (notice.deviceGroups.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                        key, EVENT_STATE_WAITE_NOTICE);
            }
        }
    }
}
