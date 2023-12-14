package com.app.home.ui.ver.detial.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.home.pojo.PledgedTokensDidListEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class PledgedTokensAdapter extends BaseQuickAdapter<PledgedTokensDidListEntity, BaseViewHolder> {
    public PledgedTokensAdapter(@Nullable List<PledgedTokensDidListEntity> data) {
        super(R.layout.dialog_pledged_token_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PledgedTokensDidListEntity item) {
        ImageView imageView = helper.getView(R.id.ivCheckBox);
        if (item.canSelect) {
            imageView.setImageResource(item.selected ? R.mipmap.icon_pledge_check : R.mipmap.icon_pledge_uncheck);
        } else imageView.setImageResource(R.mipmap.icon_pledge_unable);
        if (item.isDefaultSegment) {
            helper.setText(R.id.tvDidNumber, item.number + mContext.getString(R.string.redeem_tips_1));
        }else {
            helper.setText(R.id.tvDidNumber, item.number);
        }
    }
}
