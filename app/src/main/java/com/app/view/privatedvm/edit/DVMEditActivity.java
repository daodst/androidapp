package com.app.view.privatedvm.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.databinding.ActivityDvmeditBinding;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.view.TitleBarView;

import common.app.base.BaseActivity;
import common.app.utils.SpUtil;

public class DVMEditActivity extends BaseActivity<DVMEditViewModel> {

    ActivityDvmeditBinding binding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        binding = ActivityDvmeditBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {

        binding.dvmEditTitle.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        String node = SpUtil.getDefNode(WalletUtil.MCC_COIN);
        String host = common.app.utils.AllUtils.getHomeUrl(node);
        
        binding.dvmEditContent.setText(host + ":50320");
        binding.dvmEditCopy.setOnClickListener(v -> {
            AllUtils.copyText(binding.dvmEditContent.getText().toString().trim());
        });
    }
}
