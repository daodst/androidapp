package com.app.home.ui.vote.create;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.databinding.ActivityStartCreateVoteBinding;
import com.app.home.ui.vote.create.params.StartParamsVoteActivity;
import com.app.home.ui.vote.create.pay.setp1.PayVoteStep1Activity;
import com.app.home.ui.vote.create.upgrade.UpgradeStep1Activity;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;


public class StartCreateVoteActivity extends BaseActivity {

    ActivityStartCreateVoteBinding mViewB;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityStartCreateVoteBinding.inflate(LayoutInflater.from(this));
        return mViewB.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mViewB.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        
        mViewB.layoutParams.setOnClickListener(view1 -> {
            startActivity(new Intent(this, StartParamsVoteActivity.class));
        });

        
        mViewB.layoutPay.setOnClickListener(view1 -> {

            startActivity(new Intent(this, PayVoteStep1Activity.class));

        });

        
        mViewB.layoutUpdate.setOnClickListener(view1 -> {
            startActivity(new Intent(this, UpgradeStep1Activity.class));
        });
        
        addSubscription();
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
}
