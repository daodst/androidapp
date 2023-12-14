package com.app.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.app.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Deprecated
public class PledgeAwardDialogFragment extends DialogFragment {
    private Context mContext;
    private ConstraintLayout mRootView;
    private ImageView mImageBox, mImageCoin1, mImageCoin2, mImageWallet;
    private TextView mTvValue;
    private LinearLayout mllRoot;
    private TextView mTvShare;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    
    private float mAmount = 0.0f;
    
    private String mGroupName, mShareUrl;
    private Consumer<Void> mVoidConsumer;

    
    private long clickTime = 0L;

    private final List<AnimatorSet> mSetList = new ArrayList<>();
    private final List<MediaPlayer> mPlayerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_pledge_award, container, false);
        mContext = getContext();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(-1, -2);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        for (AnimatorSet set : mSetList) {
            if (set.isRunning()) set.end();
        }

        for (MediaPlayer player : mPlayerList) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
        }
    }

    public void setData(Float amount, String groupName, String shareUrl, Consumer<Void> consumer) {
        this.mAmount = amount;
        this.mGroupName = groupName;
        this.mShareUrl = shareUrl;
        this.mVoidConsumer = consumer;
    }

    private void initView(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRootView = view.findViewById(R.id.rootView);
        mImageBox = view.findViewById(R.id.imageBox);
        mImageCoin1 = view.findViewById(R.id.imageCoin1);
        mImageCoin2 = view.findViewById(R.id.imageCoin2);
        mImageWallet = view.findViewById(R.id.imageWallet);
        mTvValue = view.findViewById(R.id.tvValue);
        mTvShare = view.findViewById(R.id.tvShare);
        mllRoot = view.findViewById(R.id.llRoot);
        mProgressBar = view.findViewById(R.id.progressBar);

        mTextView = view.findViewById(R.id.textView);
        String text = "DST<font color='#04B485'>" + mGroupName + "</font>，DST，，POS，。<font color='#04B485'>" + mShareUrl + "</font> ";
        mTextView.setText(Html.fromHtml(text));

        
        onStartAnim();

        
        mTvShare.setOnClickListener(v -> {
            
            if (System.currentTimeMillis() - clickTime <= 2000) {
                clickTime = System.currentTimeMillis();
                return;
            }
            clickTime = System.currentTimeMillis();
            mProgressBar.setVisibility(View.VISIBLE);
            if (null != mVoidConsumer) mVoidConsumer.accept(null);
            
        });
    }

    
    private void onStartAnim() {
        @SuppressLint("Recycle") ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRootView, "rotationY", -180f, 0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRootView, "scaleX", 0.1f, 0.3f, 0.4f, 0.6f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mRootView, "scaleY", 0.1f, 0.3f, 0.4f, 0.6f, 1f);
        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(800);
        animatorSet.playTogether(animator1, animator2, animator3);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorStep2();
            }
        });
        animatorSet.start();
        MediaPlayer player = getMediaPlayer(R.raw.audio_4);
        player.start();
    }

    
    private void animatorStep2() {
        
        AnimatorSet animatorSet = getAnimatorSet(100, (ObjectAnimator) null);
        ObjectAnimator animatorTranslation = ObjectAnimator.ofFloat(mImageBox, "translationY", dip2px(-250f));
        animatorSet.setDuration(200);
        animatorSet.play(animatorTranslation);
        animatorTranslation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                
                animatorStep3(result -> {
                    
                    MediaPlayer player = getMediaPlayer(R.raw.audio_3);
                    player.start();
                    showCoinAnim();
                });
            }
        });
        animatorSet.start();
    }

    
    private void animatorStep3(Consumer<Boolean> consumer) {
        
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0000f, mAmount);
        valueAnimator.addUpdateListener(animation -> {
            String valueStr = "" + animation.getAnimatedValue();
            if (animation.getAnimatedValue() instanceof Float) {
                float value = (float) animation.getAnimatedValue();
                BigDecimal bigDecimal = BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
                valueStr = bigDecimal.toPlainString();
            }
            mTvValue.setText(valueStr);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                consumer.accept(true);
            }
        });
        AnimatorSet animatorSet = getAnimatorSet(800, valueAnimator);
        animatorSet.start();
        
        MediaPlayer player = getMediaPlayer(R.raw.audio_5);
        player.start();

    }

    
    private void showCoinAnim() {
        ObjectAnimator transOneX = ObjectAnimator.ofFloat(mImageCoin1, "translationX", 0f, dip2px(-30));
        ObjectAnimator transOneY = ObjectAnimator.ofFloat(mImageCoin1, "translationY", 0f, dip2px(-50));

        ObjectAnimator transTwoX = ObjectAnimator.ofFloat(mImageCoin2, "translationX", 0f, dip2px(15f));
        ObjectAnimator transTwoY = ObjectAnimator.ofFloat(mImageCoin2, "translationY", 0f, dip2px(-45f));

        ObjectAnimator animatorScale1 = ObjectAnimator.ofFloat(mImageCoin1, "scaleX", 1f, 4f);
        ObjectAnimator animatorScale2 = ObjectAnimator.ofFloat(mImageCoin1, "scaleY", 1f, 4f);
        ObjectAnimator animatorScale3 = ObjectAnimator.ofFloat(mImageCoin2, "scaleX", 1f, 4f);
        ObjectAnimator animatorScale4 = ObjectAnimator.ofFloat(mImageCoin2, "scaleY", 1f, 4f);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mTvShare, "alpha", 0f, 1f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.playTogether(transOneX, transOneY, transTwoX, transTwoY, alpha, animatorScale1, animatorScale2, animatorScale3, animatorScale4);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImageCoin1.setVisibility(View.VISIBLE);
                mImageCoin2.setVisibility(View.VISIBLE);
                mTvShare.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
                danceEffect();
            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    
    private void hideCoinAnim(AnimatorSet set, Animator afterAnim, Consumer<Boolean> consumer) {
        ObjectAnimator transOneX = ObjectAnimator.ofFloat(mImageCoin1, "translationX", dip2px(-10f));
        ObjectAnimator transOneY = ObjectAnimator.ofFloat(mImageCoin1, "translationY", dip2px(80f));

        ObjectAnimator transTwoX = ObjectAnimator.ofFloat(mImageCoin2, "translationX", dip2px(-10f));
        ObjectAnimator transTwoY = ObjectAnimator.ofFloat(mImageCoin2, "translationY", dip2px(30f));

        ObjectAnimator animatorScale1 = ObjectAnimator.ofFloat(mImageCoin1, "scaleX", 4f, 1f);
        ObjectAnimator animatorScale2 = ObjectAnimator.ofFloat(mImageCoin1, "scaleY", 4f, 1f);
        ObjectAnimator animatorScale3 = ObjectAnimator.ofFloat(mImageCoin2, "scaleX", 4f, 1f);
        ObjectAnimator animatorScale4 = ObjectAnimator.ofFloat(mImageCoin2, "scaleY", 4f, 1f);

        set.play(transOneX).with(transOneY).with(transTwoX).with(transTwoY).with(animatorScale1).with(animatorScale2).with(animatorScale3).with(animatorScale4).after(afterAnim);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animation.setDuration(500L);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
                MediaPlayer player = getMediaPlayer(R.raw.audio_2);
                player.start();
                
                mImageCoin1.setVisibility(View.INVISIBLE);
                mImageCoin2.setVisibility(View.INVISIBLE);

                
                consumer.accept(true);
            }
        });
    }

    
    public void shareOnClick() {
        mProgressBar.setVisibility(View.GONE);
        
        ObjectAnimator animatorScale1 = ObjectAnimator.ofFloat(mImageBox, "scaleX", 1f, 0f);
        ObjectAnimator animatorScale2 = ObjectAnimator.ofFloat(mImageBox, "scaleY", 1f, 0f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.playTogether(animatorScale1, animatorScale2);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                
                MediaPlayer player = getMediaPlayer(R.raw.audio_6);
                player.start();

                
                ObjectAnimator animatorTransWallet = ObjectAnimator.ofFloat(mImageWallet, "translationY", dip2px(-220f));
                
                AnimatorSet set = getAnimatorSet(500, animatorTransWallet);
                
                hideCoinAnim(set, animatorTransWallet, result -> closeDialog());
                animatorNumberCollapse(animatorTransWallet);
                set.start();
            }
        });
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    
    private void animatorNumberCollapse(Animator afterAnim) {
        
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mAmount, 0.0000f);
        valueAnimator.addUpdateListener(animation -> {
            String valueStr = "" + animation.getAnimatedValue();
            if (animation.getAnimatedValue() instanceof Float) {
                float value = (float) animation.getAnimatedValue();
                BigDecimal bigDecimal = BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
                valueStr = bigDecimal.toPlainString();
            }
            mTvValue.setText(valueStr);
        });

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mllRoot, "translationX", dip2px(150f));
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mllRoot, "translationY", dip2px(210f));
        ObjectAnimator animatorSca = ObjectAnimator.ofFloat(mllRoot, "scaleX", 1f, 0.3f);

        AnimatorSet animatorSet = getAnimatorSet(500, (Animator) null);
        animatorSet.play(valueAnimator).with(animatorX).with(animatorY).with(animatorSca).after(afterAnim);
        animatorSet.start();
    }

    
    private void closeDialog() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(mRootView, "translationY", dip2px(-1000f));

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(500);
        animatorSet.playTogether(animator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }
        });
        animatorSet.start();
    }

    
    private void danceEffect() {
        ObjectAnimator transOneX = ObjectAnimator.ofFloat(mImageCoin1, "translationX", dip2px(-30), dip2px(-25), dip2px(-30));
        ObjectAnimator transOneY = ObjectAnimator.ofFloat(mImageCoin1, "translationY", dip2px(-50), dip2px(-45), dip2px(-50));
        transOneX.setRepeatCount(-1);
        transOneY.setRepeatCount(-1);

        ObjectAnimator transTwoX = ObjectAnimator.ofFloat(mImageCoin2, "translationX", dip2px(15f), dip2px(10f), dip2px(15f));
        ObjectAnimator transTwoY = ObjectAnimator.ofFloat(mImageCoin2, "translationY", dip2px(-45f), dip2px(-40f), dip2px(-45f));
        transTwoX.setRepeatCount(-1);
        transTwoY.setRepeatCount(-1);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.playTogether(transOneX, transOneY, transTwoX, transTwoY);
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    private AnimatorSet getAnimatorSet(long duration, Animator... animators) {
        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(duration);
        animatorSet.playTogether(animators);
        return animatorSet;
    }

    private AnimatorSet getAnimatorSet() {
        AnimatorSet animatorSet = new AnimatorSet();
        mSetList.add(animatorSet);
        return animatorSet;
    }

    private MediaPlayer getMediaPlayer(int resourceId) {
        MediaPlayer player = MediaPlayer.create(mContext, resourceId);
        mPlayerList.add(player);
        return player;
    }

    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
        
    }
}
