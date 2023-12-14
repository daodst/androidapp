package com.app.me.destory;


import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_ENGLISH;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityDestoryBinding;
import com.app.me.computing.CusTextWatcher;
import com.app.me.computing.Spanny;
import com.app.me.destory_group.DestoryGroupActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.util.XPopupUtils;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.my.view.MyAlertDialog;
import common.app.utils.LanguageUtil;
import common.app.utils.SpUtil;


public class DestoryActivity extends BaseActivity<DestoryPledgeVM> {


    private ActivityDestoryBinding mBinding;
    private static final String KEY_ADDRESS = "address";

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, DestoryGroupActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityDestoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        
        Eyes.setTranslucent(this);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.destoryBackNested.getLayoutParams();

        params.topMargin = Eyes.getStatusBarHeight(this);

        mBinding.getRoot().post(() -> {
            int height = mBinding.destoryBackTopParent.getHeight() + Eyes.getStatusBarHeight(this);
            ViewGroup.LayoutParams layoutParams = mBinding.destoryBackTopBg.getLayoutParams();
            layoutParams.height = height;
        });

        super.onCreate(savedInstanceState);
    }

    private String mAddress, mNickName;
    private WalletEntity mSelecteWallet;

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        mAddress = getIntent().getStringExtra(KEY_ADDRESS);

        if (!TextUtils.isEmpty(mAddress)) {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddress, WalletUtil.MCC_COIN);
        } else {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        }

        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_info);
            finish();
            return;
        }
        mAddress = mSelecteWallet.getAllAddress();

        mBinding.dstDestoryWalletAddress.setOnClickListener(v -> {
            ChooseWalletDialog.showDialog(this, WalletUtil.MCC_COIN, ((address1, walletType) -> {
                
                mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo();
                mAddress = mSelecteWallet.getAllAddress();
                getData();
            }));
        });


        mBinding.destoryCoinName.setText(BuildConfig.EVMOS_FAKE_UNINT);
        mBinding.destoryBack.setOnClickListener(v -> {
            finish();
        });
        mBinding.dstDestoryGet.setOnClickListener(view1 -> {
            Uri httpUri = Uri.parse("http://water-tap.daodst.com/");
            startActivity(new Intent(Intent.ACTION_VIEW).setData(httpUri));
        });

        mBinding.dstDestoryAddressTips.setText(BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_address));
        mBinding.dstDestoryTips.setText(String.format(getString(R.string.dst_destory_tips), "--", "--"));

        ActivityResultLauncher<Intent> addressBookLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            int resultCode = result.getResultCode();
            if (resultCode == RESULT_OK && null != data) {
                String toAddress = data.getStringExtra("toAddress");
                if (!TextUtils.isEmpty(toAddress)) {
                    mBinding.dstDestoryHashAddress.setText(toAddress);
                }
            }
        });

        
        mBinding.dstDestoryHashAddressSelect.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressBookActivity.class);
            intent.putExtra("type", 1);
            addressBookLauncher.launch(intent);
        });


        
        getViewModel().mConfigLD.observe(this, this::showInfo);
        
        getViewModel().mLiveData.observe(this, bean -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            String inputNum = mBinding.dstDestoryNum.getText().toString().trim();
            String phonePrex = mBinding.dstDestoryPhone.getText().toString().trim();
            phonePrex = phonePrex.replaceAll("XXXX", "");
            String pledgeToAddress = mBinding.dstDestoryHashAddress.getText().toString().trim();

            if (TextUtils.isEmpty(inputNum)) {
                inputNum = "0";
            }
            try {
                BigDecimal decimal = new BigDecimal(inputNum);
                BigDecimal gas = new BigDecimal(bean.gas.getShowFeeAdd());

                if (decimal.add(gas).compareTo(new BigDecimal(value.tokenBalance)) > 0) {
                    showToast(R.string.balance_no_enaful);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String finalInputNum = inputNum;
            String finalPhonePrex = phonePrex;
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(inputNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(value.pledgeAddress).type(WalletUtil.MCC_COIN).orderDesc(getString(R.string.btn_mint_tips))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doPledge(bean, mAddress, finalInputNum, finalPhonePrex, value, mSelecteWallet, pwd, pledgeToAddress);
                    }).show();

        });
        
        getViewModel().observe(getViewModel().mPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });
        
        getViewModel().observe(getViewModel().mResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                finish();
            } else {
                showToast(bean.info);
            }
        });


        getData();
    }

    private void getData() {

        mNickName = mSelecteWallet.getName();
        String local = LanguageUtil.getNowLocalStr(this);
        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            mBinding.dstDestoryTips.setText(String.format(getString(R.string.dst_destory_tips), "--", "--"));

            mBinding.dstDestoryTips2.setText(new Spanny("A TOTAL OF ").append(" -- ", new ForegroundColorSpan(Color.parseColor("#FFFF00"))).append(" CAN BE DESTROYED USING A CONFIDENTIAL COMMUNICATION SYSTEM."));
            mBinding.dstDestoryWalletName.setText(mNickName + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_wallet));
        } else {

            mBinding.dstDestoryTips2.setText(new Spanny(" ").append("--", new ForegroundColorSpan(Color.parseColor("#FFFF00"))).append(" "));

            mBinding.dstDestoryWalletName.setText(mNickName + "  " + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_wallet));
        }

        mBinding.dstDestoryBalance.setText(new Spanny(getString(R.string.surplus)).append(" 0", new ForegroundColorSpan(Color.parseColor("#FF9900"))));

        mBinding.dstDestoryNumPledge.setText(new Spanny(getString(R.string.quantity2)).append(" 0", new ForegroundColorSpan(Color.parseColor("#FF9900"))));
        mBinding.dstDestoryTips3.setText(String.format(getString(R.string.dst_destory_tips3), "--", "--"));

        
        String noSegment = SpUtil.getNodeNoSegm();
        getViewModel().getConfig(mAddress, noSegment, "dst_burn", LanguageUtil.getNowLocalStr(this));
    }

    public void showInfo(EvmosPledgeConfigBean configBean) {
        if (null == configBean) {
            return;
        }

        
        mBinding.dstDestoryNum.setText("");
        String local = LanguageUtil.getNowLocalStr(this);

        String comsumeTokenName = configBean.tokenNameDestory.toUpperCase();
        String gainTokenName = configBean.tokenName.toUpperCase();
        mBinding.dstDestoryTips.setText(String.format(getString(R.string.dst_destory_tips), comsumeTokenName, gainTokenName));
        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            mBinding.dstDestoryTips.setText(String.format(getString(R.string.dst_destory_tips), comsumeTokenName, gainTokenName));

            mBinding.dstDestoryTips2.setText(new Spanny("A TOTAL OF ").append(configBean.minPledgeNumShow + comsumeTokenName, new ForegroundColorSpan(Color.parseColor("#FFFF00"))).append(" CAN BE DESTROYED USING A CONFIDENTIAL COMMUNICATION SYSTEM."));
            mBinding.dstDestoryWalletName.setText(mNickName + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_wallet));
        } else {

            mBinding.dstDestoryTips2.setText(new Spanny(" ").append(configBean.minPledgeNumShow + comsumeTokenName, new ForegroundColorSpan(Color.parseColor("#FFFF00"))).append(" "));

            mBinding.dstDestoryWalletName.setText(mNickName + "  " + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_wallet));
        }

        mBinding.dstDestoryBalance.setText(new Spanny(getString(R.string.surplus)).append(configBean.tokenBalance + " " + comsumeTokenName, new ForegroundColorSpan(Color.parseColor("#FF9900"))));

        mBinding.dstDestoryNumPledge.setText(new Spanny(getString(R.string.quantity2)).append(" 0 " + comsumeTokenName, new ForegroundColorSpan(Color.parseColor("#FF9900"))));

        mBinding.dstDestoryExchange.setText("1 " + comsumeTokenName + " : " + configBean.ratio + " " + gainTokenName);

        mBinding.dstDestoryTips3.setText(String.format(getString(R.string.dst_destory_tips3), comsumeTokenName, gainTokenName));

        mBinding.dstDestoryAddress.setText(configBean.burn_address);

        mBinding.dstDestoryWalletAddress.setText(mSelecteWallet.getAllAddress());
        
        mBinding.dstDestoryHashAddress.setText(mSelecteWallet.getAllAddress());
        
        mBinding.dstDestoryPhone.setText(configBean.phoneStartList.get(0) + "XXXX");

        mBinding.dstDestoryTipsButtom.setText(Html.fromHtml(configBean.dst_burn));


        
        mBinding.dstDestoryPhoneSelect.setOnClickListener(view1 -> {
            if (!configBean.isSuccess) {
                return;
            }
            List<String> phoneStarArrays = configBean.phoneStartList;
            if (null != phoneStarArrays && phoneStarArrays.size() > 0) {
                List<String> list = new ArrayList<>();
                for (String number : phoneStarArrays) {
                    list.add(number + "XXXX");
                }
                new XPopup.Builder(this).maxHeight((int) (XPopupUtils.getScreenHeight(this) * 0.5)).asBottomList("", list.toArray(new String[]{}), (position, text) -> {
                    mBinding.dstDestoryPhone.setText(text);
                }).show();
            }
        });


        
        mBinding.dstDestoryNum.addTextChangedListener(new CusTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                
                if (TextUtils.isEmpty(trim)) {
                    trim = "0";
                    mBinding.dstDestoryHashGet.setText("0");
                } else {
                    try {
                        BigDecimal decimal = new BigDecimal(configBean.ratio);
                        BigDecimal input = new BigDecimal(trim);
                        mBinding.dstDestoryHashGet.setText(decimal.multiply(input).stripTrailingZeros().toPlainString());
                    } catch (Exception e) {
                        mBinding.dstDestoryHashGet.setText("");
                    }
                }
                mBinding.dstDestoryNumPledge.setText(new Spanny(getString(R.string.quantity2)).append(" " + trim + " " + comsumeTokenName, new ForegroundColorSpan(Color.parseColor("#FF9900"))));

                
                if (!TextUtils.isEmpty(trim) && !configBean.isHasRegisted) {
                    BigDecimal minInutBigDecimal = new BigDecimal(configBean.minPledgeNum);
                    BigDecimal inputNumBigDecimal = new BigDecimal(trim);
                    if (minInutBigDecimal.compareTo(inputNumBigDecimal) > 0) {
                        
                        mBinding.dstDestoryPhoneSelect.setVisibility(View.GONE);
                        mBinding.dstDestoryPhoneSelectTips.setVisibility(View.GONE);
                    } else {
                        
                        mBinding.dstDestoryPhoneSelect.setVisibility(View.VISIBLE);
                        mBinding.dstDestoryPhoneSelectTips.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        
        if (!configBean.isHasRegisted) {
            if (new BigDecimal("0").equals(new BigDecimal(configBean.minPledgeNum))) {

                MyAlertDialog alertDialog = new MyAlertDialog(this, String.format(getString(R.string.min_pledge_destory_tips2), configBean.minPledgeNumShow));
                alertDialog.setonclick(new MyAlertDialog.Onclick() {
                    @Override
                    public void Yes() {
                        alertDialog.dismiss();
                    }

                    @Override
                    public void No() {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setNoBtnGone();
                alertDialog.show();
                mBinding.dstDestoryPhoneSelect.setVisibility(View.VISIBLE);
                mBinding.dstDestoryPhoneSelectTips.setVisibility(View.VISIBLE);
            } else {
                mBinding.dstDestoryPhoneSelect.setVisibility(View.GONE);
                mBinding.dstDestoryPhoneSelectTips.setVisibility(View.GONE);
            }
            mBinding.dstDestoryNum.setHint(String.format(getString(R.string.min_pledge_num3), configBean.minPledgeNum, configBean.tokenNameDestory.toUpperCase()));
        } else {
            mBinding.dstDestoryNum.setHint(getString(R.string.please_input_min_num2));
            mBinding.dstDestoryPhoneSelect.setVisibility(View.GONE);
            mBinding.dstDestoryPhoneSelectTips.setVisibility(View.GONE);
        }
        
        mBinding.dstDestoryBt.setOnClickListener(v -> {
            if (!configBean.isSuccess) {
                return;
            }
            
            String inputNum = mBinding.dstDestoryNum.getText().toString().trim();

            boolean unRegisted = !configBean.isHasRegisted;
            boolean unPay = true;
            try {
                unPay = new BigDecimal("0").equals(new BigDecimal(configBean.minPledgeNum));
            } catch (Exception e) {
            }

            if (TextUtils.isEmpty(inputNum)) {
                if (unRegisted && unPay) {
                    
                    inputNum = "0";
                } else {
                    showToast(R.string.please_input_min_num);
                    return;
                }
            }

            if (unRegisted && unPay) {
                
            } else {
                
                BigDecimal min_burn_amount = new BigDecimal(configBean.min_burn_amount);
                BigDecimal inputNumBigDecimal = new BigDecimal(inputNum);
                if (min_burn_amount.compareTo(inputNumBigDecimal) > 0) {
                    showToast(String.format(getString(R.string.min_burn_amount), configBean.min_burn_amount + " " + configBean.tokenNameDestory.toUpperCase()));
                    return;
                }
            }

            boolean doReg = !configBean.isHasRegisted;
            if (!configBean.isHasRegisted) {
                
                BigDecimal minInutBigDecimal = new BigDecimal(configBean.minPledgeNum);
                BigDecimal inputNumBigDecimal = new BigDecimal(inputNum);
                if (minInutBigDecimal.compareTo(inputNumBigDecimal) > 0) {
                    
                    doReg = false;
                }
            }
            
            String pledgeToAddress = mBinding.dstDestoryHashAddress.getText().toString().trim();
            if (TextUtils.isEmpty(pledgeToAddress)) {
                showToast(R.string.please_select_dst_destory_hash_address);
                return;
            }
            
            String phonePrex = mBinding.dstDestoryPhone.getText().toString().trim();
            if (doReg) {
                
                if (TextUtils.isEmpty(phonePrex)) {
                    showToast(R.string.please_selecte_phone_prefix);
                    return;
                }
                phonePrex = phonePrex.replaceAll("XXXX", "");
            }
            
            if (!TextUtils.isEmpty(configBean.tokenBalance) && new BigDecimal(configBean.tokenBalance).compareTo(new BigDecimal(inputNum)) < 0) {
                showToast(R.string.balance_no_enaful);
                return;
            }
            
            getViewModel().doPledggeRegistGas(doReg, mAddress, inputNum, phonePrex, configBean, mSelecteWallet, "", pledgeToAddress);
        });
    }
}
