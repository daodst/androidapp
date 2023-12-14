package com.app.store.detail;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import common.app.utils.GlideUtil;


public class DAppStoreDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public DAppStoreDetailAdapter(@Nullable List<String> data) {
        super(R.layout.activity_dapp_store_detail_app_info_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView imageView = helper.getView(R.id.imageAppSimple);
        GlideUtil.showImg(mContext, item, imageView);
    }
}
