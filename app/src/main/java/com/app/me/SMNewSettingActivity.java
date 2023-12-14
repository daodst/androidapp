package com.app.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.me.policy.PrivacyPolicyActivity;
import com.benny.openlauncher.activity.HomeActivity;
import com.wallet.ctc.databinding.SmActivityNewSettingBinding;
import com.wallet.ctc.ui.blockchain.setnode.SettingNodeActivity;

import java.util.Locale;

import javax.inject.Inject;

import common.app.ActivityRouter;
import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;
import common.app.utils.LanguageUtil;
import dagger.hilt.android.AndroidEntryPoint;
import im.vector.app.features.configuration.VectorConfiguration;
import im.vector.app.features.settings.VectorLocale;
import im.vector.app.provide.ChatStatusProvide;


@AndroidEntryPoint
public class SMNewSettingActivity extends BaseActivity {
    private SmActivityNewSettingBinding mBinding;

    @Inject
    VectorConfiguration vectorConfiguration;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = SmActivityNewSettingBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
        
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        setOnItemClick();
    }

    @Override
    public void initData() {
        super.initData();
    }

    private void setOnItemClick() {
        mBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        mBinding.linChooseNode.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingNodeActivity.class);
            startActivity(intent);
        });

        
        mBinding.tvCurrentLanguage.setText(LanguageUtil.getNowLanguageStr(this));
        
        mBinding.linLanguage.setOnClickListener(v -> {
            LanguageUtil.showSettingDialog(this, () -> {
                Locale nowLocal = LanguageUtil.getNowLocal(this);
                VectorLocale.INSTANCE.saveApplicationLocale(nowLocal);
                vectorConfiguration.applyToApplicationContext();
                if (HomeActivity._launcher != null) {
                    HomeActivity._launcher.recreate();
                    finish();
                }
            });
        });

        
        mBinding.linPrivacyPolicy.setOnClickListener(v -> {
            startPage(new Intent(this, PrivacyPolicyActivity.class), true);
        });
        
        mBinding.tvChainSync.setOnClickListener(v -> {
            startPage(ActivityRouter.getIntent(this, ActivityRouter.App.A_ChainSyncActivity), true);
        });

    }

    @Override
    protected boolean isLogined() {
        return ChatStatusProvide.loginStatus(this);
    }

    
    private void startPage(Intent intent, boolean needLogin) {
        if (needLogin && !isLogined()) {
            goLoginPage();
        } else {
            startActivity(intent);
        }
    }

    private void goLoginPage() {
        startActivity(new Intent(this, im.vector.app.features.MainActivity.class));
    }
}
