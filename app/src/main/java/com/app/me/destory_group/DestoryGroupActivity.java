package com.app.me.destory_group;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ActivityDestoryGroupBinding;
import com.app.me.computing.CusTextWatcher;
import com.app.me.computing.Spanny;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.utils.DisplayUtils;
import common.app.utils.SpannableUtils;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.fenfei.anim.dvm.BuyDvmSuccessDialogFragment;


public class DestoryGroupActivity extends BaseActivity<DestroyGroupVM> {


    private ActivityDestoryGroupBinding mBinding;
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_GROUP_ID = "groupId";

    private String mAddress, mNickName, mGroupId, mCoinName, mShowCoinName;
    private WalletEntity mSelecteWallet;
    private boolean userFreeze = false;
    private EvmosDaoParams mConfigBean;

    
    public static Intent getIntent(Context from, String address, String groupId) {
        Intent intent = new Intent(from, DestoryGroupActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        intent.putExtra(KEY_GROUP_ID, groupId);
        return intent;
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityDestoryGroupBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        
        Eyes.setTranslucent(this);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.destoryBackNested.getLayoutParams();

        params.topMargin = Eyes.getStatusBarHeight(this);

        mBinding.getRoot().post(() -> {
            int height = mBinding.destoryBackTopParent.getHeight() + Eyes.getStatusBarHeight(this);
            ViewGroup.LayoutParams layoutParams = mBinding.destoryBackTopBg.getLayoutParams();
            layoutParams.height = height;
        });

        mGroupId = getIntent().getStringExtra(KEY_GROUP_ID);
        if (TextUtils.isEmpty(mGroupId)) {
            showToast(R.string.no_found_device_group);
            finish();
            return;
        }

        
        mCoinName = getString(R.string.default_token_name2);
        mShowCoinName = mCoinName.toUpperCase();

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

                
                mBinding.dstDestoryWalletName.setText(String.format(getString(R.string.my_wallet_title_txt), mSelecteWallet.getName(), mShowCoinName));
                mBinding.dstDestoryWalletAddress.setText(mSelecteWallet.getAllAddress());

                
                getViewModel().getInfo(mAddress);
            }));
        });

        mBinding.destoryBack.setOnClickListener(v -> {
            finish();
        });
        mBinding.dstDestoryGet.setOnClickListener(view1 -> {
            Uri httpUri = Uri.parse("http://water-tap.daodst.com/");
            startActivity(new Intent(Intent.ACTION_VIEW).setData(httpUri));
        });

        
        mBinding.dstDestoryWalletName.setText(String.format(getString(R.string.my_wallet_title_txt), mSelecteWallet.getName(), mShowCoinName));
        mBinding.dstDestoryWalletAddress.setText(mSelecteWallet.getAllAddress());

        
        mBinding.dstDestoryHashAddress.setText(mAddress);

        
        mBinding.payCoinTv.setText(mShowCoinName);
        mBinding.dstDestoryTab1.setOnClickListener(view1 -> {
            userFreeze = false;
            mBinding.coinBalancetTitleTv.setTextColor(getColor(R.color.white));
            mBinding.balanceTv.setTextColor(getColor(R.color.white));
            mBinding.dstDestoryTab1.setBackgroundResource(R.mipmap.icon_group_info_tab_s1);

            mBinding.freezeTitleTv.setTextColor(getColor(R.color.default_text_color));
            mBinding.freezeNumTv.setTextColor(getColor(R.color.default_text_color));
            mBinding.dstDestoryTab2.setBackgroundResource(R.mipmap.icon_group_info_tab_d2);

            mBinding.payCoinTv.setText(mShowCoinName);
        });

        
        mBinding.dstDestoryTab2.setOnClickListener(view1 -> {
            userFreeze = true;
            mBinding.coinBalancetTitleTv.setTextColor(getColor(R.color.default_text_color));
            mBinding.balanceTv.setTextColor(getColor(R.color.default_text_color));
            mBinding.dstDestoryTab1.setBackgroundResource(R.mipmap.icon_group_info_tab_d1);

            mBinding.freezeTitleTv.setTextColor(getColor(R.color.white));
            mBinding.freezeNumTv.setTextColor(getColor(R.color.white));
            mBinding.dstDestoryTab2.setBackgroundResource(R.mipmap.icon_group_info_tab_s2);

            mBinding.payCoinTv.setText(R.string.pay_freeze_power_title);
        });


        
        
        mBinding.destoryCoinName.setText(mShowCoinName);
        mBinding.dstDestoryAddressTips.setText(mShowCoinName + getString(R.string.dst_destory_address));
        mBinding.priceTitleTv.setText(mShowCoinName+getString(R.string.price));
        mBinding.coinBalancetTitleTv.setText(String.format(getString(R.string.group_coin_balance_title), mShowCoinName));
        mBinding.destoryNumTitleTv.setText(String.format(getString(R.string.destory_num_hint_title), mShowCoinName));
        mBinding.dstDestoryNum.setHint(String.format(getString(R.string.destory_num_input_hint), mShowCoinName));

        mBinding.dstDestoryTipsButtom.setText(String.format(getString(R.string.destory_dst_tips_text), mShowCoinName,
                mShowCoinName, mShowCoinName, mShowCoinName));

        mBinding.dstDestoryTips.setText(String.format(getString(R.string.dst_destory_group_tips), mShowCoinName));
        Spannable dstDestoryTips2 = new SpannableString(getString(R.string.dst_destory_group_tips2));
        dstDestoryTips2 = SpannableUtils.colorizeMatchingText(dstDestoryTips2, "300%", Color.parseColor("#FFFF00"));
        dstDestoryTips2 = SpannableUtils.sizeMatchingText(dstDestoryTips2, "300%", DisplayUtils.sp2px(this, 23));
        mBinding.dstDestoryTips2.setText(dstDestoryTips2);

        Spannable dstDestoryTips3 = new SpannableString(String.format(getString(R.string.dst_destory_group_tips3), mShowCoinName, "--"));
        dstDestoryTips3 = SpannableUtils.colorizeMatchingText(dstDestoryTips3, "1", ContextCompat.getColor(this, R.color.default_theme_color));
        dstDestoryTips3 = SpannableUtils.sizeMatchingText(dstDestoryTips3, "1", DisplayUtils.sp2px(this, 20));
        mBinding.dstDestoryTips3.setText(dstDestoryTips3);

        Spannable dstDestoryTips4 = new SpannableString(String.format(getString(R.string.dst_destory_group_tips4),"-- GAS", "14400", "--"));
        dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, "-- GAS", Typeface.BOLD);
        dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, "14400", Typeface.BOLD);
        dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, "--", Typeface.BOLD);
        mBinding.dstDestoryTips4.setText(dstDestoryTips4);


        
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


        
        mBinding.dstDestoryNum.addTextChangedListener(new CusTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String trim = s.toString().trim();
                    
                    if (TextUtils.isEmpty(trim)) {
                        trim = "0";
                        mBinding.dstDestoryHashGet.setText("0");
                    } else {
                        if (null != mConfigBean && mConfigBean.data != null && !TextUtils.isEmpty(mConfigBean.data.getBurnPowerRatio())) {
                            try {
                                BigDecimal decimal = new BigDecimal(mConfigBean.data.getBurnPowerRatio());
                                BigDecimal input = new BigDecimal(trim);
                                mBinding.dstDestoryHashGet.setText(decimal.multiply(input).stripTrailingZeros().toPlainString());
                            } catch (Exception e) {
                                mBinding.dstDestoryHashGet.setText("--");
                            }
                        } else {
                            mBinding.dstDestoryHashGet.setText("--");
                        }
                    }
                    mBinding.dstDestoryNumPledge.setText(new Spanny(getString(R.string.quantity2)).append(" " + trim + " " + mShowCoinName, new ForegroundColorSpan(Color.parseColor("#FF9900"))));
                } catch (NumberFormatException e) {
                    ToastUtil.showToast(getString(R.string.please_input_legal_number));
                }
            }
        });


        
        mBinding.dstDestoryBt.setOnClickListener(v -> {
            try {
                
                String inputNum = mBinding.dstDestoryNum.getText().toString().trim();
                if (TextUtils.isEmpty(inputNum)) {
                    showToast(mBinding.dstDestoryNum.getHint().toString());
                    return;
                }
                
                String burnAmount = inputNum;
                String useFreeNum = inputNum;
                if (userFreeze) {
                    
                    burnAmount = "0";
                    if (null != mConfigBean && !TextUtils.isEmpty(mConfigBean.freezeNum) &&
                            new BigDecimal(mConfigBean.freezeNum).compareTo(new BigDecimal(inputNum)) < 0) {
                        showToast(R.string.balance_no_enaful);
                        return;
                    }
                } else {
                    
                    useFreeNum = "0";
                    
                    BigDecimal min_burn_amount = new BigDecimal("0.01");
                    BigDecimal inputNumBigDecimal = new BigDecimal(inputNum);
                    if (min_burn_amount.compareTo(inputNumBigDecimal) > 0) {
                        showToast(String.format(getString(R.string.min_burn_amount), "0.01"+mShowCoinName));
                        return;
                    }
                    if (null != mConfigBean && !TextUtils.isEmpty(mConfigBean.balance) &&
                            new BigDecimal(mConfigBean.balance).compareTo(new BigDecimal(inputNum)) < 0) {
                        showToast(R.string.balance_no_enaful);
                        return;
                    }
                }

                
                String toAddr = mBinding.dstDestoryHashAddress.getText().toString().trim();
                if (TextUtils.isEmpty(toAddr)) {
                    showToast(R.string.get_gas_addr_title_hint);
                    return;
                }

                getViewModel().doDestroy(DestoryGroupActivity.this, mAddress, toAddr, mGroupId, burnAmount, useFreeNum);
            } catch (NumberFormatException e) {
                ToastUtil.showToast(getString(R.string.please_input_legal_number));
            }
        });
    }


    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mDataLD, data->{
            showInfo(data);
        });

        
        getViewModel().observe(getViewModel().mDstPriceLD, price->{
            if (!TextUtils.isEmpty(price)) {
                mBinding.dstDestoryPrice.setText(price + " "+mShowCoinName+"/USDT");
            }
        });

        
        getViewModel().observe(getViewModel().mSuccessLD, data->{
            showToast(R.string.operate_success);
            String powerAmount = data.value1;
            if (!TextUtils.isEmpty(powerAmount)) {
                BuyDvmSuccessDialogFragment dialog =  BuyDvmSuccessDialogFragment.newInstance(powerAmount, data.value2);
                dialog.setListener(new Function1<View, Unit>() {
                    @Override
                    public Unit invoke(View view) {
                        finish();
                        return null;
                    }
                });
                dialog.show(getSupportFragmentManager(), DestoryGroupActivity.this.getClass().getSimpleName());
            } else {
                finish();
            }
        });

        getViewModel().getInfo(mAddress);
    }

    public void showInfo(EvmosDaoParams configBean) {
        if (null == configBean) {
            return;
        }
        mConfigBean = configBean;

        
        mBinding.dstDestoryNum.setText("");

        if (null != configBean.data) {
            String toRatio = ""+configBean.data.getBurnPowerRatio();
            Spannable dstDestoryTips3 = new SpannableString(String.format(getString(R.string.dst_destory_group_tips3), mShowCoinName, toRatio));
            dstDestoryTips3 = SpannableUtils.colorizeMatchingText(dstDestoryTips3, "1", ContextCompat.getColor(this, R.color.default_theme_color));
            dstDestoryTips3 = SpannableUtils.sizeMatchingText(dstDestoryTips3, "1", DisplayUtils.sp2px(this, 20));
            dstDestoryTips3 = SpannableUtils.colorizeMatchingText(dstDestoryTips3, toRatio, ContextCompat.getColor(this, R.color.default_theme_color));
            dstDestoryTips3 = SpannableUtils.sizeMatchingText(dstDestoryTips3, toRatio, DisplayUtils.sp2px(this, 20));
            mBinding.dstDestoryTips3.setText(dstDestoryTips3);

            String toGas = toRatio+" GAS";
            String reward = ""+configBean.data.getDayBurnReward();
            Spannable dstDestoryTips4 = new SpannableString(String.format(getString(R.string.dst_destory_group_tips4),toGas, "14400", reward));
            dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, toGas, Typeface.BOLD);
            dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, "14400", Typeface.BOLD);
            dstDestoryTips4 = SpannableUtils.styleMatchingText(dstDestoryTips4, reward, Typeface.BOLD);
            mBinding.dstDestoryTips4.setText(dstDestoryTips4);

            
            mBinding.dstDestoryExchange.setText("1 "+ mShowCoinName+ " : "+configBean.data.getBurnPowerRatio()+" GAS "+getString(R.string.compute_power));

            
            mBinding.dstDestoryAddress.setText(configBean.data.burn_address);
        }


        
        if (!TextUtils.isEmpty(configBean.balance)) {
            mBinding.balanceTv.setText(configBean.balance);
        }

        
        if (!TextUtils.isEmpty(configBean.freezeNum)) {
            mBinding.freezeNumTv.setText(configBean.freezeNum);
        }


    }
}
