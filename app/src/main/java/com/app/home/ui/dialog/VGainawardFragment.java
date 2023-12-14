package com.app.home.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.app.databinding.FragmentVGainawardBinding;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;

import common.app.base.BaseDialogFragment;



public class VGainawardFragment extends BaseDialogFragment {


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


    private FragmentVGainawardBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (null != dialog) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBinding = FragmentVGainawardBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void initData() {
        setCancelable(false);

        WalletEntity walletInfo = WalletDBUtil.getInstent(mContext).getWalletInfo(WalletUtil.MCC_COIN);
        String address = "";
        if (null != walletInfo) {
            address = walletInfo.getAllAddress();
        }
        mBinding.gainawardAddress.setText(address);

        mBinding.gainawardOk.setOnClickListener(v -> {
            if (null != mIConsume) {
                mIConsume.click();
            }
            dismissAllowingStateLoss();
        });

        mBinding.gainawardDiss.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });
    }

    private IConsume mIConsume;

    public void setIConsume(IConsume IConsume) {
        mIConsume = IConsume;
    }

    public interface IConsume {
        void click();
    }
}
