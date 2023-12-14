package com.app.store;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import common.app.utils.GlideUtil;


public class DAppStoreAdapter extends BaseQuickAdapter<DAppStoreEntity, BaseViewHolder> {
    public DAppStoreAdapter(@Nullable List<DAppStoreEntity> data) {
        super(R.layout.activity_dapp_store_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DAppStoreEntity item) {
        helper.setText(R.id.tvTitle, item.appName)
                .setText(R.id.tvDesc, item.appDeveloper);
        ImageView imageView = helper.getView(R.id.imageLogo);
        GlideUtil.showImg(mContext, item.appLogo, imageView);

        TextView tvInstall = helper.getView(R.id.tvInstalled);
        Drawable download = ContextCompat.getDrawable(mContext, R.mipmap.icon_appstore_download);
        Drawable install = ContextCompat.getDrawable(mContext, R.mipmap.icon_appstore_install);
        

        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(item.appPackagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != packageInfo) {
            
            tvInstall.setCompoundDrawablesRelativeWithIntrinsicBounds(install, null, null, null);
            tvInstall.setText(R.string.app_store_installed);
        } else {
            tvInstall.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            tvInstall.setText("");
        }

    }
}
