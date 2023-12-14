

package com.wallet.ctc.nft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.NftBean;

import butterknife.BindView;
import common.app.my.view.CircularImage;

public class WalletHomeNftAdapter extends SkyAdapter<NftBean, WalletHomeNftAdapter.AssetsHolder> {

    @NonNull
    @Override
    public AssetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_nft, parent, false);
        return new AssetsHolder(v);
    }

    public static class AssetsHolder extends SkyHolder<NftBean> {

        @BindView(R2.id.iv_logo)
        CircularImage ivLogo;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.tv_amount)
        TextView tvAmount;
        @BindView(R2.id.v_bottom)
        View vBottom;

        public AssetsHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(NftBean data, int position) {
            super.bindData(data, position);
            tvName.setText(data.name);
            tvAmount.setText(data.tokenCount + "");
        }
    }
}
