package com.app.home.ui.vote.list;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.R;
import com.app.databinding.ActivityVoteListBinding;
import com.app.home.pojo.VoteInfoWapper;
import com.app.home.ui.vote.create.StartCreateVoteActivity;
import com.app.home.ui.vote.list.adapter.VListItemAdapter;
import com.wallet.ctc.view.view.LoadMoreFooter;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;


public class VoteListActivity extends BaseActivity<VoteListVM> {


    ActivityVoteListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityVoteListBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
    }

    private VListItemAdapter mItemAdapter;

    private LoadMoreFooter mLoadMoreFooter;

    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            RxNotice notice = (RxNotice) obj;
            if (notice.mType == RxNotice.MSG_SUBMIT_VOTE) {
                getViewModel().getVoteInfo(null);
            }
        }
    }

    @Override
    public void initData() {
        mBinding.voteListTopbar.setLeftTv(v -> {
                    finish();
                }).setMiddleTv(R.string.vote_list_topbar_title, R.color.default_titlebar_title_color)
                .setRighterTvTextSize(12)
                .setRighterTvTextOnclick(R.string.vote_create_topbar_right, R.color.default_theme_color, v -> {
                    
                    startActivity(new Intent(VoteListActivity.this, StartCreateVoteActivity.class));
                });

        mItemAdapter = new VListItemAdapter();
        mBinding.voteListRv.setAdapter(mItemAdapter);

        mBinding.voteListRefersh.setOnRefreshListener(() -> {
            getViewModel().getVoteInfo(null);
        });
        mLoadMoreFooter = new LoadMoreFooter(this, mBinding.voteListRv, () -> {
            getViewModel().getVoteInfo(mPageList);
        });

        getViewModel().mLiveData.observe(this, result -> {
            reset();
            mPageList = result;
            if (null != mItemAdapter) {
                mItemAdapter.setVoteInfos(result.proposals);
                mLoadMoreFooter.setState(result.isEnd ? LoadMoreFooter.STATE_FINISHED : LoadMoreFooter.STATE_ENDLESS);
            }
            mBinding.voteListEmpty.setVisibility((null == result.proposals || 0 == result.proposals.size()) ? View.VISIBLE : View.GONE);
        });
        getViewModel().mErrorLiveData.observe(this, o -> {
            reset();
        });

        getViewModel().getVoteInfo(null);
    }


    private VoteInfoWapper mPageList;

    private void reset() {
        if (mBinding.voteListRefersh.isRefreshing()) {
            mBinding.voteListRefersh.setRefreshing(false);
        }
    }

}
