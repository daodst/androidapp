package com.app.home.ui.vote.detial.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemVoteDetailListBinding;
import com.app.home.pojo.VoteInfoDetialListWapper;

import java.util.List;


public class VoteDetialAdapter extends RecyclerView.Adapter<VoteDetialAdapter.ViewHolder> {


    private List<VoteInfoDetialListWapper.VoteInfoDetialList> mLists;

    public void setLists(List<VoteInfoDetialListWapper.VoteInfoDetialList> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vote_detail_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ItemVoteDetailListBinding view = holder.mBinding;

        VoteInfoDetialListWapper.VoteInfoDetialList list = mLists.get(position);
        view.itemVoteDetialAddress.setText(list.voter);
        if (null != list.options && list.options.size() > 0) {
            view.itemVoteDetialSelect.setText(list.options.get(0).getOption(context));
            if (TextUtils.equals(list.options.get(0).option, "1")) {
                view.itemVoteDetialSelect.setTextColor(ContextCompat.getColor(context, R.color.item_vote_detial_select_yes));
            } else {
                view.itemVoteDetialSelect.setTextColor(ContextCompat.getColor(context, R.color.item_vote_detial_select_no));
            }
        } else {
            view.itemVoteDetialSelect.setText("--");
            view.itemVoteDetialSelect.setTextColor(ContextCompat.getColor(context, R.color.item_vote_detial_select_yes));

        }


        if (position == getItemCount() - 1) {
            view.itemVoteDetialLine.setVisibility(View.INVISIBLE);
        } else {
            view.itemVoteDetialLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == mLists ? 0 : mLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVoteDetailListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemVoteDetailListBinding.bind(view);

        }
    }
}
