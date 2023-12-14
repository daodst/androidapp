package com.app.view.groupinformation.vote.votelist;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ActivityGovernancePoolVoteListItemBinding;
import com.app.home.ui.utils.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;

import java.util.List;

import im.vector.app.provide.ChatStatusProvide;


public class VoteListAdapter extends BaseQuickAdapter<EvmosClusterVoteBean.Data, VoteListAdapter.VoteListViewHolder> {
    private String groupId;

    
    public VoteListAdapter(@Nullable List<EvmosClusterVoteBean.Data> data, String groupId) {
        super(R.layout.activity_governance_pool_vote_list_item, data);
        this.groupId = groupId;
    }

    
    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull VoteListViewHolder helper, EvmosClusterVoteBean.Data item) {
        ActivityGovernancePoolVoteListItemBinding binding = helper.mBinding;

        EvmosClusterVoteBean.Data.Metadata metadata = new Gson().fromJson(item.metadata, EvmosClusterVoteBean.Data.Metadata.class);
        binding.voteListContent.setText( metadata.title);
        
        binding.voteListTime.setText(TimeUtils.format(item.submit_time) + " - " + TimeUtils.format(item.voting_period_end));
        binding.originatingAddress.setText(item.proposers.get(0));

        
        try {
            
            String nickStr = ChatStatusProvide.getOtherUserInfo(mContext, item.proposers.get(0), groupId);
            int identity = ChatStatusProvide.getOtherUserIdentity(mContext, item.proposers.get(0), groupId);
            String idStr = "--";
            if (identity == 0) idStr = mContext.getString(R.string.vote_list_title);
            else if (identity == 2) idStr = mContext.getString(R.string.vote_list_member);
            String nickName =mContext.getString(R.string.vote_list_name)+ ":<font color='#111111'>" + nickStr + "</font><font color='#0BBD8B'>(" + idStr + ")</font>";
            binding.originatingNick.setText(Html.fromHtml(nickName, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (item.status) {
            case 1://"PROPOSAL_STATUS_SUBMITTED":
                binding.voteListStatus.setText(R.string.vote_list_status_ing);
                binding.voteListStatus.setTextColor(ContextCompat.getColor(mContext, R.color.v_home_list_status_ing2));
                break;
            case 2://"PROPOSAL_STATUS_ACCEPTED":
                binding.voteListStatus.setText(R.string.vote_list_status_accept);
                binding.voteListStatus.setTextColor(ContextCompat.getColor(mContext, R.color.item_vote_detial_select_yes));
                break;
            case 3://"PROPOSAL_STATUS_REJECTED":
                binding.voteListStatus.setText(R.string.vote_list_status_reject);
                binding.voteListStatus.setTextColor(ContextCompat.getColor(mContext, R.color.item_vote_detial_select_no));
                break;
            case 4://"PROPOSAL_STATUS_ABORTED":
                binding.voteListStatus.setText(R.string.vote_list_status_end);
                binding.voteListStatus.setTextColor(ContextCompat.getColor(mContext, R.color.v_home_list_status_ed));
                break;
        }
    }

    private SpannableString extracted(int padding, String description) {
        SpannableString spannableString = new SpannableString(description);
        
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(padding, 0);
        spannableString.setSpan(leadingMarginSpan, 0, description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    static class VoteListViewHolder extends BaseViewHolder {
        public ActivityGovernancePoolVoteListItemBinding mBinding;

        public VoteListViewHolder(View view) {
            super(view);
            mBinding = ActivityGovernancePoolVoteListItemBinding.bind(view);
        }
    }
}
