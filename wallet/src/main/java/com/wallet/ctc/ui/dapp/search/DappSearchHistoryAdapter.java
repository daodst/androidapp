package com.wallet.ctc.ui.dapp.search;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.DappHistoryEntity;
import com.wallet.ctc.nft.adapter.SkyAdapter;
import com.wallet.ctc.nft.adapter.SkyHolder;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

public class DappSearchHistoryAdapter extends SkyAdapter<DappHistoryEntity, DappSearchHistoryAdapter.ViewHolder> {

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_browse_history, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends SkyHolder<DappHistoryEntity> {
        @BindView(R2.id.iv_logo)
        ImageView ivLogo;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_url)
        TextView tvUrl;
        @BindView(R2.id.collected_iv)
        ImageView collectedIv;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(DappHistoryEntity data, int position) {
            super.bindData(data, position);
            tvTitle.setText(TextUtils.isEmpty(data.title) ? mContext.getString(R.string.js_un_known) : data.title);
            tvUrl.setText(data.url);
            if (data.isLike == 1) {
                collectedIv.setVisibility(View.VISIBLE);
            } else {
                collectedIv.setVisibility(View.GONE);
            }
            Glide.with(mContext).load(data.iconPath).placeholder(R.mipmap.js_ic_bowse_web_icon_default).error(R.mipmap.js_ic_bowse_web_icon_default).into(ivLogo);
        }
    }
}
