package com.app.view.groupinformation.ratio.commission;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityCommissionBinding;
import com.wallet.ctc.view.TitleBarView;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import im.vector.app.provide.ChatStatusProvide;


public class CommissionActivity extends BaseActivity<CommissionVM> {


    private static final String PARAM_CURRENT_RATE = "param_current_rate";
    private static final String PARAM_GROUP_ID = "param_group_id";
    private static final String PARAM_ID = "param_id";

    public static Intent getIntent(Context context, String currentRate, String id, String groupId) {
        Intent intent = new Intent(context, CommissionActivity.class);
        intent.putExtra(PARAM_CURRENT_RATE, currentRate);
        intent.putExtra(PARAM_GROUP_ID, groupId);
        intent.putExtra(PARAM_ID, id);
        return intent;
    }

    ActivityCommissionBinding binding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        binding = ActivityCommissionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {


        Intent intent = getIntent();
        String oldRate = intent.getStringExtra(PARAM_CURRENT_RATE);
        String groupId = intent.getStringExtra(PARAM_GROUP_ID);
        String id = intent.getStringExtra(PARAM_ID);
        binding.commissionRate.setText(getString(R.string.commissionRateTips) + "（" + oldRate + "%）");

        binding.commissionTitle.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        binding.commissionBt.setOnClickListener(v -> {

            String rate = binding.commissionRateEd.getText().toString().trim();
            if (TextUtils.isEmpty(rate)) {
                ToastUtil.showToast(binding.commissionRateEd.getHint().toString());
                return;
            }
            String desc = binding.commissionDesc.getText().toString().trim();
            if (TextUtils.isEmpty(desc)) {
                ToastUtil.showToast(binding.commissionDesc.getHint().toString());
                return;
            }
            String radio = new BigDecimal("1").subtract(new BigDecimal(rate).multiply(new BigDecimal("0.01"))).toPlainString();
            String address = ChatStatusProvide.getAddress(this);
            getViewModel().onSubmit(this, id, address, groupId, radio, getString(R.string.commissionTitle), desc);
        });
    }
}
