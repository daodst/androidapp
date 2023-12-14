package com.app.home.ui.vote.create.upgrade.type2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityUpgradeType2Binding;
import com.app.home.ui.vote.create.CreateVoteActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;

public class UpgradeType2Activity extends BaseActivity {


    private static final String TAG = "UpgradeType2Activity";
    ActivityUpgradeType2Binding mBinding;


    public static final String PARAM_TYPE = "param_type";
    public static final String TYPE_NODE = "TYPE_node";
    public static final String TYPE_CHAT = "TYPE_chat";

    public static Intent getIntent(Context context, String type) {

        Intent intent = new Intent(context, UpgradeType2Activity.class);
        intent.putExtra(PARAM_TYPE, type);

        return intent;
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityUpgradeType2Binding.inflate(LayoutInflater.from(this));
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

        if (getIntent() == null) {
            finish();
            return;
        }
        String type = getIntent().getStringExtra(PARAM_TYPE);
        String showName = "";
        if (TextUtils.equals(TYPE_NODE, type)) {
            showName = getString(R.string.upgrade_node_name);
        } else {
            showName = getString(R.string.upgrade_chat_name);
            mBinding.upgradeType2Sha512Parent.setVisibility(View.VISIBLE);
        }
        mBinding.upgradeType2Title.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mBinding.upgradeType2TypeName.setText(showName);
        mBinding.upgradeType2Bt.setOnClickListener(v -> {
            String version = mBinding.upgradeType2Version.getText().toString().trim();
            if (TextUtils.isEmpty(version)) {
                ToastUtil.showToast(mBinding.upgradeType2Version.getHint().toString().trim());
                return;
            }
            String linuxUrl = mBinding.upgradeType2UrlLinux.getText().toString().trim();
            if (TextUtils.isEmpty(linuxUrl)) {
                ToastUtil.showToast(mBinding.upgradeType2UrlLinux.getHint().toString().trim());
                return;
            }
            String windowsUrl = mBinding.upgradeType2UrlWindows.getText().toString().trim();
            if (TextUtils.isEmpty(windowsUrl)) {
                ToastUtil.showToast(mBinding.upgradeType2UrlWindows.getHint().toString().trim());
                return;
            }
            String hight = mBinding.upgradeType2Hight.getText().toString().trim();
            if (TextUtils.isEmpty(hight)) {
                ToastUtil.showToast(mBinding.upgradeType2Hight.getHint().toString().trim());
                return;
            }
            String name = mBinding.upgradeType2Name.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showToast(mBinding.upgradeType2Name.getHint().toString().trim());
                return;
            }

            if (TextUtils.equals(TYPE_NODE, type)) {

                

                Map<String, Object> json = new HashMap<>();
                Map<String, Object> blockchain = new HashMap<>();
                Map<String, Object> binaries = new HashMap<>();
                Map<String, Object> linux = new HashMap<>();
                Map<String, Object> windows = new HashMap<>();

                json.put("blockchain", blockchain);
                blockchain.put("version", version);
                blockchain.put("binaries", binaries);
                binaries.put("linux/amd64", linux);
                binaries.put("windows/amd64", windows);
                windows.put("url", linuxUrl);
                linux.put("url", windowsUrl);
                linux.put("size", 0);
                windows.put("size", 0);
                String jsonStr = new Gson().toJson(json);
                startActivity(CreateVoteActivity.getUpIntent(this, name, jsonStr, hight));


            } else {
                String sha512Linux = mBinding.upgradeType2Sha512Linux.getText().toString().trim();
                if (TextUtils.isEmpty(sha512Linux)) {
                    showToast(mBinding.upgradeType2Sha512Linux.getHint().toString().trim());
                    return;
                }

                String sha512Windows = mBinding.upgradeType2Sha512Windows.getText().toString().trim();
                if (TextUtils.isEmpty(sha512Windows)) {
                    showToast(mBinding.upgradeType2Sha512Windows.getHint().toString().trim());
                    return;
                }

                

                Map<String, Object> json = new HashMap<>();
                Map<String, Object> gateway = new HashMap<>();
                Map<String, Object> binaries = new HashMap<>();
                Map<String, Object> linux = new HashMap<>();
                Map<String, Object> windows = new HashMap<>();

                json.put("gateway", gateway);
                gateway.put("version", version);
                gateway.put("binaries", binaries);
                binaries.put("linux/amd64", linux);
                binaries.put("windows/amd64", windows);
                windows.put("url", linuxUrl);
                linux.put("url", windowsUrl);
                linux.put("size", 0);
                linux.put("hash", sha512Linux);
                windows.put("size", 0);
                windows.put("hash", sha512Windows);
                String jsonStr = new Gson().toJson(json);
                startActivity(CreateVoteActivity.getUpIntent(this, name, jsonStr, hight));


            }


        });
    }
}
