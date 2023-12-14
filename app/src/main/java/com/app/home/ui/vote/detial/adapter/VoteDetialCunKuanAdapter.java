package com.app.home.ui.vote.detial.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemVoteDetialCunkuanListBinding;
import com.app.home.pojo.VoteDetial;

import java.util.List;

public class VoteDetialCunKuanAdapter extends RecyclerView.Adapter<VoteDetialCunKuanAdapter.ViewHolder> {

    private List<VoteDetial.Deposits> mDeposits;


    private int decimal;

    public VoteDetialCunKuanAdapter(int decimal) {
        this.decimal = decimal;
    }

    public void setDeposits(List<VoteDetial.Deposits> deposits) {
        mDeposits = deposits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vote_detial_cunkuan_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ItemVoteDetialCunkuanListBinding view = holder.mBinding;
        VoteDetial.Deposits deposits = mDeposits.get(position);
        view.itemVoteDetialCnAddress.setText(deposits.depositor);
        if (null != deposits.amount && deposits.amount.size() > 0) {
            VoteDetial.DepositAmount amount = deposits.amount.get(0);
            view.itemVoteDetialCnBalance.setText(amount.getAmount(decimal) + " " + amount.denom);
        } else {
            view.itemVoteDetialCnBalance.setText("--");
        }
        if (position == getItemCount() - 1) {
            view.itemVoteDetialLine.setVisibility(View.INVISIBLE);
        } else {
            view.itemVoteDetialLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == mDeposits ? 0 : mDeposits.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemVoteDetialCunkuanListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemVoteDetialCunkuanListBinding.bind(view);

        }
    }
}
