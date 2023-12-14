package com.app.home.ui.ver.detial.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemWalletVeryListBinding;
import com.wallet.ctc.model.blockchain.ValidatorInfo;

import java.util.List;

public class WalletVeryItemAdapter extends RecyclerView.Adapter<WalletVeryItemAdapter.ViewHolder> {


    private List<ValidatorInfo.ValidatorInfoResult> mResults;

    private int decimal;

    public WalletVeryItemAdapter(int decimal) {
        this.decimal = decimal;
    }

    public void setResults(List<ValidatorInfo.ValidatorInfoResult> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_wallet_very_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        ItemWalletVeryListBinding view = holder.mBinding;

        ValidatorInfo.ValidatorInfoResult result = mResults.get(position);
        if (null != result.delegation) {
            view.itemWalletVeryAddress.setText(result.delegation.delegator_address);
        } else {
            view.itemWalletVeryAddress.setText("--");
        }
        ValidatorInfo.Balance balance = result.balance;
        if (null != balance) {
            view.itemWalletVeryBalance.setText(balance.getAmount(decimal) + " " + balance.denom);
        } else {
            view.itemWalletVeryBalance.setText("--");
        }

    }

    @Override
    public int getItemCount() {
        return null == mResults ? 0 : mResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWalletVeryListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemWalletVeryListBinding.bind(view);

        }
    }
}
