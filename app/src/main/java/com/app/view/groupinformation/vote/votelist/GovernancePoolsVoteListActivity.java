package com.app.view.groupinformation.vote.votelist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.app.databinding.ActivityGovernancePoolVoteListBinding;
import com.app.me.destory_group.DestoryGroupActivity;
import com.app.view.groupinformation.vote.votedetail.ClusterVoteDetailActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;
import com.wallet.ctc.view.TitleBarView;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import im.vector.app.provide.ChatStatusProvide;


public class GovernancePoolsVoteListActivity extends BaseActivity<GovernancePoolsVoteListVM> implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private ActivityGovernancePoolVoteListBinding mBinding;
    private VoteListAdapter mAdapter;

    private String groupId;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityGovernancePoolVoteListBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        groupId = getIntent().getStringExtra("groupId");
        getViewModel().groupId = groupId;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        mBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                String address = ChatStatusProvide.getAddress(GovernancePoolsVoteListActivity.this);
                @SuppressLint("UnsafeIntentLaunch")
                Intent intent = DestoryGroupActivity.getIntent(GovernancePoolsVoteListActivity.this, address, groupId);
                startActivity(intent);
            }
        });

        mBinding.refreshView.setOnRefreshListener(this);
        mAdapter = new VoteListAdapter(new ArrayList<>(),groupId);
        mAdapter.setEmptyView(R.layout.mk_orders_empty, mBinding.rvList);
        
        
        mBinding.rvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            EvmosClusterVoteBean.Data entity =
                    (EvmosClusterVoteBean.Data) adapter.getData().get(position);
            
            Intent intent = new Intent(GovernancePoolsVoteListActivity.this, ClusterVoteDetailActivity.class);
            intent.putExtra("proposalId", entity.id);
            startActivity(intent);
        });

    }

    @Override
    public void initData() {
        super.initData();
        getViewModel().onRefresh();
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

    private void setData(List<EvmosClusterVoteBean.Data> data) {
        if (getViewModel().refresh) {
            mBinding.refreshView.setRefreshing(false);
            mAdapter.setNewData(data);
        } else {
            if (data == null) mAdapter.loadMoreFail();
            else {
                if (data.size() == 0) mAdapter.loadMoreEnd();
                else mAdapter.loadMoreComplete();
            }
        }
    }
}
