package com.app.home.ui.vote.create;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.R;
import com.app.databinding.ActivityCreateVoteBinding;
import com.app.databinding.CreateVoteParamsBinding;
import com.app.databinding.CreateVotePayBinding;
import com.app.databinding.CreateVoteUpdateBinding;
import com.app.pojo.VoteParamsBean;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BlockDepositBean;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.RxBus;
import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;
import common.app.utils.AllUtils;


public class CreateVoteActivity extends BaseActivity<CreateVoteVM> {

    private ActivityCreateVoteBinding mViewB;
    private CreateVoteParamsBinding mParamsViewB;
    private CreateVotePayBinding mPayViewB;
    private CreateVoteUpdateBinding mUpdateViewB;
    private WalletEntity mSelecteWallet;

    
    public static final int TYPE_PARAMS = 0;
    public static final int TYPE_PAY = 1;
    public static final int TYPE_UPGRADE = 2;
    private int mType = TYPE_PARAMS;

    private BlockDepositBean mDepositBean;
    private String mBalance;

    private ParamsItemAdapter mParamsAdapter;

    private static final String KEY_ADDR = "address";

    private static final String KEY_TYPE = "type";
    private static final String KEY_PARMAS_LIST = "paramsList";

    
    private static final String KEY_PAY_AMOUNT = "payAmount";
    private static final String KEY_PAY_RECEIVE_ADDR = "payReceiveAddr";
    private static final String KEY_PAY_COIN_NAME = "payCoinName";

    
    private static final String KEY_UP_TITLE = "upgradeTitle"; 
    private static final String KEY_UP_CONTENT = "upgradeContent";
    private static final String KEY_UP_HEIGHT = "upgradeHeight";

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, CreateVoteActivity.class);
        intent.putExtra(KEY_ADDR, address);
        return intent;
    }


    
    public static Intent getParamsIntent(Context from, ArrayList<VoteParamsBean> paramsList) {
        Intent intent = new Intent(from, CreateVoteActivity.class);
        intent.putExtra(KEY_TYPE, TYPE_PARAMS);
        intent.putParcelableArrayListExtra(KEY_PARMAS_LIST, paramsList);
        return intent;
    }

    
    public static Intent getPayIntent(Context from, String amount, String receiveAddr, String coinName) {
        Intent intent = new Intent(from, CreateVoteActivity.class);
        intent.putExtra(KEY_TYPE, TYPE_PAY);
        intent.putExtra(KEY_PAY_AMOUNT, amount);
        intent.putExtra(KEY_PAY_RECEIVE_ADDR, receiveAddr);
        intent.putExtra(KEY_PAY_COIN_NAME, coinName);
        return intent;
    }

    
    public static Intent getUpIntent(Context from, String upgradeTitle, String upgradeContentJson, String upgradeHeight) {
        Intent intent = new Intent(from, CreateVoteActivity.class);
        intent.putExtra(KEY_TYPE, TYPE_UPGRADE);
        intent.putExtra(KEY_UP_TITLE, upgradeTitle);
        intent.putExtra(KEY_UP_CONTENT, upgradeContentJson);
        intent.putExtra(KEY_UP_HEIGHT, upgradeHeight);
        return intent;
    }

    @Override
    public void initParam() {
        String address = getIntent().getStringExtra(KEY_ADDR);
        if (!TextUtils.isEmpty(address)) {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(address, WalletUtil.MCC_COIN);
        } else {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        }

        if (mSelecteWallet == null) {
            setForceIntercept(true);
            showToast(R.string.no_found_wallet_error);
            finish();
            return;
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityCreateVoteBinding.inflate(getLayoutInflater());
        mParamsViewB = CreateVoteParamsBinding.bind(mViewB.layoutParams);
        mPayViewB = CreateVotePayBinding.bind(mViewB.layoutPay);
        mUpdateViewB = CreateVoteUpdateBinding.bind(mViewB.layoutUpdate);
        return mViewB.getRoot();
    }


    @Override
    public void initView(@Nullable View view) {
        
        mViewB.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }
            @Override
            public void rightClick() {}
        });

        
        mViewB.voteTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                
                mType = i;
                initTypeShow();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        
        mViewB.submitBtn.setOnClickListener(view1 -> {
            submit(true, "");
        });

        
        mViewB.addrTv.setOnClickListener(view1 -> {
            ChooseWalletDialog.showDialog(CreateVoteActivity.this, WalletUtil.MCC_COIN, (address, walletType) -> {
                mSelecteWallet = WalletDBUtil.getInstent(CreateVoteActivity.this).getWalletInfoByAddress(address, walletType);
                mViewB.addrTv.setText(mSelecteWallet.getAllAddress());
            });
        });

        
        mViewB.addrTv.setText(mSelecteWallet.getAllAddress());

        
        mParamsAdapter = new ParamsItemAdapter(size -> {
            if (size > 0) {
                mParamsViewB.emptyParamTv.setVisibility(View.GONE);
            } else {
                mParamsViewB.emptyParamTv.setVisibility(View.VISIBLE);
            }
        });
        mParamsViewB.paramRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mParamsViewB.paramRecyclerview.setAdapter(mParamsAdapter);
        
        mParamsViewB.addParamsBtn.setOnClickListener(view1 -> {
            String subSpace = mParamsViewB.subsapceEdit.getText().toString().trim();
            String key = mParamsViewB.keyEdit.getText().toString().trim();
            String value = mParamsViewB.valueEdit.getText().toString().trim();
            if (TextUtils.isEmpty(subSpace) || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                showToast(R.string.jinrongjia_info_error);
                return;
            }
            mParamsAdapter.add(new VoteParamsBean(subSpace, key, value));
            mParamsViewB.subsapceEdit.setText("");
            mParamsViewB.keyEdit.setText("");
            mParamsViewB.valueEdit.setText("");
        });

        
        initVoteData();
        initTypeShow();
    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mBlockInfLD, blockDepositBean -> {
            mDepositBean = blockDepositBean;
            if (null != blockDepositBean) {
                String minNum = blockDepositBean.getMinAmount();
                String coinName = blockDepositBean.getCoinName();
                if (!TextUtils.isEmpty(minNum) && !TextUtils.isEmpty(coinName)) {
                    mViewB.moneyEdit.setHint(getString(R.string.min_vote_money_hint)+minNum+coinName.toUpperCase());
                }
            }
        });

        
        getViewModel().observe(getViewModel().mBalanceLD, balance->{
            mBalance = balance;
        });

        
        getViewModel().observe(getViewModel().mResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(com.wallet.ctc.R.string.operate_success_and_wait);
                RxBus.getInstance().post(new RxNotice(RxNotice.MSG_SUBMIT_VOTE));
                finish();
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });


        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            String toAddress = mType == TYPE_PAY ? mPayViewB.payReceiveAddrEdit.getText().toString() : "";
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(mViewB.moneyEdit.getText().toString())
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(toAddress)
                    .type(mSelecteWallet.getType())
                    .orderDesc(getString(R.string.create_vote_title))
                    
                    .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                    
                    .goTransferListener(pwd -> {
                        
                        submit(false, pwd);
                    }).show();
        });

        getDatas();
    }

    
    private void getDatas() {
        getViewModel().getDatas(mSelecteWallet.getAllAddress());
    }

    
    private void initVoteData() {
        mType = getIntent().getIntExtra(KEY_TYPE, TYPE_PARAMS);
        if (mType == TYPE_PARAMS) {
            
            List<VoteParamsBean> list = getIntent().getParcelableArrayListExtra(KEY_PARMAS_LIST);
            mParamsAdapter.cleanAndAddAll(list);
        } else if(mType == TYPE_PAY) {
            
            String payAmount = getIntent().getStringExtra(KEY_PAY_AMOUNT);
            String receiveAddr = getIntent().getStringExtra(KEY_PAY_RECEIVE_ADDR);
            String payCoinName = getIntent().getStringExtra(KEY_PAY_COIN_NAME);
            mPayViewB.payNumEdit.setText(payAmount);
            mPayViewB.payReceiveAddrEdit.setText(receiveAddr);
            String[] coins = getResources().getStringArray(R.array.mcc_coins);
            int index = 0;
            for (int i=0; i<coins.length; i++) {
                if (coins[i].equalsIgnoreCase(payCoinName)) {
                    index = i;
                    break;
                }
            }
            mPayViewB.payCoinSpinner.setSelection(index);
        } else if(mType == TYPE_UPGRADE) {
            
            String upgradeTitle = getIntent().getStringExtra(KEY_UP_TITLE);
            String upgradeContent = getIntent().getStringExtra(KEY_UP_CONTENT);
            String upgradeHeight = getIntent().getStringExtra(KEY_UP_HEIGHT);
            mUpdateViewB.updateNameEdit.setText(upgradeTitle);
            mUpdateViewB.updateInfoEdit.setText(upgradeContent);
            mUpdateViewB.updateHeightEdit.setText(upgradeHeight);
        }
    }

    
    private void initTypeShow() {
        if (mType == TYPE_PARAMS) {
            
            voteContentShow(View.VISIBLE, View.GONE, View.GONE);
        } else if(mType == TYPE_PAY) {
            
            voteContentShow(View.GONE, View.VISIBLE, View.GONE);
        } else if(mType == TYPE_UPGRADE) {
            
            voteContentShow(View.GONE, View.GONE, View.VISIBLE);
        }
    }

    
    private void submit(boolean gasAlert, String pwd) {
        
        String address = mViewB.addrTv.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            showToast(R.string.vote_creater_hint);
            return;
        }

        
        String title = mViewB.titleEdit.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            showToast(R.string.vote_biaoti_hint);
            return;
        }

        
        String desc = mViewB.descEdit.getText().toString().trim();
        if (TextUtils.isEmpty(desc)) {
            showToast(R.string.vote_desc_hint);
            return;
        }

        
        if (null == mDepositBean || TextUtils.isEmpty(mBalance)) {
            showToast(R.string.update_some_data_please);
            getDatas();
            return;
        }

        
        List<VoteParamsBean> params = null;
        if (mType == TYPE_PARAMS) {
            params = mParamsAdapter.getDatas();
            if (null == params || params.size() == 0) {
                showToast(R.string.please_add_vote_params);
                return;
            }
        }

        
        String payReceiveAddr = "", bigPayNum = "";
        if(mType == TYPE_PAY) {
            
            payReceiveAddr = mPayViewB.payReceiveAddrEdit.getText().toString().trim();
            if (TextUtils.isEmpty(payReceiveAddr)) {
                showToast(R.string.vote_pay_receive_addr_hint);
                return;
            }
            if (!payReceiveAddr.startsWith(BuildConfig.ENABLE_MCC_ADDRESS) || payReceiveAddr.length() < 15) {
                showToast(R.string.vote_receive_addr_error);
                return;
            }

            String payNum = mPayViewB.payNumEdit.getText().toString().trim();
            String payCoin = mPayViewB.payCoinSpinner.getSelectedItem().toString();
            if (!TextUtils.isEmpty(payNum)) {
                
                bigPayNum = AllUtils.getBigDecimalValue(payNum, getDecimal(payCoin))+payCoin;
            }
            if (TextUtils.isEmpty(bigPayNum)) {
                showToast(R.string.vote_pay_num_hint);
                return;
            }
        }

        
        String upgradeName = "",upgradeInfo = "",upgradeHeight = "";
        if(mType == TYPE_UPGRADE) {
            upgradeName = mUpdateViewB.updateNameEdit.getText().toString().trim();
            if (TextUtils.isEmpty(upgradeName)) {
                showToast(R.string.vote_upgrade_name_hint);
                return;
            }
            upgradeInfo = mUpdateViewB.updateInfoEdit.getText().toString().trim();
            if (TextUtils.isEmpty(upgradeInfo)) {
                showToast(R.string.vote_upgrade_content_hint);
                return;
            }
            
            
            
            
            
            try {
                Map<String, Object> infoData = new Gson().fromJson(upgradeInfo, Map.class);
                if (null == infoData || infoData.isEmpty()) {
                    showToast(R.string.vote_upgrade_info_error);
                    return;
                }
                if(!infoData.containsKey("gateway") && !infoData.containsKey("blockchain") && !infoData.containsKey("app")) {
                    showToast(R.string.vote_upgrade_info_error);
                    return;
                }
            } catch (JsonSyntaxException e){
                showToast(R.string.vote_upgrade_info_error);
                return;
            }

            upgradeHeight = mUpdateViewB.updateHeightEdit.getText().toString().trim();
            if (TextUtils.isEmpty(upgradeHeight)) {
                showToast(R.string.vote_pay_height_hint);
                return;
            }
            String nowBlockHeight = mDepositBean.getHeight();
            
            if (!TextUtils.isEmpty(nowBlockHeight) && upgradeHeight.compareTo(nowBlockHeight) <= 0) {
                showToast(getApplication().getString(R.string.vote_upgrade_height_error));
                return;
            }
        }


        
        String tiAnMoney = mViewB.moneyEdit.getText().toString().trim();
        if (TextUtils.isEmpty(tiAnMoney)) {
            showToast(R.string.please_input_tian_money);
            return;
        }

        String minMoney = mDepositBean.getMinAmount();
        String moneyCoinName = mDepositBean.getCoinName();
        if (!TextUtils.isEmpty(minMoney)) {
            if (new BigDecimal(tiAnMoney).compareTo(new BigDecimal(minMoney)) < 0) {
                String errorInfo = getApplication().getString(R.string.vote_money_title)+
                        getApplication().getString(R.string.min_vote_money_hint)+minMoney+moneyCoinName;
                showToast(errorInfo);
                return;
            }
        }
        
        if (new BigDecimal(tiAnMoney).compareTo(new BigDecimal(mBalance)) > 0) {
            showToast(R.string.insufficient_balance);
            return;
        }



        
        String bigMoneyCoin = AllUtils.getBigDecimalValue(tiAnMoney, getDecimal(moneyCoinName))+moneyCoinName;

        if (gasAlert) {
            getViewModel().showGasAlert(mType, address, title, desc, bigMoneyCoin,
                    params,             
                    payReceiveAddr,     
                    bigPayNum,             
                    upgradeName,        
                    upgradeInfo,        
                    upgradeHeight       
            );
        } else {
            
            getViewModel().submit(mType, address, title, desc, bigMoneyCoin,
                    params,             
                    payReceiveAddr,     
                    bigPayNum,             
                    upgradeName,        
                    upgradeInfo,        
                    upgradeHeight,       
                    mSelecteWallet,
                    pwd
            );
        }

    }

    
    private int getDecimal(String coinName) {
        AssertBean assertBean = WalletDBUtil.getInstent(this).getWalletAssets(WalletUtil.MCC_COIN, coinName);
        if (null != assertBean) {
            return assertBean.getDecimal();
        } else {
            return 18;
        }
    }

    
    private void voteContentShow(int paramsVisibility, int payVisibility, int upgradVisibility ) {
        mViewB.layoutParams.setVisibility(paramsVisibility);
        mViewB.layoutPay.setVisibility(payVisibility);
        mViewB.layoutUpdate.setVisibility(upgradVisibility);
    }
}
