package com.app.store;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.app.databinding.ActivityDappStoreBinding;
import com.app.store.detail.DAppStoreDetailActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;
import im.vector.app.core.platform.SimpleTextWatcher;


public class DAppStoreActivity extends BaseActivity<DAppStoreVM> implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener {
    private ActivityDappStoreBinding mBinding;
    private DAppStoreAdapter mAdapter;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityDappStoreBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mAdapter = new DAppStoreAdapter(new ArrayList<>());
        mAdapter.setEmptyView(R.layout.has_no_data, mBinding.recyclerView);
        
        
        
        mAdapter.setOnItemClickListener((adapter1, view1, position) -> {
            DAppStoreEntity entity = (DAppStoreEntity) adapter1.getData().get(position);
            Intent intent = new Intent(DAppStoreActivity.this, DAppStoreDetailActivity.class);
            
            intent.putExtra("info", getViewModel().mGson.toJson(entity));
            startActivity(intent);
        });
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.swipeRefreshLayout.setOnRefreshListener(this);

        mBinding.tvSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                viewModel.searchKeyword = s.toString();
                viewModel.isRefresh = true;
                viewModel.page = 1;
                viewModel.searchFilter();
            }
        });

        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getDAppStoreListData();
    }

    @Override
    public void initData() {
        super.initData();
        
        viewModel.mLiveData.observe(this, this::setData);
    }

    @Override
    public void onRefresh() {
        viewModel.isRefresh = true;
        viewModel.page = 1;
        viewModel.getDAppStoreListData();
    }

    @Override
    public void onLoadMoreRequested() {
        viewModel.isRefresh = false;
        viewModel.page++;
        viewModel.getDAppStoreListData();
    }

    private void setData(List<DAppStoreEntity> data) {
        if (viewModel.isRefresh) {
            mAdapter.setNewData(data);
            mBinding.swipeRefreshLayout.setRefreshing(false);
        } else {
            if (null != data) {
                mAdapter.addData(data);
                if (data.size() > 0) mAdapter.loadMoreComplete();
                else mAdapter.loadMoreEnd();
            } else mAdapter.loadMoreFail();
        }
    }
}
