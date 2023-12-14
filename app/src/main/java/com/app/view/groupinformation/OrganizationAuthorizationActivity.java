package com.app.view.groupinformation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityOrganizationAuthorizationBinding;
import com.wallet.ctc.view.TitleBarView;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import im.vector.app.core.platform.SimpleTextWatcher;


public class OrganizationAuthorizationActivity extends BaseActivity<OrganizationAuthorizationVM> {
    private ActivityOrganizationAuthorizationBinding mBinding;

    private String groupId;
    private String clusterVotePolicy;
    private String amount;
    private String mContract;
    private String mBlockHeight;
    private String mDescription;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityOrganizationAuthorizationBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        groupId = getIntent().getStringExtra("groupId");
        clusterVotePolicy = getIntent().getStringExtra("clusterVotePolicy");
        amount = getIntent().getStringExtra("amount");
    }

    @Override
    public void initView(@Nullable View view) {
        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mBinding.tvRemind.setText(R.string.governance_authorization_remind);
        mBinding.tvKuCun.setText(getString(R.string.governance_authorization_string_2, amount));

        mBinding.etAuthorizationTime.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                String inputTx = s.toString();
                if (TextUtils.isEmpty(inputTx)) {
                    mBinding.etAuthorizationBlockHeight.setText("0");
                    return;
                }
                long height = new BigDecimal(inputTx).multiply(new BigDecimal(14400)).longValue();
                mBinding.etAuthorizationBlockHeight.setText(height + "");
            }
        });

        mBinding.button.setOnClickListener(v -> {
            if (checkInput()) getViewModel().onSubmit(this,clusterVotePolicy, groupId, mBlockHeight, mContract, mDescription);
        });
    }

    private boolean checkInput() {
        mContract = mBinding.etContract.getText().toString();
        String time = mBinding.etAuthorizationTime.getText().toString();
        mBlockHeight = mBinding.etAuthorizationBlockHeight.getText().toString();
        mDescription = mBinding.etDescription.getText().toString();

        if (TextUtils.isEmpty(mContract)) {
            showToast(mBinding.etContract.getHint().toString());
            return false;
        }

        if (mContract.length() < 15){
            showToast(R.string.vote_please_input_right_auth_addr);
            return false;
        }


        if (TextUtils.isEmpty(time)) {
            showToast(mBinding.etAuthorizationTime.getHint().toString());
            return false;
        }

        try {
            if(Float.parseFloat(time) <= 0){
                showToast(R.string.vote_please_input_right_auth_time);
                return false;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }



        if (TextUtils.isEmpty(mBlockHeight)) {
            showToast(mBinding.etAuthorizationBlockHeight.getHint().toString());
            return false;
        }
        if (TextUtils.isEmpty(mDescription)) {
            showToast(mBinding.etDescription.getHint().toString());
            return false;
        }

        return true;
    }
}
