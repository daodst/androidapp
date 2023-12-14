

package com.wallet.ctc.ui.me.virtualphone;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.SmActivityVirtualPhoneBinding;
import com.wallet.ctc.model.me.SMVirtualPhoneEntity;
import com.wallet.ctc.ui.blockchain.did.WalletDidTransferActivity;

import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.utils.SpUtil;


public class SMVirtualPhoneActivity extends BaseActivity<SMVirtualPhoneActivityVM> implements SwipeRefreshLayout.OnRefreshListener {
    private SmActivityVirtualPhoneBinding mBinding;
    private SMVirtualPhoneAdapter mAdapter;
    private static final String KEY_ADDR = "address";
    private String mAddress;

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, SMVirtualPhoneActivity.class);
        intent.putExtra(KEY_ADDR, address);
        return intent;
    }

    @Override
    public void initParam() {
        mAddress = getIntent().getStringExtra(KEY_ADDR);
        if (TextUtils.isEmpty(mAddress)) {
            showToast(R.string.data_error);
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = SmActivityVirtualPhoneBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
            mBinding.titleBar.setPadding(0, Eyes.getStatusBarHeight(this), 0, 0);
        }

        mBinding.refreshView.setOnRefreshListener(this);
        mBinding.ivBack.setOnClickListener(v -> finish());
        
        mBinding.ivCallPhone.setOnClickListener(v -> {
            try {
                Class<?> clazz = Class.forName("im.vector.app.features.call.phone.SMDialActivity");
                Intent intent = new Intent(this, clazz);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        mAdapter = new SMVirtualPhoneAdapter(new ArrayList<>(), mobile -> {
            startActivity(WalletDidTransferActivity.getIntent(SMVirtualPhoneActivity.this, mAddress, mobile));
        });
        RecyclerView recyclerView = mBinding.rvList;
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            for (SMVirtualPhoneEntity entity : mAdapter.getData()) {
                entity.checked = false;
            }
            SMVirtualPhoneEntity entity = (SMVirtualPhoneEntity) adapter.getData().get(position);
            entity.checked = true;
            adapter.notifyDataSetChanged();
            
            SpUtil.saveNowPhone(mAddress, entity.phoneNumber);
        });

        
        mBinding.btnMint.setOnClickListener(v -> {
            startActivity(SMGetVirtualPhoneActivity.getIntent(SMVirtualPhoneActivity.this, mAddress));
        });
    }

    @Override
    public void initData() {
        super.initData();
        getViewModel().observe(viewModel.mVirtualPhone, entity -> mAdapter.setNewData(entity));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getHoldPhoneList(mAddress);
    }

    @Override
    public void onRefresh() {
        mBinding.refreshView.setRefreshing(false);
        getViewModel().getHoldPhoneList(mAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModel().onDestroy();
    }
}
