package com.app.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.FragmentVHomeBinding;
import com.app.databinding.FragmentVHomeTopBinding;
import com.app.home.pojo.DposInfo;
import com.app.home.pojo.MyPledge;
import com.app.home.pojo.rpc.DposAwardParam;
import com.app.home.ui.adapter.VHomeItemAdapter;
import com.app.home.ui.dialog.VGainawardFragment;
import com.app.home.ui.vote.list.VoteListActivity;
import com.app.view.dposmarket.DPosMarketActivity;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.util.List;

import common.app.base.BaseFragment;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.utils.TimeUtil;
import im.vector.app.provide.ChatStatusProvide;


public class VHomeFragment extends BaseFragment<VHomeVM> {

    FragmentVHomeBinding mVHomeBinding;
    FragmentVHomeTopBinding mVHomeTopBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVHomeBinding = FragmentVHomeBinding.inflate(inflater, container, false);
        return mVHomeBinding.getRoot();
    }

    private VHomeItemAdapter mHomeItemAdapter;

    @Override
    public boolean isLogined() {
        return ChatStatusProvide.loginStatus(getContext());
    }

    
    private int mDecimal = 18;

    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            RxNotice notice = (RxNotice) obj;
            if (notice.mType == RxNotice.MSG_SUBMIT_VOTE) {
                getViewModel().getVoteInfo();
            }
        }
    }

    @Override
    public void initView(@Nullable View view) {


        
        mVHomeTopBinding = mVHomeBinding.fragmentVHomeTop;
        mVHomeBinding.vHomeIvCloseOpenParent.setOnClickListener(v -> {
            int visibility = mVHomeTopBinding.getRoot().getVisibility();
            if (visibility == View.VISIBLE) {

                mVHomeBinding.vHomeIvCloseTips.setText(R.string.vhome_open);
                mVHomeBinding.vHomeIvCloseOpen.setImageResource(R.drawable.v_home_iv_top_open);
                mVHomeTopBinding.getRoot().setVisibility(View.GONE);
            } else {
                mVHomeBinding.vHomeIvCloseTips.setText(R.string.vhome_close);
                mVHomeTopBinding.getRoot().setVisibility(View.VISIBLE);
                mVHomeBinding.vHomeIvCloseOpen.setImageResource(R.drawable.v_home_iv_top_close);
            }
            mVHomeBinding.vHomeNsv.setFocusable(true);
            mVHomeBinding.vHomeNsv.setFocusableInTouchMode(true);
            mVHomeBinding.vHomeNsv.scrollTo(0, 0);
        });

        mHomeItemAdapter = new VHomeItemAdapter();
        mVHomeBinding.vHomeIvBottom.setAdapter(mHomeItemAdapter);

        
        mVHomeBinding.vHomeVoteMore.setOnClickListener(v -> {
            startActivity(new Intent(mContext, VoteListActivity.class));
        });


        
        mVHomeBinding.vHomeGainBt.setOnClickListener(v -> {


            MyPledge value = getViewModel().mMyPledgeLiveData.getValue();
            if (null == value || TextUtils.isEmpty(value.getPledge_amount(mDecimal))) {
                return;
            }
            
            if (new BigDecimal(value.getPledge_amount()).compareTo(new BigDecimal(0)) == 0) {
                showToast(getString(R.string.lq_dpos_value_alert));
                return;
            }

            

            WalletEntity walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfo(WalletUtil.MCC_COIN);
            if (null == walletEntity) {
                ToastUtil.showToast(R.string.get_wallet_address_fail);
                return;
            }
            
            dopsGain(walletEntity);
        });


        
        getViewModel().mLiveData.observe(this, result -> {
            if (null != mHomeItemAdapter) {
                mHomeItemAdapter.setVoteInfos(result);
            }
        });


        getViewModel().mGasLiveData.observe(requireActivity(), info -> {

            DposAwardParam param = null;
            if (info.param instanceof DposAwardParam) {
                param = (DposAwardParam) info.param;
            } else {
                return;
            }

            DposAwardParam finalParam = param;
            TransConfirmDialogBuilder.builder(requireActivity(), info.mWalletEntity).amount("")
                    
                    .fromAddress(param.delegator_address)
                    
                    .toAddress(param.validator_address)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.v_home_gain_bt_tips))
                    
                    .gasFeeWithToken(info.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().dposaward(finalParam, info.mWalletEntity, pwd, info);

                    }).show();
        });

        getViewModel().mDposGainLive.observe(this, aBoolean -> {
            getViewModel().getTopInfo();
            getViewModel().getVoteInfo();
        });


        
        getViewModel().mDposInfoLiveData.observe(this, this::showTopInfo);
        getViewModel().mMyPledgeLiveData.observe(this, this::showMiddleInfo);


        mVHomeBinding.vHomeWalletAddress.setOnClickListener(v -> {
            ChooseWalletDialog.showDialog(requireActivity(), WalletUtil.MCC_COIN, ((address1, walletType) -> {
                
                getViewModel().getTopInfo();
                getViewModel().getVoteInfo();
            }));

        });

        
        List<AssertBean> assets = WalletDBUtil.getInstent(mContext).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }


    private void dopsGain(WalletEntity entity) {
        
        VGainawardFragment dialog = new VGainawardFragment();
        dialog.show(getChildFragmentManager(), dialog.getTag());
        dialog.setIConsume(() -> {
            getViewModel().dposaward(entity);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getViewModel().getTopInfo();
        getViewModel().getVoteInfo();
    }

    private void showMiddleInfo(MyPledge myPledge) {
        WalletEntity info = WalletDBUtil.getInstent(mContext).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != info) {
            address = info.getAllAddress();
        }
        String coinName = getString(R.string.default_token_name).toUpperCase();
        mVHomeBinding.vHomeWalletNum.setText(myPledge.getPledge_amount(mDecimal) + " " + coinName);

        mVHomeBinding.vHomeWalletAddress.setText(address);
        if (null != mInfo) {
            mVHomeBinding.vHomeWalletRate.setText(mInfo.getMyWalletRate(myPledge.getPledge_amount()));
        } else {
            mVHomeBinding.vHomeWalletRate.setText("-- %");
        }

        String withdraw = myPledge.getCan_withdraw(mDecimal);
        if (TextUtils.isEmpty(withdraw)) {
            mVHomeBinding.vHomeWalletBounsWait.setText("--");
        } else {
            mVHomeBinding.vHomeWalletBounsWait.setText(withdraw);
        }

        if (TextUtils.isEmpty(myPledge.commission)) {
            mVHomeBinding.vHomeWalletBounsNew.setVisibility(View.GONE);
            mVHomeBinding.vHomeWalletBounsNewTips.setVisibility(View.GONE);
        } else {
            mVHomeBinding.vHomeWalletBounsNew.setVisibility(View.VISIBLE);
            mVHomeBinding.vHomeWalletBounsNewTips.setVisibility(View.VISIBLE);
            String commission = myPledge.getCommission(mDecimal);
            if (TextUtils.isEmpty(commission)) {
                mVHomeBinding.vHomeWalletBounsNew.setText("--");
            } else {
                mVHomeBinding.vHomeWalletBounsNew.setText(commission);
            }
        }

    }

    private DposInfo mInfo;

    private void showTopInfo(DposInfo info) {
        mInfo = info;
        WalletEntity walletInfo = WalletDBUtil.getInstent(mContext).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != walletInfo) {
            address = walletInfo.getAllAddress();
            getViewModel().getMyPledge(address);
        }
        mVHomeTopBinding.thomeSupplyNum.setText(info.getSupplyNum(mDecimal));
        mVHomeTopBinding.thomeSupplyRate.setText(info.getSupplyRate());

        DposInfo.Staking staking = info.staking_params;
        if (null != staking) {
            
            mVHomeBinding.vHomeWalletBt.setOnClickListener(v -> {
                
                startActivity(DPosMarketActivity.getIntent(mContext));
            });

            mVHomeTopBinding.thomeArgs1MaxNum.setText(staking.max_entries);
            mVHomeTopBinding.thomeArgs1Time.setText(TimeUtil.getTimeFormatText2(staking.unbonding_time));
            mVHomeTopBinding.thomeArgs1Num.setText(staking.max_validators);
        }
        DposInfo.Distribution tax = info.distribution_params;
        if (null != tax) {
            mVHomeTopBinding.thomeArgs2Rate.setText(tax.getCommunity_tax());
            mVHomeTopBinding.thomeArgs2Bouns.setText(tax.getBase_proposer_reward());
            mVHomeTopBinding.thomeArgs2BounsMore.setText(tax.getBonus_proposer_reward());
        }
        DposInfo.Slashing slashing = info.slashing_params;
        if (null != slashing) {
            mVHomeTopBinding.thomeArgs3Times.setText(slashing.signed_blocks_window + " ");
            mVHomeTopBinding.thomeArgs3Rate.setText(slashing.getMin_signed_per_window());
            mVHomeTopBinding.thomeArgs3Time.setText(TimeUtil.getTimeFormatText2(slashing.downtime_jail_duration));
            mVHomeTopBinding.thomeArgs3DeRate.setText(slashing.getSlash_fraction_double_sign());
            mVHomeTopBinding.thomeArgs3DeRateOffline.setText(slashing.getSlash_fraction_downtime());
        }

    }
}
