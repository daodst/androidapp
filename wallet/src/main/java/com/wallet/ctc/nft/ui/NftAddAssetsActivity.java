

package com.wallet.ctc.nft.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.util.FastClickUtils;
import com.wallet.ctc.view.TitleBarView;

import butterknife.BindView;
import butterknife.OnClick;
import common.app.base.BaseActivity;


public class NftAddAssetsActivity extends BaseActivity<NftAddAssetsBiz> {
    @BindView(R2.id.title_bar)
    TitleBarView titleBar;
    @BindView(R2.id.et_address)
    EditText etAddress;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;

    
    public static void intent(Context context) {
        Intent in = new Intent(context, NftAddAssetsActivity.class);
        context.startActivity(in);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_nft_add_assets;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        register();
    }

    private void register() {
        getViewModel().doAddAssetsLiveData.observe(this, rs -> {
            if (rs.isSuccess()) {
                showToast(R.string.caozuo_success);
                finish();
            } else {
                showToast(rs.getInfo());
            }
        });
    }

    @OnClick(R2.id.btn_confirm)
    public void onClick() {
        if (FastClickUtils.isFastClick()) {
            return;
        }

        if (TextUtils.isEmpty(etAddress.getText().toString())) {
            showToast(etAddress.getHint().toString());
            return;
        }
        getViewModel().addNFtAssets(etAddress.getText().toString());
    }
}
