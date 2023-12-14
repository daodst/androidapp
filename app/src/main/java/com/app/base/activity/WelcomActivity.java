

package com.app.base.activity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

import android.os.Bundle;

import com.app.R;
import com.app.lg4e.ui.fragment.splash.SplashFragment;

import common.app.base.base.BaseActivity;
import common.app.base.base.BaseFragment;
import common.app.base.them.Eyes;
import common.app.utils.LogUtil;



public class WelcomActivity extends BaseActivity {

    private static final String TAG = "ContentActivity";

    
    private BaseFragment mBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE
                | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Eyes.setTranslucent(this);

        setView(R.layout.activity_welcom);
        mBaseFragment = setFragment(SplashFragment.class.getName());
        
    }

    @Override
    public void onBackPressed() {
        if (null != mBaseFragment) {
            mBaseFragment.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "WelcomActivity: onResume");
    }


}
