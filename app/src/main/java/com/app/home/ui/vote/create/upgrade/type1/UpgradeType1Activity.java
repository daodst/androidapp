package com.app.home.ui.vote.create.upgrade.type1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.databinding.ActivityUpgradeType1Binding;
import com.app.home.ui.vote.create.CreateVoteActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;

public class UpgradeType1Activity extends BaseActivity {


    private static final String TAG = "UpgradeType1Activity";
    ActivityUpgradeType1Binding mBinding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityUpgradeType1Binding.inflate(LayoutInflater.from(this));
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
        mBinding.upgradeType1Title.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mBinding.upgradeType1Bt.setOnClickListener(v -> {

            String version = mBinding.upgradeType1Version.getText().toString().trim();
            if (TextUtils.isEmpty(version)) {
                showToast(mBinding.upgradeType1Version.getHint().toString().trim());
                return;
            }
            String url = mBinding.upgradeType1Url.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                showToast(mBinding.upgradeType1Url.getHint().toString().trim());
                return;
            }
            String size = mBinding.upgradeType1Size.getText().toString().trim();
            if (TextUtils.isEmpty(size)) {
                showToast(mBinding.upgradeType1Size.getHint().toString().trim());
                return;
            }
            String hight = mBinding.upgradeType1Hight.getText().toString().trim();
            if (TextUtils.isEmpty(hight)) {
                showToast(mBinding.upgradeType1Hight.getHint().toString().trim());
                return;
            }
            String name = mBinding.upgradeType1Name.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                showToast(mBinding.upgradeType1Name.getHint().toString().trim());
                return;
            }
            String sha512 = mBinding.upgradeType1Sha512.getText().toString().trim();
            if (TextUtils.isEmpty(sha512)) {
                showToast(mBinding.upgradeType1Sha512.getHint().toString().trim());
                return;
            }


            Map<String, Object> json = new HashMap<>();
            Map<String, Object> app = new HashMap<>();
            Map<String, Object> binaries = new HashMap<>();
            Map<String, Object> android = new HashMap<>();

            json.put("app", app);
            app.put("version", version);
            app.put("binaries", binaries);
            binaries.put("android", android);
            android.put("url", url);
            android.put("size", Long.parseLong(size));
            android.put("hash", sha512);

            String jsonStr = new Gson().toJson(json);
            startActivity(CreateVoteActivity.getUpIntent(this, name, jsonStr, hight));

        });
    }
}
