package com.app.home.ui.vote.detial.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.app.databinding.FragmentVoteDetialDialogBinding;

import common.app.base.BaseDialogFragment;


public class VoteDetialDialogFragment extends BaseDialogFragment {


    private FragmentVoteDetialDialogBinding mBinding;

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
        mBinding = FragmentVoteDetialDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    
    
    private static final int TYPE1 = 1;
    
    private static final int TYPE2 = 3;
    
    private static final int TYPE3 = 4;
    
    private static final int TYPE4 = 2;

    private int type = TYPE1;


    @Override
    public void initData() {
        setCancelable(false);
        mBinding.voteDetialDiss.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });

        mBinding.voteDetialApprove.setOnClickListener(v -> {
            reset();
            mBinding.voteDetialApproveCk.setChecked(true);
            type = TYPE1;
        });
        mBinding.voteDetialAgainst.setOnClickListener(v -> {
            reset();
            mBinding.voteDetialAgainstCk.setChecked(true);
            type = TYPE2;

        });
        mBinding.voteDetialDisapprove.setOnClickListener(v -> {
            reset();
            mBinding.voteDetialDisapproveCk.setChecked(true);
            type = TYPE3;

        });
        mBinding.voteDetialGiveUp.setOnClickListener(v -> {
            reset();
            mBinding.voteDetialGiveUpCk.setChecked(true);
            type = TYPE4;
        });
        mBinding.voteDetialOk.setOnClickListener(v -> {
            if (null != mIConsume) {
                mIConsume.click(type);
            }
            dismissAllowingStateLoss();
        });
    }

    private void reset() {
        mBinding.voteDetialApproveCk.setChecked(false);
        mBinding.voteDetialAgainstCk.setChecked(false);
        mBinding.voteDetialDisapproveCk.setChecked(false);
        mBinding.voteDetialGiveUpCk.setChecked(false);
    }

    private IConsume mIConsume;

    public void setIConsume(IConsume IConsume) {
        mIConsume = IConsume;
    }

    public interface IConsume {
        void click(Integer type);
    }
}
