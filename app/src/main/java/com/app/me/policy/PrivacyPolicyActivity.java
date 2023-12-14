package com.app.me.policy;


import android.os.Bundle;

import com.app.R;
import com.app.chain.ChainSyncActivity;
import com.app.databinding.ActivityPrivacyPolicyBinding;
import com.wallet.ctc.ui.blockchain.privacy.ChatPrivacySettingActivity;
import com.wallet.ctc.util.AllUtils;

import common.app.base.BaseActivity;
import im.vector.app.provide.ChatStatusProvide;



public class PrivacyPolicyActivity extends BaseActivity {

    private ActivityPrivacyPolicyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        mBinding.privacyPolicyTopbar.setLeftTv(v -> {
            finish();
        }).setMiddleTv(R.string.room_privacy_setting_title, R.color.default_titlebar_title_color);

        mBinding.privacySetting.setOnClickListener(v -> {
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(this));
            startActivity(ChatPrivacySettingActivity.getIntent(this, address));
        });
        mBinding.balckSetting.setOnClickListener(v -> {
            startActivity(ChainSyncActivity.getBlackIntent(this, "", ""));
        });
        mBinding.whiteSetting.setOnClickListener(v -> {
            startActivity(ChainSyncActivity.getWhiteIntent(this, "", ""));
        });
    }
}
