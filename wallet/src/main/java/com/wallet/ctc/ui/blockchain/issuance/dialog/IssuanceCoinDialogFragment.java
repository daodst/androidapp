package com.wallet.ctc.ui.blockchain.issuance.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.wallet.ctc.databinding.FragmentIssuanceCoinDialogBinding;
import com.wallet.ctc.ui.blockchain.issuance.IssuanceCoinVM;

import common.app.base.BaseDialogFragment;



public class IssuanceCoinDialogFragment extends BaseDialogFragment<IssuanceCoinVM> {

    FragmentIssuanceCoinDialogBinding mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setDialogBackground();
        mView = FragmentIssuanceCoinDialogBinding.inflate(inflater, container, false);
        return mView.getRoot();
    }

    
    protected void setDialogBackground() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
    public void initView(@Nullable View view) {
        super.initView(view);

        mView.issuanceCoinNum.setText("0.1025642");
        mView.issuanceCoinUnint.setText("DST");
        mView.issuanceCoinLeft.setOnClickListener(v -> {
            dismissLoadingDialog();
        });
        mView.issuanceCoinRight.setOnClickListener(v -> {
            
        });
    }
}
