package com.app.view.groupinformation.introduction.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.databinding.FragmentIntroduction1Binding;
import com.app.view.groupinformation.introduction.IntroductionPageActivity;
import com.app.view.groupinformation.introduction.IntroductionPageVM;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;

import common.app.base.BaseFragment;
import im.vector.app.features.createdirect.cluster.ClusterActivity;


public class IntroductionFragment1 extends BaseFragment<IntroductionPageVM> {
    private FragmentIntroduction1Binding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentIntroduction1Binding.inflate(inflater, container, false);
        mView = mBinding.getRoot();
        return mView;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.btnCreate.setOnClickListener(v -> {
            if (null != getActivity() && getActivity() instanceof IntroductionPageActivity) {
                IntroductionPageActivity activity = (IntroductionPageActivity) getActivity();
                activity.setCurrent(1);
            }
        });
        mBinding.tvJump.setOnClickListener(v -> {
            if (null != getActivity()) {
                getActivity().finish();
                startActivity(ClusterActivity.Companion.newIntent(getActivity(), 1, null, null,null));
            }
        });

        
        int width = getActivity().getWindow().getDecorView().getWidth();
        int height = (int) (width / 0.773);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.ivLogo.getLayoutParams();
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

        String title = "<br/>" +
                "<font color='#42BA7D'>DAO</font>";
        mBinding.tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));

        mBinding.btnCreate.setText("");
        mBinding.tvJump.setText("，");
    }

    
    private void startApngAnimation(ImageView view) {
        AssetStreamLoader assetLoader = new AssetStreamLoader(mContext, "create_group_apng_01.png");
        
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        
        view.setImageDrawable(apngDrawable);
        
        apngDrawable.setLoopLimit(1);
    }
}
