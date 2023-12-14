package com.app.chat_engine.notice;

import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DAO;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DVM;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_POS;

import android.text.SpannableString;
import android.text.TextUtils;

import com.app.R;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class GetIncomeNotice extends BaseChatNotice {

    private int mRewardType;
    private int mDecimal = 18;
    private static final String REWARD_KEY = "GetIncome";
    public GetIncomeNotice(int type) {
        super(REWARD_KEY+type);
        this.mRewardType = type;
        String dstCoinName = getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(getAppContext()).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        if (decimal > 0) {
            mDecimal = decimal;
        }
    }

    @Override
    ChatCStateBean doCheckState() {
        
        List<DeviceGroupBean> nowDayNoNoticedGroups = getNowDayNoNoticeGroups();
        if (null == nowDayNoNoticedGroups || nowDayNoNoticedGroups.size() == 0) {
            logw("need notice groups is empty");
            return null;
        }


        List<DeviceGroupBean> needNoticeGroups = new ArrayList<>();
        String loginAddress = getLoginUserAddress();
        int chatMsgType = ChatCStateBean.CHAT_NONE;
        String contentStr = "";
        String chatTips = "";
        String msgTitle = "";
        String value = "";
        int headRes = 0;
        for (DeviceGroupBean group: nowDayNoNoticedGroups) {
            if (group != null) {
                String reward = "0";
                if (mRewardType == TIMELINE_AWARD_TYPE_POS && httpIsTodayActive() == STATE_ACTIVE) {
                    
                    reward = group.deviceReward;
                    chatMsgType = ChatCStateBean.CHAT_GET_DEVICE_INCOME;
                    contentStr = getString(R.string.nc_get_income_device_content);
                    chatTips = getString(R.string.nc_get_income_device_title);
                    headRes = R.drawable.icon_notice_devie_reward;
                } else if(mRewardType == TIMELINE_AWARD_TYPE_DVM) {
                    
                    reward = group.powerReward;
                    chatMsgType = ChatCStateBean.CHAT_GET_DVM_INCOME;
                    boolean first = isFirstNotice(group.groupId);
                    String firstUpRatio = "";
                    if (first && AllUtils.isNumberUpZero(reward)) {
                        String deviceRewardNum = group.deviceReward;
                        if (!AllUtils.isNumberUpZero(deviceRewardNum)) {
                            
                            DeviceGroupNoticeEntity noticeEntity = ChatNoticeDb.getInstance().getNoticeInfo(loginAddress, group.groupId, REWARD_KEY+TIMELINE_AWARD_TYPE_POS);
                            if (null != noticeEntity) {
                                deviceRewardNum = noticeEntity.getValue();
                            }
                        }
                        if (!AllUtils.isNumberUpZero(deviceRewardNum)) {
                            first = false;
                        } else {
                            firstUpRatio = getRewardUpRatio(deviceRewardNum, reward);
                        }
                    }
                    if (first) {
                        contentStr = getString(R.string.nc_get_income_dvm_content)+firstUpRatio;
                    } else {
                        contentStr = getString(R.string.nc_get_income_dvm_content2);
                    }
                    chatTips = getString(R.string.nc_get_income_dvm_title);
                    headRes = R.drawable.icon_notice_simple;

                } else if(mRewardType == TIMELINE_AWARD_TYPE_DAO) {
                    
                    reward = group.ownerReward;
                    chatMsgType = ChatCStateBean.CHAT_GET_OWNER_INCOME;
                    contentStr = getString(R.string.nc_get_income_owner_content);
                    chatTips = getString(R.string.nc_get_income_owner_title);
                    headRes = R.drawable.icon_notice_simple;
                }

                String tenValue = reward;
                try {
                    if (!TextUtils.isEmpty(tenValue) && new BigDecimal(tenValue).compareTo(new BigDecimal(0)) > 0) {
                        
                        needNoticeGroups.add(group);
                        value = tenValue;
                    }
                } catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }

        if (needNoticeGroups.size() > 0) {
            SpannableString content = new SpannableString(contentStr);
            ChatCStateBean state = createImportentNoticeState(content, chatTips, "Go", view -> {
                startRoomActivity(needNoticeGroups.get(0).groupId);
            });
            state.value = value;
            state.headRes = headRes;
            state.deviceGroups = needNoticeGroups;
            state.chatMsgType = chatMsgType;
            state.chatTips = chatTips;
            state.msgTitle = msgTitle;
            state.msgContent = "";
            return state;
        }

        return null;
    }


    
    public String getRewardUpRatio(String deviceReward, String dvmReward) {
        if (TextUtils.isEmpty(deviceReward)) {
            return dvmReward;
        }
        if (TextUtils.isEmpty(dvmReward)) {
            return "--";
        }
        try {
            BigDecimal deviceRewardB = new BigDecimal(deviceReward);
            BigDecimal dvmRewardB = new BigDecimal(dvmReward);

            if (deviceRewardB.compareTo(new BigDecimal(0)) > 0 && dvmRewardB.compareTo(new BigDecimal(0)) > 0) {
                
                String raido = dvmRewardB.divide(deviceRewardB, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).toPlainString()+"%";
                return raido;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return dvmReward;
    }

}
