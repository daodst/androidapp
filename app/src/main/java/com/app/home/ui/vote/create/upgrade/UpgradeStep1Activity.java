package com.app.home.ui.vote.create.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.databinding.ActivityUpgradeStep1Binding;
import com.app.home.ui.vote.create.upgrade.type1.UpgradeType1Activity;
import com.app.home.ui.vote.create.upgrade.type2.UpgradeType2Activity;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;

public class UpgradeStep1Activity extends BaseActivity {


    ActivityUpgradeStep1Binding mBinding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityUpgradeStep1Binding.inflate(LayoutInflater.from(this));
        return mBinding.getRoot();
    }


    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            RxNotice notice = (RxNotice) obj;
            if (notice.mType == RxNotice.MSG_SUBMIT_VOTE) {
                finish();
            }
        }
    }



    @Override
    public void initView(@Nullable View view) {
        mBinding.upgradeTitle.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        
        mBinding.upgradeApp.setOnClickListener(v -> {

            startActivity(new Intent(this, UpgradeType1Activity.class));
        });
        
        mBinding.upgradeNode.setOnClickListener(v -> {
            startActivity(UpgradeType2Activity.getIntent(this,UpgradeType2Activity.TYPE_NODE));
        });
        
        mBinding.upgradeChat.setOnClickListener(v -> {
            startActivity(UpgradeType2Activity.getIntent(this,UpgradeType2Activity.TYPE_CHAT));
        });
    }
}
