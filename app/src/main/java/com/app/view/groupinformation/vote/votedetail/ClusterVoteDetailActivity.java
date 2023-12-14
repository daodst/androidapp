package com.app.view.groupinformation.vote.votedetail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.app.databinding.ActivityClusterVoteDetailBinding;
import com.app.databinding.ActivityClusterVoteDetailHeaderBinding;
import com.app.home.pojo.rpc.DposVoteParam;
import com.app.home.ui.utils.TimeUtils;
import com.app.home.ui.vote.detial.dialog.VoteDetialDialogFragment;
import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteInfoBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.utils.DisplayUtils;
import im.vector.app.provide.ChatStatusProvide;


public class ClusterVoteDetailActivity extends BaseActivity<ClusterVoteDetailVM> implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityClusterVoteDetailBinding mBinding;
    private ActivityClusterVoteDetailHeaderBinding mHeaderBinding;

    private int proposalId;
    private ClusterVoteDetailAdapter mAdapter;

    
    private boolean isVoter = false;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityClusterVoteDetailBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        proposalId = getIntent().getIntExtra("proposalId", 0);
        getViewModel().proposalId = proposalId;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.titleBar.setLeftTv(v -> {
            finish();
        }).setMiddleTv(R.string.vote_detial_title, R.color.default_titlebar_title_color);

        mHeaderBinding = ActivityClusterVoteDetailHeaderBinding.inflate(LayoutInflater.from(this));

        mBinding.refresh.setOnRefreshListener(this);
        mAdapter = new ClusterVoteDetailAdapter(new ArrayList<>());
        mAdapter.addHeaderView(mHeaderBinding.getRoot());
        mBinding.rvList.setAdapter(mAdapter);

        
        mBinding.btnVoteDetail.setOnClickListener(v -> showVoteDialog());
    }

    @Override
    public void initData() {
        super.initData();

        getViewModel().loadDetailData();
        getViewModel().observe(viewModel.mHeaderLiveData, this::setHeaderData);

        getViewModel().loadVoteData();
        getViewModel().observe(viewModel.mutableLiveData, this::dealVotePer);
        getViewModel().getClusterVoteAllPersonDetail();
        getViewModel().observe(viewModel.mLiveData, voteEntity -> {
            mAdapter.setNewData(voteEntity);

            if (null == voteEntity) return;
            
            String address = ChatStatusProvide.getAddress(getApplication());
            
            for (EvmosClusterPersonVoteBean.Data.VoteEntity vote : voteEntity) {
                if (vote.voter.equals(address)) {
                    isVoter = true;
                    mBinding.btnVoteDetail.setEnabled(false);
                    break;
                }
            }
        });
    }

    
    @Override
    public void onRefresh() {
        getViewModel().loadDetailData();
        getViewModel().getClusterVoteAllPersonDetail();
    }

    @SuppressLint("SetTextI18n")
    private void setHeaderData(EvmosClusterVoteDetailBean.Data data) {
        mBinding.refresh.setRefreshing(false);
        mHeaderBinding.voteDetailId.setText(data.id + "");
        mHeaderBinding.voteDetailAuthor.setText(data.proposers.get(0));
        mHeaderBinding.voteDetailTime.setText(TimeUtils.format(data.submit_time));
        mHeaderBinding.voteDetailType.setText("--");
        mHeaderBinding.voteDetailTimeVote.setText(TimeUtils.format(data.submit_time) + " - " + TimeUtils.format(data.voting_period_end));

        EvmosClusterVoteDetailBean.Data.Metadata metadata = new Gson().fromJson(data.metadata, EvmosClusterVoteDetailBean.Data.Metadata.class);
        mHeaderBinding.voteDetailTitle.setText(metadata.title);
        mHeaderBinding.voteDetailDesc.setText(metadata.description);

        ClusterVoteDetailItemAdapter adapter = new ClusterVoteDetailItemAdapter(data.messages);
        mHeaderBinding.voteDetailUpdate.setAdapter(adapter);

        String status = data.status + "";
        if (TextUtils.equals(status, "0") ) {
            
            mBinding.btnVoteDetail.setEnabled(false);
            mBinding.btnVoteDetail.setText(R.string.vote_detial_bt_tips_un_begin);
        } else if (TextUtils.equals(status, "1")) {
            
            mBinding.btnVoteDetail.setText(getString(R.string.vote_detial_bt_tips));
            mBinding.btnVoteDetail.setEnabled(true);
        } else if (TextUtils.equals(status, "2")) {
            
            mBinding.btnVoteDetail.setText(getString(R.string.vote_detial_bt_tips));
            mBinding.btnVoteDetail.setEnabled(false);
        } else {
            
            mBinding.btnVoteDetail.setText(R.string.vote_detial_bt_tips_over);
            mBinding.btnVoteDetail.setEnabled(false);
        }

        
        if (isVoter) {
            mBinding.btnVoteDetail.setEnabled(false);
        }

        
        EvmosClusterVoteDetailBean.Data.FinalTallyResultEntity tallyResult = data.final_tally_result;
    }

    private void dealVotePer(EvmosClusterVoteInfoBean.Data.FinalTallyResultEntity tally) {
        BigDecimal yes = new BigDecimal("0");
        BigDecimal abstain = new BigDecimal("0");
        BigDecimal no = new BigDecimal("0");
        BigDecimal no_with_veto = new BigDecimal("0");
        BigDecimal all = new BigDecimal("0");
        if (null != tally) {
            if (!TextUtils.isEmpty(tally.yes_count)) {
                yes = new BigDecimal(tally.yes_count);
            }
            if (!TextUtils.isEmpty(tally.abstain_count)) {
                abstain = new BigDecimal(tally.abstain_count);
            }
            if (!TextUtils.isEmpty(tally.no_count)) {
                no = new BigDecimal(tally.no_count);
            }
            if (!TextUtils.isEmpty(tally.no_with_veto_count)) {
                no_with_veto = new BigDecimal(tally.no_with_veto_count);
            }

            all = yes.add(no).add(abstain).add(no_with_veto);
        }

        
        mHeaderBinding.voteDetailVote1.setText(getPer(yes, all));
        mHeaderBinding.voteDetailVote2.setText(getPer(no, all));
        mHeaderBinding.voteDetailVote3.setText(getPer(no_with_veto, all));
        mHeaderBinding.voteDetailVote4.setText(getPer(abstain, all));
        
        ViewGroup.LayoutParams params = mHeaderBinding.voteDetailVote1.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetailVote2.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetailVote3.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        params = mHeaderBinding.voteDetailVote4.getLayoutParams();
        params.width = DisplayUtils.dp2px(this, 50);
        BigDecimal finalAll = all;
        BigDecimal finalYes = yes;
        BigDecimal finalAbstain = abstain;
        BigDecimal finalNo_with_veto = no_with_veto;
        BigDecimal finalNo = no;
        mHeaderBinding.voteDetailVoteParent.post(() -> {
            int canUseSize = mHeaderBinding.voteDetailVoteParent.getWidth() - DisplayUtils.dp2px(this, 15) * 2;

            
            canUseSize = canUseSize - DisplayUtils.dp2px(this, 50) * 4;

            
            ViewGroup.LayoutParams paramsTemp = mHeaderBinding.voteDetailVote1.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalYes, finalAll));
            mHeaderBinding.voteDetailVote1.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetailVote2.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalNo, finalAll));
            mHeaderBinding.voteDetailVote2.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetailVote3.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalNo_with_veto, finalAll));
            mHeaderBinding.voteDetailVote3.setLayoutParams(paramsTemp);
            paramsTemp = mHeaderBinding.voteDetailVote4.getLayoutParams();
            paramsTemp.width = (int) (DisplayUtils.dp2px(this, 50) + canUseSize * getPer2(finalAbstain, finalAll));
            mHeaderBinding.voteDetailVote4.setLayoutParams(paramsTemp);

        });
    }

    public float getPer2(BigDecimal in, BigDecimal all) {
        if (new BigDecimal("0").equals(all)) {
            return 0.25f;
        }
        if (new BigDecimal("0").equals(in)) {
            return 0;
        }
        try {
            return in.divide(all, 2, RoundingMode.DOWN).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getPer(BigDecimal in, BigDecimal all) {
        if (new BigDecimal("0").equals(all)) {
            return "0%";
        }
        if (new BigDecimal("0").equals(in)) {
            return "0%";
        }
        try {
            return in.multiply(new BigDecimal("100")).divide(all, 0, RoundingMode.DOWN) + "%";
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }

    private void showVoteDialog() {
        VoteDetialDialogFragment dialog = new VoteDetialDialogFragment();
        dialog.show(getSupportFragmentManager(), dialog.getTag());
        
        String address = ChatStatusProvide.getAddress(getApplication());
        dialog.setIConsume(type -> {
            DposVoteParam param = new DposVoteParam();
            if (type == 1) param.option2 = "VOTE_OPTION_YES";
            else if (type == 2) param.option2 = "VOTE_OPTION_NO";
            else if (type == 3) param.option2 = "VOTE_OPTION_NO_WITH_VETO";
            else if (type == 4) param.option2 = "VOTE_OPTION_ABSTAIN";

            param.option = type;
            
            param.proposal_id = proposalId;
            
            param.voter = address;

            
            getViewModel().vote(this, param);

        });
    }
}
