package com.app.home.ui.vote.detial.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemVoteDetialUpdateListBinding;
import com.app.home.pojo.IUpdateInfo;

import java.util.List;

public class VoteDetialUpdateAdapter extends RecyclerView.Adapter<VoteDetialUpdateAdapter.ViewHolder> {


    private List<IUpdateInfo> mInfos;

    public void setInfos(List<IUpdateInfo> infos) {
        mInfos = infos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vote_detial_update_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IUpdateInfo info = mInfos.get(position);
        Context context = holder.itemView.getContext();
        ItemVoteDetialUpdateListBinding view = holder.mBinding;
        view.itemVoteDetialUpdateMoudle.setText(info.getName());
        view.itemVoteDetialUpdateMoudle.setVisibility(TextUtils.isEmpty(info.getName()) ? View.GONE : View.VISIBLE);
        view.itemVoteDetialUpdateKey.setText(info.getKeyName(context) + ":" + info.getKey());
        view.itemVoteDetialUpdateValueName.setText(info.getValueName(context) + ":");
        view.itemVoteDetialUpdateValue.setText(info.getValue());
        if (position == getItemCount() - 1) {
            view.itemVoteDetialLine.setVisibility(View.INVISIBLE);
        } else {
            view.itemVoteDetialLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == mInfos ? 0 : mInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVoteDetialUpdateListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemVoteDetialUpdateListBinding.bind(view);

        }
    }
}
