package com.app.home.ui.ver.detial.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.app.databinding.FragmentVWalletVeryDialogReleaseBinding;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.util.List;

import common.app.base.BaseDialogFragment;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;


public class VWalletVeryReleaseDialogFragment extends BaseDialogFragment {


    private FragmentVWalletVeryDialogReleaseBinding mBinding;

    public static final String PARAM_NAME = "param_name";
    public static final String PARAM_AMOUNT = "PARAM_amount";
    private int mDecimal = 18;

    public static VWalletVeryReleaseDialogFragment getInstance(String name, String amount) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_NAME, name);
        bundle.putString(PARAM_AMOUNT, amount);
        VWalletVeryReleaseDialogFragment fragment = new VWalletVeryReleaseDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
        mBinding = FragmentVWalletVeryDialogReleaseBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void initData() {
        setCancelable(false);
        Bundle bundle = getArguments();
        String name = "";
        String amount = "";
        if (null != bundle) {
            name = bundle.getString(PARAM_NAME, "");
            amount = bundle.getString(PARAM_AMOUNT, "");
        }
        
        List<AssertBean> assets = WalletDBUtil.getInstent(getContext()).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
        mBinding.walletVeryReleaseDiss.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });

        mBinding.walletVeryReleaseOk.setOnClickListener(v -> {
            
            String balance = mBinding.walletVeryReleaseEd.getText().toString().trim();
            if (TextUtils.isEmpty(balance)) {
                ToastUtil.showToast(mBinding.walletVeryReleaseEd.getHint().toString().trim());
                return;
            }
            if (null != mIConsume) {
                mIConsume.click(balance);
            }
            dismissAllowingStateLoss();
        });

        mBinding.walletVeryReleaseName.setText(name);
        if (TextUtils.isEmpty(amount)) {
            amount = "--";
        } else {
            amount = AllUtils.getTenDecimalValue(amount, mDecimal, 4);
        }
        mBinding.walletVeryReleaseBalance.setText(amount );
    }


    private IConsume mIConsume;

    public void setIConsume(IConsume IConsume) {
        mIConsume = IConsume;
    }

    public interface IConsume {
        void click(String input);
    }
}
