package com.app.home.ui.ver.detial.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.FragmentVWalletVeryDialogBinding;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.math.BigDecimal;
import java.util.List;

import common.app.base.BaseDialogFragment;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import im.vector.app.core.platform.SimpleTextWatcher;


public class VWalletVeryDialogFragment extends BaseDialogFragment {


    private static final String TAG = "VWalletVeryDialogFragment";
    private FragmentVWalletVeryDialogBinding mBinding;
    public static final String PARAM_BALANCE = "PARAM_balance";
    private int mDecimal = 18;

    public static VWalletVeryDialogFragment getInstance(String balance) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_BALANCE, balance);
        VWalletVeryDialogFragment fragment = new VWalletVeryDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (null != dialog) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBinding = FragmentVWalletVeryDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void initData() {
        setCancelable(false);
        Bundle bundle = getArguments();
        String balanceAll = "";
        if (null != bundle) {
            balanceAll = bundle.getString(PARAM_BALANCE, "");
        }
        
        List<AssertBean> assets = WalletDBUtil.getInstent(getContext()).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }

        mBinding.walletVeryDiss.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });

        String finalBalanceAll = balanceAll;
        mBinding.walletVeryOk.setOnClickListener(v -> {
            try {
                
                String balance = mBinding.walletVeryDialogBalance.getText().toString().trim();
                if (TextUtils.isEmpty(balance)) {
                    ToastUtil.showToast(mBinding.walletVeryDialogBalance.getHint().toString().trim());
                    return;
                }
                BigDecimal inputDecimal = new BigDecimal(balance);
                BigDecimal amount = new BigDecimal(AllUtils.getTenDecimalValue(finalBalanceAll, mDecimal, 4));
                if (inputDecimal.compareTo(amount) > 0) {
                    showToast(getString(R.string.fm_balance_not_enough));
                    return;
                }

                if (null != mIConsume) {
                    mIConsume.click(balance);
                }
                dismissAllowingStateLoss();
            } catch (NumberFormatException e) {
                ToastUtil.showToast(getString(R.string.please_input_legal_number));
            }
        });


        WalletEntity walletInfo = WalletDBUtil.getInstent(mContext).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != walletInfo) {
            address = walletInfo.getAllAddress();
        }
        mBinding.walletVeryDialogAddress.setText(address);
        

        if (TextUtils.isEmpty(balanceAll)) {
            balanceAll = "--";
        } else {
            balanceAll = AllUtils.getTenDecimalValue(balanceAll, mDecimal, 4);
        }
        String coinName = getString(R.string.default_token_name).toUpperCase();
        mBinding.walletVeryDialogCoinNum.setText(balanceAll + " "+coinName);

        mBinding.walletVeryDialogBalance.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                super.afterTextChanged(s);
                if (TextUtils.isEmpty(s) || s.toString().compareTo("0") <= 0){
                    mBinding.walletVeryOk.setTextColor(ContextCompat.getColor(mContext,R.color.default_hint_text_color));
                    mBinding.walletVeryOk.setEnabled(false);
                }else {
                    mBinding.walletVeryOk.setTextColor(ContextCompat.getColor(mContext,R.color.default_button_color));
                    mBinding.walletVeryOk.setEnabled(true);
                }
            }
        });
    }


    private IConsume mIConsume;

    public void setIConsume(IConsume IConsume) {
        mIConsume = IConsume;
    }

    public interface IConsume {
        void click(String input);
    }
}
