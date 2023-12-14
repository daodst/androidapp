package com.app.view.groupinformation.introduction;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.databinding.ActivityIntroductionPageBinding;
import com.app.view.groupinformation.introduction.adapter.ScreenSlidePagerAdapter;
import com.app.view.groupinformation.introduction.fragment.IntroductionFragment1;
import com.app.view.groupinformation.introduction.fragment.IntroductionFragment2;
import com.app.view.groupinformation.introduction.fragment.IntroductionFragment3;
import com.app.view.groupinformation.introduction.fragment.IntroductionFragment4;

import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;


public class IntroductionPageActivity extends BaseActivity<IntroductionPageVM> {
    private ActivityIntroductionPageBinding mBinding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityIntroductionPageBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        Eyes.setTranslucent(this);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new IntroductionFragment1());
        fragments.add(new IntroductionFragment2());
        fragments.add(new IntroductionFragment3());
        fragments.add(new IntroductionFragment4());
        mBinding.viewPager2.setAdapter(new ScreenSlidePagerAdapter(this, fragments));
    }

    @Override
    public void initData() {
        super.initData();
    }

    public void setCurrent(int position) {
        try {
            mBinding.viewPager2.setCurrentItem(position);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }
}
