package com.app.home.ui.adapter;

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
import com.app.databinding.ItemVHomeListBinding;
import com.app.home.pojo.VoteInfo;
import com.app.home.ui.utils.TimeUtils;
import com.app.home.ui.vote.detial.VoteDetialActivity;

import java.util.List;

import common.app.utils.DisplayUtils;

public class VHomeItemAdapter extends RecyclerView.Adapter<VHomeItemAdapter.ViewHolder> {


    private List<VoteInfo> mVoteInfos;

    public void setVoteInfos(List<VoteInfo> voteInfos) {
        mVoteInfos = voteInfos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_v_home_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ItemVHomeListBinding view = holder.mBinding;
        Context context = view.getRoot().getContext();

        VoteInfo info = mVoteInfos.get(position);

        String description = info.getContent();

        view.vHomeListContent.setText(extracted(context, description));
        view.vHomeListTime.setText(TimeUtils.format(info.voting_start_time) + " - " + TimeUtils.format(info.voting_start_time));

        view.getRoot().setOnClickListener(v -> {
            context.startActivity(VoteDetialActivity.getIntent(context, info.proposal_id));
        });
        view.vHomeListStatus.setText(info.getStatus(context));
        if (info.isSuccess()) {
            view.vHomeListStatus.setTextColor(ContextCompat.getColor(context, R.color.v_home_list_status_ing));
        } else {
            view.vHomeListStatus.setTextColor(ContextCompat.getColor(context, R.color.v_home_list_status_ed));
        }
    }

    @Override
    public int getItemCount() {
        return null == mVoteInfos ? 0 : mVoteInfos.size();
    }

    private SpannableString extracted(Context context, String description) {
        SpannableString spannableString = new SpannableString(description);
        int marginSpanSize = DisplayUtils.dp2px(context, 45);
        
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(marginSpanSize, 0);
        spannableString.setSpan(leadingMarginSpan, 0, description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVHomeListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemVHomeListBinding.bind(view);
        }
    }


}
