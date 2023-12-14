package com.app.home.ui.ver.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemWalletVeryListListBinding;
import com.app.home.pojo.ValidatorListInfo;
import com.app.home.ui.utils.TimeUtils;
import com.app.home.ui.ver.detial.VWalletVeryActivity;

import java.util.List;


public class VWalletVeryListAdapter extends RecyclerView.Adapter<VWalletVeryListAdapter.ViewHolder> {


    private static final String TAG = "VWalletVeryListAdapter";
    private List<ValidatorListInfo.Result> mResults;

    private int decimal;

    public VWalletVeryListAdapter(int decimal) {
        this.decimal = decimal;
    }

    public void setResults(List<ValidatorListInfo.Result> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_wallet_very_list_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();
        ValidatorListInfo.Result result = mResults.get(position);
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(VWalletVeryActivity.getIntent(context, result.operator_address));
        });


        ItemWalletVeryListListBinding view = holder.mBinding;
        view.walletVeryListIndex.setText("[" + (position + 1) + "]");
        if (null != result.description) {
            view.walletVeryListTitle.setText(result.description.getMoniker());
        } else {
            view.walletVeryListTitle.setText("");
        }
        view.walletVeryListStatus.setText(result.getStatus(context));
        view.walletVeryListNum.setText(result.getDelegator_shares(decimal));
       view.walletVeryListTime.setText(TimeUtils.getTime(context.getApplicationContext(), result.start_time));
    }

    @Override
    public int getItemCount() {
        return null == mResults ? 0 : mResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWalletVeryListListBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemWalletVeryListListBinding.bind(view);

        }
    }
}
