package com.app.view.groupinformation.vote.votedetail;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ItemVoteDetailListBinding;
import com.app.home.ui.utils.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;

import java.util.List;


public class ClusterVoteDetailAdapter extends BaseQuickAdapter<EvmosClusterPersonVoteBean.Data.VoteEntity, ClusterVoteDetailAdapter.ClusterVoteDetailVH> {

    
    public ClusterVoteDetailAdapter(@Nullable List<EvmosClusterPersonVoteBean.Data.VoteEntity> data) {
        super(R.layout.item_vote_detail_list, data);
    }

    
    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull ClusterVoteDetailVH helper, EvmosClusterPersonVoteBean.Data.VoteEntity item) {
        String builder = item.voter.substring(0, 10) +
                "..." +
                item.voter.substring(item.voter.length() - 10);
        helper.mBinding.itemVoteDetialAddress.setText(builder);
        String option = mContext.getString(R.string.vote_detial_approve);
        if (item.option == 1) {
            option = mContext.getString(R.string.vote_detial_approve);
        } else if (item.option == 2) {
            option = mContext.getString(R.string.vote_detial_against);
        } else if (item.option == 3) {
            option = mContext.getString(R.string.vote_detial_disapprove);
        } else if (item.option == 4) {
            option = mContext.getString(R.string.vote_detial_give_up);
        }
        helper.mBinding.itemVoteDetialSelect.setText(option);
        helper.mBinding.itemVoteDetialTime.setVisibility(View.VISIBLE);
        helper.mBinding.itemVoteDetialTime.setText(mContext.getString(R.string.online_wallet_time) + TimeUtils.format2(item.submitTime));
    }

    static class ClusterVoteDetailVH extends BaseViewHolder {
        public ItemVoteDetailListBinding mBinding;

        public ClusterVoteDetailVH(View view) {
            super(view);
            mBinding = ItemVoteDetailListBinding.bind(view);
        }
    }
}
