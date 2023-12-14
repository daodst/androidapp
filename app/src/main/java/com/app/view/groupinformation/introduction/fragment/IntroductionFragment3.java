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

import com.app.databinding.FragmentIntroduction3Binding;
import com.app.view.groupinformation.introduction.IntroductionPageVM;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import common.app.base.BaseFragment;


public class IntroductionFragment3 extends BaseFragment<IntroductionPageVM> {
    private FragmentIntroduction3Binding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentIntroduction3Binding.inflate(inflater, container, false);
        mView = mBinding.getRoot();
        return mView;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        
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
        String title = "<font color='#0BBD8B'>DAO</font>";
        mBinding.tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        mBinding.tvSubTitle.setText("DAO、");
        String dvm = "<font color='#0BBD8B'>DAO</font>";
        mBinding.tvDvm.setText(Html.fromHtml(dvm, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        mBinding.tvDesc.setText("、、 DAO");
    }

    
    private void startApngAnimation(ImageView view) {
        AssetStreamLoader assetLoader = new AssetStreamLoader(mContext, "create_group_apng_03.png");
        
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        
        view.setImageDrawable(apngDrawable);
        
        apngDrawable.setLoopLimit(0);
    }
}
