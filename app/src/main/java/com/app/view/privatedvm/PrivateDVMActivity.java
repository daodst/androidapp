package com.app.view.privatedvm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.app.anim.AnimationAssistanceHelper;
import com.app.anim.AnimationType;
import com.app.databinding.ActivityPrivateDvmBinding;
import com.app.view.dialog.HashrateAuthorizationPopup;
import com.app.view.privatedvm.edit.DVMEditActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.view.TitleBarView;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import im.vector.app.provide.ChatStatusProvide;


public class PrivateDVMActivity extends BaseActivity<PrivateDVMVM> implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private ActivityPrivateDvmBinding binding;
    private MyPrivateDVMAdapter mDvmAdapter;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        binding = ActivityPrivateDvmBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        binding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {

            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                startActivity(new Intent(PrivateDVMActivity.this, DVMEditActivity.class));
            }
        });
        binding.refreshLayout.setOnRefreshListener(this);

        
        mDvmAdapter = new MyPrivateDVMAdapter(new ArrayList<>());
        
        
        mDvmAdapter.setEmptyView(R.layout.mk_orders_empty, binding.rvList);
        binding.rvList.setAdapter(mDvmAdapter);
        
        mDvmAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            EvmosDvmListBean.Data data = (EvmosDvmListBean.Data) adapter.getData().get(position);
            if (view1.getId() == R.id.btnReward) {
                
                String address = ChatStatusProvide.getAddress(getApplication());
                
                AnimationAssistanceHelper.getInstance(this, address, address, data.cluster_chat_id)
                        .setAnimationType(AnimationType.TYPE_DVM_VIRTUAL)
                        .setCallbackConsumer((status, amount) -> {
                            if (!"-1".equals(status)) {
                                
                                getViewModel().onRefresh();
                            }
                        })
                        .init();
            } else if (view1.getId() == R.id.btnReward2) {
                
                String address = ChatStatusProvide.getAddress(getApplication());
                
                AnimationAssistanceHelper.getInstance(this, address, address, data.cluster_chat_id)
                        .setAnimationType(AnimationType.TYPE_DVM_VIRTUAL)
                        .setCallbackConsumer((status, amount) -> {
                            if (!"-1".equals(status)) {
                                
                                getViewModel().onRefresh();
                            }
                        })
                        .init();
            } else if (view1.getId() == R.id.btnPower) {
                
                HashrateAuthorizationPopup authPopup = new HashrateAuthorizationPopup(this, consumerData -> {
                    String contractAddress = consumerData.value1;
                    String authHeight = consumerData.value2;
                    
                    getViewModel().myDvmApprove(this, contractAddress, data.cluster_chat_id, authHeight);
                });
                new XPopup.Builder(this).dismissOnTouchOutside(true).asCustom(authPopup).show();
            } else if (view1.getId() == R.id.tvPowerContract) {
                
                AllUtils.copyText(data.auth_contract);
                showToast(R.string.copy_success);
            } else if (view1.getId() == R.id.tvPowerContract2) {
                
                AllUtils.copyText(data.auth_contract);
                showToast(R.string.copy_success);
            } else if (view1.getId() == R.id.btnPowerDetail) {
                
            }
            
        });

    }

    @Override
    public void initData() {
        super.initData();

        getViewModel().observe(viewModel.mLiveData, this::setData);
    }

    @Override
    public void onRefresh() {
        getViewModel().onRefresh();
    }

    @Override
    @Deprecated
    public void onLoadMoreRequested() {
        getViewModel().onLoadMore();
    }

    private void setData(List<EvmosDvmListBean.Data> pData) {
        if (getViewModel().isRefresh) {
            mDvmAdapter.setNewData(pData);
            binding.refreshLayout.setRefreshing(false);
        } else {
            if (null == pData) mDvmAdapter.loadMoreFail();
            else {
                mDvmAdapter.addData(pData);
                if (pData.size() > 0) mDvmAdapter.loadMoreComplete();
                else mDvmAdapter.loadMoreEnd();
            }
        }
    }
}
