package com.app.anim;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.app.R;
import com.app.chat_engine.ChatEngine;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.notice.BaseChatNotice;
import com.app.chat_engine.notice.GetAirDropWalletNotice;
import com.app.me.destory_group.DestoryGroupActivity;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.ui.pay.TransferControlApi;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

import common.app.mall.util.ToastUtil;
import common.app.utils.LogUtil;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.listener.TranslationListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AnimationAssistanceHelper {
    private final FragmentActivity mActivity;
    private final String toAddress, fromAddress;
    
    private String groupId;
    
    private String amount = "0", gasTax = "0";
    private BiConsumer<String, String> mCallbackConsumer;

    private int mAnimationType = 0;
    private EvmosSeqGasBean mSeqGasBean;

    private AnimationAssistanceHelper(FragmentActivity activity, String toAddr, String fromAddr) {
        this.mActivity = activity;
        this.toAddress = toAddr;
        this.fromAddress = fromAddr;
    }

    
    private AnimationAssistanceHelper(FragmentActivity activity, String toAddr, String fromAddr, String groupId) {
        this(activity, toAddr, fromAddr);
        this.groupId = groupId;
    }

    
    public static AnimationAssistanceHelper getInstance(FragmentActivity context, String toAddr, String fromAddr, String groupId) {
        return new AnimationAssistanceHelper(context, toAddr, fromAddr, groupId);
    }

    
    public AnimationAssistanceHelper setCallbackConsumer(BiConsumer<String, String> pCallbackConsumer) {
        mCallbackConsumer = pCallbackConsumer;
        return this;
    }

    
    public AnimationAssistanceHelper setAnimationType(@AnimationType int pAnimationType) {
        mAnimationType = pAnimationType;
        return this;
    }

    public void init() {
        

        
        if (mAnimationType == AnimationType.TYPE_POS_DEVICES) {
            
            getRewardParam((isZero, amount) -> {
                if (isZero) {
                    mCallbackConsumer.accept("-1", amount);
                    return;
                }
                showPosDevicesAnim();
            });
        } else if (mAnimationType == AnimationType.TYPE_SALARY) {
            
            getRewardParam((isZero, amount) -> {
                if (isZero) {
                    mCallbackConsumer.accept("-1", amount);
                    ToastUtil.showToast(mActivity.getString(R.string.device_group_has_get_reward));
                    return;
                }
                showSalaryAnim();
            });
        } else if (mAnimationType == AnimationType.TYPE_DVM_VIRTUAL) {
            
            
            getRewardParam((isZero, amount) ->{
                if (isZero) {
                    mCallbackConsumer.accept("-1", amount);
                    ToastUtil.showToast(mActivity.getString(R.string.device_group_has_get_reward));
                    return;
                }
                showDvmVirtualAnim();
            });
        } else if (mAnimationType == AnimationType.TYPE_AIRDROP) {
            
            getAriDropReward(toAddress);
        }
    }

    private void showPosDevicesAnim() {
        DevicesAwardDialogFragment fragment = new DevicesAwardDialogFragment();
        fragment.setGroupId(groupId);
        fragment.setData(Float.valueOf(amount), toAddress, fromAddress, gasTax, pUnused -> {
            
            getRewardResult(() -> {
                fragment.shareOnClick();
                
                mCallbackConsumer.accept("", amount);
            });
        });
        fragment.setClickConsumer(() -> {
            
            ChatStatusProvide.sendThanks(mActivity, groupId);
        });
        fragment.show(mActivity.getSupportFragmentManager(), "");
    }

    private void showSalaryAnim() {
        SalaryDialogFragment fragment = new SalaryDialogFragment();
        fragment.setData(Float.valueOf(amount), toAddress, fromAddress, gasTax, pUnused -> {
            
            getRewardResult(() -> {
                fragment.shareOnClick();
                
                mCallbackConsumer.accept("", amount);
            });
        });
        
        fragment.setClickConsumer(() -> {
            mActivity.startActivity(DestoryGroupActivity.getIntent(mActivity, toAddress, groupId));
        });
        fragment.show(mActivity.getSupportFragmentManager(), "");
    }

    private void showDvmVirtualAnim() {
        DvmVirtualDialogFragment fragment = new DvmVirtualDialogFragment();
        fragment.setGroupId(groupId);
        fragment.setData(Float.valueOf(amount), toAddress, fromAddress, gasTax, pUnused -> {
            
            getRewardResult(() -> {
                fragment.shareOnClick();
                
                mCallbackConsumer.accept("", amount);
            });
        });
        fragment.setClickConsumer(pInteger -> {
            if (pInteger == 1) {
                
                mActivity.startActivity(DestoryGroupActivity.getIntent(mActivity, toAddress, groupId));
            } else if (pInteger == 2) {
                
                ChatStatusProvide.sendThanks(mActivity, groupId);
            }
        });
        fragment.show(mActivity.getSupportFragmentManager(), "");
    }

    
    @SuppressLint("CheckResult")
    private void getRewardParam(BiConsumer<Boolean, String> pConsumer) {
        LoadingPopupView loading = new XPopup.Builder(mActivity).asLoading();
        loading.show();
        

        TransferControlApi controlApi = new TransferControlApi();
        Observable<EvmosSeqGasBean> observable = controlApi.getGasAndRewardAmount(mAnimationType, toAddress, groupId);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pEvmosSeqGasBean -> {
            LogUtil.d("Jues_Anim", new Gson().toJson(pEvmosSeqGasBean));
            gasTax = pEvmosSeqGasBean.getShowFee();
            amount = assetsExchange(pEvmosSeqGasBean.rewardAmount);
            if (amount == null) amount = "0";
            mSeqGasBean = pEvmosSeqGasBean;
            loading.dismiss();
            pConsumer.accept(isZeroAmount(amount), amount);
        });
    }

    private boolean isZeroAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return true;
        }
        try {
            if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) ==0) {
                return true;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return false;
    }

    
    private void getRewardResult(Runnable pConsumer) {
        TransferControlApi controlApi = new TransferControlApi();
        TranslationListener listener = new TranslationListener() {
            @Override
            public void onFail(String errorInfo) {
                ToastUtil.showToast(errorInfo);
            }

            @Override
            public void onTransSuccess() {
                pConsumer.run();
            }
        };
        if (mAnimationType == AnimationType.TYPE_POS_DEVICES) {
            
            controlApi.withdrawDeviceReward(mActivity, toAddress, groupId, mSeqGasBean, listener);
        } else if (mAnimationType == AnimationType.TYPE_DVM_VIRTUAL) {
            
            controlApi.withdrawBurnReward(mActivity, toAddress, groupId, mSeqGasBean, listener);
        } else if (mAnimationType == AnimationType.TYPE_SALARY) {
            
            controlApi.withdrawOwnerReward(mActivity, toAddress, groupId, mSeqGasBean, listener);
        }

        
        
    }


    
    private void getAriDropReward(String userAddr) {
        TransferControlApi controlApi = new TransferControlApi();
        String userId = ChatStatusProvide.getUserId(mActivity.getApplicationContext());
        String serverName = AllUtils.getServerNameByUid(userId);
        if (TextUtils.isEmpty(userAddr) || TextUtils.isEmpty(serverName)) {
            ToastUtil.showToast(R.string.data_error);
            return;
        }
        controlApi.withdrawAirDropReward(mActivity, userAddr, serverName, new TranslationListener() {
            @Override
            public void onFail(String errorInfo) {
                ToastUtil.showToast(errorInfo);
            }

            @Override
            public void onTransSuccess() {
                
                ChatNoticeDb.getInstance().ioUpdateNoticeState(userAddr, GetAirDropWalletNotice.getKey(),
                        BaseChatNotice.EVENT_STATE_WAITE_NOTICE, BaseChatNotice.EVENT_STATE_COMPLETE);
                ChatEngine.getInstance().resetLoginTime();
                if (null != mCallbackConsumer) {
                    String amount = (String) getData();
                    mCallbackConsumer.accept("", amount);
                }
            }
        });
    }

    
    private String assetsExchange(String bigAmount) {
        String dstCoinName = mActivity.getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(mActivity).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        return AllUtils.getTenDecimalValue(bigAmount, decimal, 4);
    }
}
