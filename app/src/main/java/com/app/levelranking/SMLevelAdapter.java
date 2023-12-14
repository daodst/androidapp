

package com.app.levelranking;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.AppHolder;
import com.wallet.ctc.R;
import com.wallet.ctc.model.me.SMLevelEntity;

import java.util.List;

import common.app.utils.DisplayUtils;
import common.app.utils.GlideUtil;
import im.vector.app.provide.ChatStatusProvide;


public class SMLevelAdapter extends BaseQuickAdapter<SMLevelEntity, BaseViewHolder> {
    public SMLevelAdapter(@Nullable List<SMLevelEntity> data) {
        super(R.layout.sm_activity_level_ranking_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SMLevelEntity item) {
        ImageView logo = (ImageView) helper.getView(R.id.civ_logo);

        if (!TextUtils.isEmpty(item.avatarUrl)) {
            try {
                ChatStatusProvide.showAvatarRenderer(mContext, item.userId, item.displayName, item.avatarUrl, logo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String defaultLogoRes = AppHolder.getLogoByAddress(item.facc);
            GlideUtil.showImg(mContext, defaultLogoRes, logo);
        }


        String did = "";
        if (TextUtils.isEmpty(item.idi)) {
            did = "";
        } else {
            did = item.idi;
        }
        String displayName = "";
        if (!TextUtils.isEmpty(item.displayName)) {
            displayName = item.displayName;
        } else {
            displayName = item.getMiniAddr();
        }
        helper.setText(R.id.tv_level, item.ranking)
                .setText(R.id.tv_username, displayName)
                .setText(R.id.tv_user_level, "LV." + item.pledge_level)
                .setText(R.id.tv_idi, "DID: " + did);
        if (TextUtils.isEmpty(did)) {
            helper.getView(R.id.tv_idi).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_idi).setVisibility(View.VISIBLE);
        }

        Drawable drawable;
        TextView tv_username = (TextView) helper.getView(R.id.tv_username);
        drawable = ContextCompat.getDrawable(mContext, getLevelDrawableRes(mContext, item.pledge_level));
        if (null != drawable) {
            drawable.setBounds(0, 0, DisplayUtils.dp2px(mContext, 18), DisplayUtils.dp2px(mContext, 23));
        }
        tv_username.setCompoundDrawables(null, null, drawable, null);
        TextView title = (TextView) helper.getView(R.id.tv_level);
        if ("1".equals(item.ranking)) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.sm_icon_level_ranking_jin);
            title.setText("");
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        } else if ("2".equals(item.ranking)) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.sm_icon_level_ranking_yin);
            title.setText("");
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        } else if ("3".equals(item.ranking)) {
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.sm_icon_level_ranking_tong);
            title.setText("");
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    private int getLevelDrawableRes(Context context, int level) {
        String resName = "sm_pledge" + level;
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(resName, "mipmap", packageName);
    }

}
