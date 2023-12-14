package com.app.did_guid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ActivityBuyDidGuidBinding;
import com.app.databinding.DialogBscBuyDstBinding;
import com.app.me.computing.ComputingActivity;
import com.app.me.destory.DestoryActivity;
import com.app.pojo.BuyDidConfigBean;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.Constants;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.base.listener.DefaultTextWatcher;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.ContentDialogUtil;
import common.app.utils.SpannableUtils;
import im.vector.app.provide.ChatStatusProvide;

public class BuyDidGuidActivity extends BaseActivity<BuyDidGuidVM> {

    private ActivityBuyDidGuidBinding mViewB;
    private WalletEntity mSelecteWallet;
    private BuyDidConfigBean mConfig;
    private static final String KEY_ADDR = "address";

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, BuyDidGuidActivity.class);
        intent.putExtra(KEY_ADDR, address);
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
        mViewB = ActivityBuyDidGuidBinding.inflate(getLayoutInflater());
        return mViewB.getRoot();
    }


    @Override
    public void initView(@Nullable View view) {

        
        mViewB.backImg.setOnClickListener(view1 -> {
            finish();
        });

        
        mViewB.buyBnbBtn.setOnClickListener(view1 -> {
            
            DappWebViewActivity.startDappWebViewActivity(BuyDidGuidActivity.this, Constants.PANCAKESWAP_URL_USDT_DST);
        });

        
        mViewB.buyDstBtn.setOnClickListener(view1 -> {
            showBuyDstBottomDialog();
        });

        
        mViewB.destroyWayTv.setOnClickListener(view1 -> {
            showDestroyGuidDialog();
        });

        
        mViewB.destroyDstBtn.setOnClickListener(view1 -> {
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(BuyDidGuidActivity.this));
            startActivity(DestoryActivity.getIntent(BuyDidGuidActivity.this, address));
        });

        
        mViewB.suanliManagerBtn.setOnClickListener(view1 -> {
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(BuyDidGuidActivity.this));
            startActivity(ComputingActivity.getIntent(BuyDidGuidActivity.this, address));
        });
    }

    @Override
    public void initData() {

        
        getViewModel().observe(getViewModel().mConfigLD, buyDidConfigBean -> {
            showData(buyDidConfigBean);
        });

        
        getViewModel().observe(getViewModel().mBuyPayAmountLD, amount->{
            setPayNum(amount);
        });

        
        getViewModel().observe(getViewModel().mShowApproveDialogLD, evmosSeqGasBean -> {
            
            MyAlertDialog dialog = new MyAlertDialog(BuyDidGuidActivity.this, getString(com.wallet.ctc.R.string.first_pay_approve_tip));
            dialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    dialog.dismiss();
                    showGasAndTranscation(evmosSeqGasBean, getString(com.wallet.ctc.R.string.to_approve_do), true);
                }
                @Override
                public void No() {
                    dialog.dismiss();
                }
            });
            dialog.setNoText(getString(com.wallet.ctc.R.string.cancel));
            dialog.setYesText(getString(com.wallet.ctc.R.string.ex_shouquan));
            dialog.show();
        });

        
        getViewModel().observe(getViewModel().mApproveResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(com.wallet.ctc.R.string.approve_success_continue);
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            showGasAndTranscation(evmosSeqGasBean, getString(com.wallet.ctc.R.string.confirm_yingshe), false);
        });

        
        getViewModel().observe(getViewModel().mResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(com.wallet.ctc.R.string.operate_success_and_wait);
                finish();
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });

        getData();
    }


    
    private void getData() {
        
        getViewModel().getData(mSelecteWallet.getAllAddress());
    }


    
    private void showData(BuyDidConfigBean config) {
        if (null == config) {
            return;
        }
        mConfig = config;
        String minUsdtBurnAmount = config.getMinUsdtBurnAmount();
        
        mViewB.bnbSubTitleTv.setText(String.format(getString(R.string.use_usdt_buy_did), minUsdtBurnAmount));
        
        mViewB.bnbBalanceTv.setText(config.bnbBalance);
        
        mViewB.usdtBalanceTv.setText(config.bscUsdtBalance);
        mViewB.needUsdtNumTv.setText("/"+minUsdtBurnAmount);
        if (config.isUsdtEnough()) {
            
            mViewB.bnbAlertImg.setVisibility(View.INVISIBLE);
        } else {
            mViewB.bnbAlertImg.setVisibility(View.VISIBLE);
        }

        
        mViewB.dstPriceTv.setText(String.format(getString(R.string.buy_dst_price_title), config.getBscDstUsdtPrice()));
        
        mViewB.dstBalanceTv.setText(config.dstBalance);
        if (config.isDstEnough()) {
            mViewB.dstAlertImg.setVisibility(View.INVISIBLE);
        } else {
            mViewB.dstAlertImg.setVisibility(View.VISIBLE);
        }
        
        mViewB.stillDstNumTv.setText(config.getSubDstNum());

        
        
        if (!TextUtils.isEmpty(config.minBurnAmount)) {
            String minAmountStr = config.minBurnAmount+getString(R.string.only_unint);
            String titleDesc = String.format(getString(R.string.need_min_dst_title), minAmountStr);
            Spannable spannableString = SpannableUtils.colorizeMatchingText(
                    SpannableUtils.styleMatchingText(new SpannableString(titleDesc), minAmountStr, Typeface.BOLD),
                    minAmountStr,
                    ContextCompat.getColor(BuyDidGuidActivity.this, R.color.default_text_color));
            mViewB.needMinDstTv.setText(spannableString);
        }

        
        mViewB.hasDestroyNumTv.setText(config.hasBurnAmount);
        
        mViewB.waitDestroyNumTv.setText(config.getSubBurnNum());
        if(config.isBurnEnough()) {
            mViewB.destroyAlertImg.setVisibility(View.INVISIBLE);
        } else {
            mViewB.destroyAlertImg.setVisibility(View.VISIBLE);
        }


    }


    
    private String needPayNum;
    private void resetPayNum() {
        if (null == mDialogBscBuyDstBinding) {
            return;
        }
        needPayNum = "";
        String amount = mDialogBscBuyDstBinding.dstNumEdit.getText().toString().trim();
        String selectCoin = mDialogBscBuyDstBinding.coinSpinner.getSelectedItem().toString();
        String payAmount = "";
        if (TextUtils.isEmpty(amount)) {
            payAmount = "0.00"+selectCoin;
        } else {
            payAmount = "--"+selectCoin;
        }
        String tip = String.format(getString(R.string.pay_bsc_num_tip), payAmount);
        Spannable spl = SpannableUtils.colorizeMatchingText(new SpannableString(tip), payAmount, ContextCompat.getColor(BuyDidGuidActivity.this, R.color.default_theme_color));
        mDialogBscBuyDstBinding.payNumTv.setText(SpannableUtils.styleMatchingText(spl, payAmount, Typeface.BOLD_ITALIC));
        mDialogBscBuyDstBinding.buyBtn.setEnabled(false);
    }

    
    private void setPayNum(String useAmount) {
        if (null == mDialogBscBuyDstBinding || TextUtils.isEmpty(useAmount)) {
            return;
        }
        needPayNum = useAmount;
        String selectCoin = mDialogBscBuyDstBinding.coinSpinner.getSelectedItem().toString();
        String payAmount = useAmount+selectCoin;
        String tip = String.format(getString(R.string.pay_bsc_num_tip), payAmount);
        Spannable spl = SpannableUtils.colorizeMatchingText(new SpannableString(tip), payAmount, ContextCompat.getColor(BuyDidGuidActivity.this, R.color.default_theme_color));
        mDialogBscBuyDstBinding.payNumTv.setText(SpannableUtils.styleMatchingText(spl, payAmount, Typeface.BOLD_ITALIC));
        if (!TextUtils.isEmpty(needPayNum)) {
            mDialogBscBuyDstBinding.buyBtn.setEnabled(true);
        }
    }

    
    private void calculatePayNum() {
        if (null == mDialogBscBuyDstBinding) {
            return;
        }
        String amount = mDialogBscBuyDstBinding.dstNumEdit.getText().toString().trim();
        String selectCoin = mDialogBscBuyDstBinding.coinSpinner.getSelectedItem().toString();
        if (TextUtils.isEmpty(amount)) {
            return;
        }
        BigDecimal num = null;
        try {
            num = new BigDecimal(amount);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        if (null == num || num.compareTo(new BigDecimal(0)) <= 0) {
            return;
        }
        getViewModel().calculateBuyPayAmount(amount, selectCoin);
    }

    
    DialogBscBuyDstBinding mDialogBscBuyDstBinding;
    ContentDialogUtil mBuyDstDialog;
    private void showBuyDstBottomDialog() {
        mDialogBscBuyDstBinding = DialogBscBuyDstBinding.inflate(LayoutInflater.from(this));
        ContentDialogUtil dialogUtil = new ContentDialogUtil(this, mDialogBscBuyDstBinding.getRoot(), true);
        mBuyDstDialog = dialogUtil;
        dialogUtil.show();
        dialogUtil.setOnDismissListener(dialogInterface -> {
            mDialogBscBuyDstBinding = null;
            mBuyDstDialog = null;
        });

        mDialogBscBuyDstBinding.coinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetPayNum();
                calculatePayNum();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        mDialogBscBuyDstBinding.dstNumEdit.addTextChangedListener(new DefaultTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                resetPayNum();
                calculatePayNum();
            }
        });

        mDialogBscBuyDstBinding.cancelBtn.setOnClickListener(view -> {
            dialogUtil.dismiss();
        });
        mDialogBscBuyDstBinding.buyBtn.setOnClickListener(view -> {
            String num = mDialogBscBuyDstBinding.dstNumEdit.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                showToast(R.string.buy_dst_num_hint);
                return;
            }
            mBuyToAddr = mDialogBscBuyDstBinding.addrTv.getText().toString().trim();
            mPayCoinSymbol = mDialogBscBuyDstBinding.coinSpinner.getSelectedItem().toString();
            mBuyDstNum = num;
            doChainMap(true, "", false);
        });
    }

    
    private String mBuyToAddr, mPayCoinSymbol, mBuyDstNum;
    private void doChainMap(boolean showGasAlert, String pwd, boolean isApprove) {

        String amount = needPayNum;

        if (TextUtils.isEmpty(amount) || new BigDecimal(amount).compareTo(new BigDecimal(0)) <= 0) {
            showToast(R.string.pay_num_error_hint);
            return;
        }

        String toAddr = mBuyToAddr;
        if (TextUtils.isEmpty(toAddr)) {
            showToast(R.string.get_dst_addr_hint);
            return;
        }
        if (!toAddr.startsWith(BuildConfig.ENABLE_MCC_ADDRESS) || toAddr.length() < 30) {
            
            showToast(com.wallet.ctc.R.string.input_address_error);
            return;
        }

        WalletEntity wallet = WalletDBUtil.getInstent(getApplication()).getWalletInfo(WalletUtil.BNB_COIN);

        
        if (null == wallet) {
            showToast(com.wallet.ctc.R.string.no_found_wallet_error);
            return;
        }

        if (mConfig == null || TextUtils.isEmpty(mConfig.bscUsdtBalance) || TextUtils.isEmpty(mConfig.bnbBalance)) {
            showToast(R.string.query_balance_please_wait);
            getData();
            return;
        }

        
        String useCoin = mPayCoinSymbol;
        String balance = "0.00";
        AssertBean payAsserts = null;
        if ("USDT".equalsIgnoreCase(useCoin)) {
            balance = mConfig.bscUsdtBalance;
            payAsserts = Constants.getUSDTBscAssets();
        } else if("BNB".equalsIgnoreCase(useCoin)) {
            balance = mConfig.bnbBalance;
            payAsserts = Constants.getWbnbBscAssets();
        }

        if (TextUtils.isEmpty(balance) || new BigDecimal(amount).compareTo(new BigDecimal(balance)) > 0) {
            showToast(com.wallet.ctc.R.string.insufficient_balance);
            return;
        }


        String fromAddr = wallet.getAllAddress();

        AssertBean dstAssert = WalletDBUtil.getInstent(this).getWalletAssets(WalletUtil.MCC_COIN, BuildConfig.EVMOS_FAKE_UNINT);
        if (null == dstAssert) {
            showToast(R.string.please_check_wallet_info);
            return;
        }
        String wantBuyDstAmount = mBuyDstNum;
        String dstBigAmount = new BigDecimal(wantBuyDstAmount).multiply(new BigDecimal(Math.pow(10, dstAssert.getDecimal()))).stripTrailingZeros().toPlainString();
        if (showGasAlert) {
            getViewModel().showGasAlert(fromAddr, toAddr, dstBigAmount, payAsserts, amount);
        } else {
            getViewModel().swapBuy(fromAddr, toAddr, dstBigAmount, amount, payAsserts, wallet, pwd, isApprove);
        }

        if (null != mBuyDstDialog) {
            mBuyDstDialog.dismiss();
        }

    }

    
    private void showDestroyGuidDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_destroy_dst_guid, null);
        ContentDialogUtil dialogUtil = new ContentDialogUtil(this, contentView);
        dialogUtil.findViewById(R.id.close_iv).setOnClickListener(view -> {
            dialogUtil.dismiss();
        });
        dialogUtil.show();
    }

    
    private void showGasAndTranscation(EvmosSeqGasBean evmosSeqGasBean, String desc, boolean isApprove) {
        if (null == evmosSeqGasBean) {
            showToast("get gas fail");
            return;
        }
        String toAddress = mBuyToAddr;
        WalletEntity wallet = WalletDBUtil.getInstent(getApplication()).getWalletInfo(WalletUtil.BNB_COIN);
        TransConfirmDialogBuilder.builder(this, wallet).amount(needPayNum)
                
                .fromAddress(wallet.getAllAddress())
                
                .toAddress(toAddress)
                .type(wallet.getType())
                .orderDesc(desc)
                
                .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                .gasCount(evmosSeqGasBean.gasCount+"")
                .gasPrice(evmosSeqGasBean.gasPrice)
                
                .goTransferListener(pwd -> {
                    
                    if (isApprove) {
                        
                        doChainMap(false, pwd, true);
                    } else {
                        
                        doChainMap(false, pwd, false);
                    }

                }).show();
    }







    @Override
    protected void onDestroy() {
        getViewModel().onDestroy();
        super.onDestroy();
    }
}




