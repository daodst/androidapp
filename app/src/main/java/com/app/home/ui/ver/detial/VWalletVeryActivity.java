package com.app.home.ui.ver.detial;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.app.R;
import com.app.databinding.ActivityVwalletVeryBinding;
import com.app.databinding.ItemWalletVeryHeaderBinding;
import com.app.home.pojo.ValidatorDetailNew;
import com.app.home.pojo.rpc.DposPledgeParam;
import com.app.home.pojo.rpc.DposRedeemParam;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.app.home.ui.ver.detial.adapter.WalletVeryItemAdapter;
import com.app.home.ui.ver.detial.dialog.PledgedTokensPopup;
import com.app.home.ui.ver.detial.dialog.VWalletVeryDialogFragment;
import com.lxj.xpopup.XPopup;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ValidatorInfo;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.view.LoadMoreFooter;

import java.math.BigDecimal;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.utils.TimeUtil;


public class VWalletVeryActivity extends BaseActivity<VWalletVeryVM> implements PledgedTokensPopup.OnDidSegmentList {


    public static final String TAG = "VWalletVeryActivity";

    private ActivityVwalletVeryBinding mBinding;

    private static final String PARAM_ADDRESS = "PARAM_address";

    
    private PledgedTokensPopup mTokensPopup;

    public static Intent getIntent(Context context, String address) {
        Intent intent = new Intent(context, VWalletVeryActivity.class);
        intent.putExtra(PARAM_ADDRESS, address);
        return intent;
    }


    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityVwalletVeryBinding.inflate(getLayoutInflater());
        mAddress = getIntent().getStringExtra(PARAM_ADDRESS);
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
    }


    private WalletVeryItemAdapter mItemAdapter;
    private LoadMoreFooter mLoadMoreFooter;
    
    private int mDecimal = 18;

    @Override
    public void initData() {
        mBinding.walletVeryTopbar.setLeftTv(v -> {
            finish();
        }).setMiddleTv(R.string.v_walet_very_title, R.color.default_titlebar_title_color);

        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }

        
        bulidRv();
        
        bulidHeader();
        
        buildPopup();
    }

    private void bulidHeader() {
        initHeader();

        
        mHeaderBinding.veryHeaderPay.setOnClickListener(v -> {
            ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();

            if (value == null) {
                return;
            }
            
            WalletEntity walletEntity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
            if (null == walletEntity) {
                ToastUtil.showToast(getString(R.string.get_wallet_address_fail));
                return;
            }
            String operator_address = value.valAddr;
            
            String minSelfDelegation = value.getValidator_self_delagate(mDecimal);
            
            doRelease(walletEntity, operator_address, false);

        });

        
        mHeaderBinding.veryHeaderUnpay.setOnClickListener(v -> {
            ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();
            if (null == value) {
                return;
            }
            
            WalletEntity walletEntity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
            if (null == walletEntity) {
                ToastUtil.showToast(getString(R.string.get_wallet_address_fail));
                return;
            }
            String address = walletEntity.getAllAddress();

            if (TextUtils.isEmpty(address)) {
                ToastUtil.showToast(R.string.get_wallet_address_fail);
                return;
            }

            String operator_address = value.valAddr;
            
            doRelease(walletEntity, operator_address, true);
        });

        
        getViewModel().mDetailLiveData.observe(this, this::showHeaderInfo);

        WalletEntity walletInfo = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != walletInfo) {
            address = walletInfo.getAllAddress();
            getViewModel().getValidatorDetail(mAddress, address);
        }


        getViewModel().mGasLiveData.observe(VWalletVeryActivity.this, info -> {
            
            if (null != mTokensPopup) mTokensPopup.dismiss();

            String delegator_address;
            String validator_address;
            String input = info.consume;
            String title = "";
            boolean pledge = true;
            if (info.param instanceof DposPledgeParam) {
                pledge = true;
                DposPledgeParam param = (DposPledgeParam) info.param;
                delegator_address = param.delegator_address;
                validator_address = param.validator_address;
                title = getString(R.string.wallet_very_header_bt1);
            } else if (info.param instanceof DposRedeemParam) {
                DposRedeemParam param = (DposRedeemParam) info.param;
                pledge = false;
                delegator_address = param.delegator_address;
                validator_address = param.validator_address;
                title = getString(R.string.wallet_very_header_bt2);
            } else {
                return;
            }
            boolean finalPledge = pledge;
            TransConfirmDialogBuilder.builder(VWalletVeryActivity.this, info.mWalletEntity).amount(input)
                    
                    .fromAddress(delegator_address)
                    
                    .toAddress(validator_address)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(title)
                    
                    .gasFeeWithToken(info.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        if (finalPledge) {
                            getViewModel().dposPledge((DposPledgeParam) info.param, info.mWalletEntity, pwd, info);
                        } else {
                            getViewModel().dposRedeem((DposRedeemParam) info.param, info.mWalletEntity, pwd, info);
                        }

                    }).show();
        });

        
        getViewModel().mBlockOrRateData.observe(this, pBean -> {
            if (null == mTokensPopup) return;
            mTokensPopup.setTaxAndBlockHeight(pBean.data.rate, pBean.data.height);
        });
        
        getViewModel().mGatewayNumberCount.observe(this, pInteger -> {
            if (null == mTokensPopup) return;
            mTokensPopup.setMaxSelect(pInteger);
        });
    }

    private void buildPopup() {
        WalletEntity entity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        
        getViewModel().getRedeemToken(mAddress, entity.getAllAddress());
    }

    void doRelease(WalletEntity entity, String operator_address, boolean release) {
        
        ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();
        if (null == value) {
            return;
        }
        String name = value.name;
        if (release) {
            
            Boolean isGateway = getViewModel().mIsGateway.getValue();
            mTokensPopup = PledgedTokensPopup.getInstance(this, name, value.delegated, this, null != isGateway && isGateway);
            new XPopup.Builder(this)
                    .moveUpToKeyboard(false) 
                    .dismissOnTouchOutside(false)
                    .autoFocusEditText(false)
                    .asCustom(mTokensPopup)
                    .show();

        } else {
            
            VWalletVeryDialogFragment dialog = VWalletVeryDialogFragment.getInstance(value.balance);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            dialog.setIConsume(input -> {
                DposPledgeParam param = new DposPledgeParam();
                param.delegator_address = entity.getAllAddress();
                param.validator_address = operator_address;
                param.amount = new DposPledgeParam.Amount(input, mDecimal, getString(R.string.default_token_name));

                getViewModel().dposGas(RPCVoteParam.TYPE_MSGDELEGATE, param, input, entity);
            });
        }
    }

    private ValidatorInfo mInfo = null;

    private void bulidRv() {
        mItemAdapter = new WalletVeryItemAdapter(mDecimal);
        mBinding.walletVeryRv.setAdapter(mItemAdapter);

        mLoadMoreFooter = new LoadMoreFooter(this, mBinding.walletVeryRv, () -> {
            getViewModel().getValidatorInfo(mAddress, mInfo);
        });
        getViewModel().mLiveData.observe(this, info -> {
            if (null != mItemAdapter) {
                mInfo = info;
                mItemAdapter.setResults(mInfo.result);
                mLoadMoreFooter.setState(mInfo.isEnd ? LoadMoreFooter.STATE_FINISHED : LoadMoreFooter.STATE_ENDLESS);
            }
        });

        
        getViewModel().getValidatorInfo(mAddress, null);
    }

    @SuppressLint("SetTextI18n")
    private void showHeaderInfo(ValidatorDetailNew result) {
        
        
        
        
        
        

        
        if (!TextUtils.isEmpty(result.name)) {
            mHeaderBinding.hwalletVeryDetialName.setText(result.name);
        }
        String coinName = getString(R.string.default_token_name).toUpperCase();
        mHeaderBinding.hwalletVeryDetialNum.setText(result.getTenAmount(result.valDelegateAmount, mDecimal) + " " + coinName);

        try {
            
            String s = result.getRate(result.selfDelegateRate);
            mHeaderBinding.hwalletVeryDetialRate.setText(s);
        } catch (Exception e) {
            mHeaderBinding.hwalletVeryDetialRate.setText(" -- ");
            e.printStackTrace();
        }
        String tokenName = getString(R.string.default_token_name).toUpperCase();
        mHeaderBinding.hwalletVeryDetialMin.setText(result.getTenAmount(result.minSelfDelegate, mDecimal) + " " + tokenName);
        mHeaderBinding.hwalletVeryDetialId.setText(result.getHasOrNone(result.identify, this));
        
        
        if (result.status == 3) {
            mHeaderBinding.hwalletVeryDetialStatusActive.setVisibility(View.VISIBLE);
            mHeaderBinding.hwalletVeryDetialStatusLevel.setVisibility(View.GONE);
        } else  {
            mHeaderBinding.hwalletVeryDetialStatusActive.setVisibility(View.GONE);
            mHeaderBinding.hwalletVeryDetialStatusLevel.setVisibility(View.VISIBLE);
        }
        
        
        
        
        
        mHeaderBinding.hwalletVeryDetialHeight.setText(result.getHasOrNone(result.unbondHeight + "", this));
        if (result.unbondTime != 0) {
            mHeaderBinding.hwalletVeryDetialTime.setText(result.getHasOrNone(TimeUtil.getYYYYMMddHHMM(result.unbondTime), this));
        } else {
            mHeaderBinding.hwalletVeryDetialTime.setText("--");
        }
        if (result.jail) {
            mHeaderBinding.hwalletVeryDetialYes.setVisibility(View.VISIBLE);
            mHeaderBinding.hwalletVeryDetialNo.setVisibility(View.GONE);
        } else {
            mHeaderBinding.hwalletVeryDetialYes.setVisibility(View.GONE);
            mHeaderBinding.hwalletVeryDetialNo.setVisibility(View.VISIBLE);
        }

        mHeaderBinding.hwalletVeryDetialChat.setText(result.getHasOrNone(result.contact, this));
        String cname = getString(R.string.default_token_name).toUpperCase();
        mHeaderBinding.hwalletVeryDetialPayed.setText(result.getTenAmount(result.delegated, mDecimal) + " " + cname);
        
        
        
        
        mHeaderBinding.hwalletVeryDetialRebateTime.setText(TimeUtil.getYYYYMMddHHMM(result.updateTime * 1000L));
        mHeaderBinding.hwalletVeryDetialRebateRate.setText(result.getRate(result.commissionRate));
        mHeaderBinding.hwalletVeryDetialRebateMrate.setText(result.getRate(result.maxCommissionRate));
        mHeaderBinding.hwalletVeryDetialRebateRateDay.setText(result.getRate(result.maxChangeRate));

    }

    private ItemWalletVeryHeaderBinding mHeaderBinding;

    private void initHeader() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_wallet_very_header, mBinding.walletVeryRv.getHeaderContainer(), false);
        mHeaderBinding = ItemWalletVeryHeaderBinding.bind(view);
        mBinding.walletVeryRv.addHeaderView(view);
    }

    @Override
    public void onGetDidSegment() {
        WalletEntity walletInfo = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        
        
        
        mTokensPopup.setDataList(getViewModel().mPledgeTokenData.getValue());

        
        ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();
        getViewModel().getBlockHeightOrRate(value.valAddr, walletInfo.getAllAddress());
    }

    @Override
    public void onDoposGas(String input, List<String> pIndexNum) {
        ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();
        WalletEntity entity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        DposRedeemParam param = new DposRedeemParam();
        param.delegator_address = entity.getAllAddress();
        param.validator_address = value.valAddr;
        param.index_number = pIndexNum;
        param.amount = new DposRedeemParam.Amount(input, mDecimal, getString(R.string.default_token_name));

        getViewModel().dposGas(RPCVoteParam.TYPE_MSGUNDELEGATE, param, input, entity);
    }

    @Override
    public void onGatewayNumberCount(String amount) {
        ValidatorDetailNew value = getViewModel().mDetailLiveData.getValue();
        String amountNew = new BigDecimal(amount).multiply(BigDecimal.valueOf(Math.pow(10, mDecimal))).toPlainString();

        String tokenName = getString(R.string.default_token_name);
        getViewModel().getGatewayNumberCount(value.valAddr, amountNew + tokenName);
    }
}
