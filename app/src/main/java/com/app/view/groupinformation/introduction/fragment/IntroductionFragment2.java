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

import com.app.databinding.FragmentIntroduction2Binding;
import com.app.view.groupinformation.introduction.IntroductionPageVM;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import common.app.base.BaseFragment;


public class IntroductionFragment2 extends BaseFragment<IntroductionPageVM> {
    private FragmentIntroduction2Binding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentIntroduction2Binding.inflate(inflater, container, false);
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
        mBinding.tvTitle.setText("DAO");
        mBinding.tvSubTitle.setText("");
        String dvm = "DST<font color='#0BBD8B'>DVM</font>";
        mBinding.tvDvm.setText(Html.fromHtml(dvm, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        mBinding.tvDesc.setText("，DVM GAS");
    }

    
    private void startApngAnimation(ImageView view) {
        AssetStreamLoader assetLoader = new AssetStreamLoader(mContext, "create_group_apng_02.png");
        
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        
        view.setImageDrawable(apngDrawable);
        
        apngDrawable.setLoopLimit(0);
    }
}
