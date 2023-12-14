

package com.wallet.ctc.ui.blockchain.importwallet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletMnemonicAdapter extends RecyclerView.Adapter<WalletMnemonicAdapter.ViewHolder> {


    private List<String> mStringList;

    public void setStringList(List<String> stringList) {
        mStringList = stringList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.j_wallet_mnemonic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String data = mStringList.get(i);
        holder.mJWalletMnemonicTv.setText(data);
        holder.mJWalletMnemonicTv.setOnClickListener(v -> {
            if (null != mIClick) {
                mIClick.set(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mStringList ? 0 : mStringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.j_wallet_mnemonic_tv)
        TextView mJWalletMnemonicTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private IClick mIClick;

    public void setIClick(IClick IClick) {
        mIClick = IClick;
    }

    public interface IClick {
        void set(String data);
    }
}
