package com.app.view.groupinformation.introduction.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.FragmentIntroduction4Binding;
import com.app.view.groupinformation.introduction.IntroductionPageVM;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import common.app.base.BaseFragment;
import im.vector.app.features.createdirect.cluster.ClusterActivity;


public class IntroductionFragment4 extends BaseFragment<IntroductionPageVM> {
    private FragmentIntroduction4Binding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentIntroduction4Binding.inflate(inflater, container, false);
        mView = mBinding.getRoot();
        return mView;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.btnCreate.setOnClickListener(v -> {
            if (null != getActivity()) {
                getActivity().finish();
                startActivity(ClusterActivity.Companion.newIntent(getActivity(), 1, null, null,null));
            }
        });

        
        int width = getActivity().getWindow().getDecorView().getWidth();
        int height = (int) (width / 1.29);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBinding.ivLogo.getLayoutParams();
        params.width = width;
        params.height = height;
        mBinding.ivLogo.setLayoutParams(params);
        startApngAnimation(mBinding.ivLogo);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initData() {
        super.initData();
        mBinding.tvTitle.setText("");
        mBinding.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.default_text_color));
        String subTitle = "<font color='#0BBD8B'></font>";
        mBinding.tvSubTitle.setText(Html.fromHtml(subTitle, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        String title = "<font color='#0BBD8B'></font>，";
        mBinding.tvDvm.setText(Html.fromHtml(title, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        String desc = "DAO<font color='#0BBD8B'></font><br/>" +
                "DAO";
        mBinding.tvDesc.setText(Html.fromHtml(desc, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        mBinding.btnCreate.setText("");
    }

    
    private void startApngAnimation(ImageView view) {
        AssetStreamLoader assetLoader = new AssetStreamLoader(mContext, "create_group_apng_04.png");
        
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        
        view.setImageDrawable(apngDrawable);
        
        apngDrawable.setLoopLimit(0);
    }
}
