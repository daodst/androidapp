

package com.app.levelranking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.me.destory.DestoryActivity;
import com.wallet.ctc.databinding.SmActivityLevelRankingBinding;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.DisplayUtils;
import im.vector.app.provide.ChatStatusProvide;


public class SMLevelRankingActivity extends BaseActivity<SMLevelRankingActivityVM> {
    private SmActivityLevelRankingBinding mBinding;
    private SMLevelAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = SmActivityLevelRankingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
            Eyes.setStatusBarTextColor(this, false);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.ivBack.getLayoutParams();
            params.topMargin = Eyes.getStatusBarHeight(this);
        }

        mBinding.ivBack.setOnClickListener(v -> finish());

        RecyclerView recyclerView = mBinding.rvList;
        mAdapter = new SMLevelAdapter(new ArrayList<>());
        recyclerView.setAdapter(mAdapter);

        
        mBinding.btnZhiya.setOnClickListener(v -> {
            
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(this));
            startActivity(DestoryActivity.getIntent(this, address));
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {

        getViewModel().observe(viewModel.getMError(), aBool -> {
            hidePb();
        });
        
        getViewModel().observe(viewModel.getMData(), smLevelEntities -> {
            hidePb();
            mAdapter.setNewData(smLevelEntities);
        });
        
        getViewModel().observe(viewModel.getMUserLevel(), entity -> {
            hidePb();
            ChatStatusProvide.showAvatarRenderer(this, entity.userId, entity.displayName, entity.avatarUrl, mBinding.civLogo);
            mBinding.tvUsername.setText(entity.displayName);
            mBinding.tvUserLevel.setText("LV." + entity.pledge_level);
            mBinding.tvUserLevel.setVisibility(View.VISIBLE);
            mBinding.tvIdi.setText("DID: " + entity.idi);
            if (TextUtils.isEmpty(entity.idi)) {
                mBinding.tvIdi.setVisibility(View.GONE);
            } else {
                mBinding.tvIdi.setVisibility(View.VISIBLE);
            }

            if ("1".equals(entity.ranking)) {
                Drawable drawable = ContextCompat.getDrawable(this, com.wallet.ctc.R.mipmap.sm_icon_level_ranking_jin);
                mBinding.tvMyLevel.setText("");
                mBinding.tvMyLevel.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            } else if ("2".equals(entity.ranking)) {
                Drawable drawable = ContextCompat.getDrawable(this, com.wallet.ctc.R.mipmap.sm_icon_level_ranking_yin);
                mBinding.tvMyLevel.setText("");
                mBinding.tvMyLevel.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            } else if ("3".equals(entity.ranking)) {
                Drawable drawable = ContextCompat.getDrawable(this, com.wallet.ctc.R.mipmap.sm_icon_level_ranking_tong);
                mBinding.tvMyLevel.setText("");
                mBinding.tvMyLevel.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            } else {
                mBinding.tvMyLevel.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                mBinding.tvMyLevel.setText(entity.ranking);
            }
            Drawable drawable = ContextCompat.getDrawable(this, getLevelDrawableRes(this, entity.pledge_level));
            if (null != drawable) {
                drawable.setBounds(0, 0, DisplayUtils.dp2px(this, 18), DisplayUtils.dp2px(this, 23));
            }
            mBinding.tvUsername.setCompoundDrawables(null, null, drawable, null);
        });
        showPb();
        getViewModel().getPledgeRank();
    }

    private int getLevelDrawableRes(Context context, int level) {
        String resName = "sm_pledge" + level;
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(resName, "mipmap", packageName);
    }

    private MyProgressDialog mProgressDialog;

    private void showPb() {
        if (null == mProgressDialog) {
            mProgressDialog = new MyProgressDialog(this, "");
        }
        mProgressDialog.show();
    }

    private void hidePb() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }
}
