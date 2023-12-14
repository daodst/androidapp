package com.app.home.ui.vote.list.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemVoteListBinding;
import com.app.home.pojo.VoteInfo;
import com.app.home.ui.utils.TimeUtils;
import com.app.home.ui.vote.detial.VoteDetialActivity;

import java.util.List;

import common.app.utils.DisplayUtils;

public class VListItemAdapter extends RecyclerView.Adapter<VListItemAdapter.ViewHolder> {


    private List<VoteInfo> mVoteInfos;

    public void setVoteInfos(List<VoteInfo> voteInfos) {
        mVoteInfos = voteInfos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vote_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        VoteInfo info = mVoteInfos.get(position);
        ItemVoteListBinding view = holder.mBinding;
        String description = info.getContent();
        view.voteListLabel.post(() -> {
            int marginSpanSize = view.voteListLabel.getWidth() + DisplayUtils.dp2px(context, 5);
            view.voteListContent.setText(extracted(marginSpanSize, description));
        });

        view.voteListTime.setText(TimeUtils.format(info.voting_start_time)+" - "+TimeUtils.format(info.voting_start_time));

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(VoteDetialActivity.getIntent(context, info.proposal_id));
        });

        view.voteListStatus.setText(info.getStatus(context));
        if (info.isVoting()) {
            view.voteListStatus.setTextColor(ContextCompat.getColor(context, R.color.v_home_list_status_ing2));
        } else {
            view.voteListStatus.setTextColor(ContextCompat.getColor(context, R.color.v_home_list_status_ed));
        }
    }
    



    @Override
    public int getItemCount() {
        return null == mVoteInfos ? 0 : mVoteInfos.size();
    }

    private SpannableString extracted(int padding, String description) {
        SpannableString spannableString = new SpannableString(description);
        
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(padding, 0);
        spannableString.setSpan(leadingMarginSpan, 0, description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVoteListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemVoteListBinding.bind(view);
        }
    }
}
