

package com.app.lg4e.ui.fragment.splash;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.view.CustomVideoView;

import butterknife.BindView;
import common.app.ActivityRouter;
import common.app.base.base.BaseFragment;
import common.app.utils.AppVerUtil;



public class SplashFragment extends BaseFragment<SplashContract.Presenter> implements SplashContract.View {


    private static final String TAG = "SplashFragment";

    @BindView(R.id.splash_video)
    CustomVideoView videoview;
    private int config = 1;

    private AppVerUtil mAppVerUtil;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        
        initVideo(view);
        return view;
    }


    
    private void initVideo(View view) {
        videoview = view.findViewById(R.id.splash_video);
        videoview.setVisibility(View.VISIBLE);
        config = 0;
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int densityDPI = dm.densityDpi;     
        
        if (screenHeight > 2160) {
            videoview.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.splash_all));
        } else {
            videoview.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.splash));
        }
        
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            
                            videoview.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        return false;
                    }
                });

            }
        });
        videoview.setOnCompletionListener(v -> {
            nextJump();
        });

        mHandler = new Handler();
        
        mHandler.postDelayed(() -> {
            toMainTab();
        }, 15000);
    }

    @Override
    protected void initViews() {
        if (mActivity == null) {
            return;
        }
        new SplashPresenter(this, mActivity);
        checkVer();
    }


    @Override
    public void toLogInFragment() {
        config = 2;
    }

    @Override
    public void toMainTab() {
        if (config == 1) {
            mActivity.startActivity(ActivityRouter.getMainActivityIntent(mActivity));
            mActivity.finish();
        }
        config = 3;
    }


    
    private void checkVer() {
        

        toMainTab();


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.unsubscribe();
        }
        if (null != mAppVerUtil) {
            mAppVerUtil.cancel();
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        System.gc();
    }

    private void nextJump() {
        if (config == 2) {
            config = 1;
            toLogInFragment();
            return;
        } else if (config == 3) {
            config = 1;
            toMainTab();
            return;
        }
        config = 1;
    }

}
