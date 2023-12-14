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
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.app.R;
import com.app.databinding.DialogFragmentDvmVirtualBinding;
import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.github.penfeizhou.animation.loader.AssetStreamLoader;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.GlideUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import common.app.utils.LogUtil;
import im.vector.app.provide.ChatStatusProvide;


public class DvmVirtualDialogFragment extends DialogFragment {
    private Context mContext;
    private DialogFragmentDvmVirtualBinding mBinding;
    private ConstraintLayout mRootView;

    private String groupId;
    
    private float mAmount = 0.0f;
    
    private String toAddress, fromAddress, gasTax;
    private Consumer<Void> mVoidConsumer;
    private Consumer<Integer> mClickConsumer;

    
    private long clickTime = 0L;

    private final List<AnimatorSet> mSetList = new ArrayList<>();
    private final List<MediaPlayer> mPlayerList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DialogFragmentDvmVirtualBinding.inflate(inflater);
        View view = mBinding.getRoot();
        mContext = getContext();
        initView(view);
        setWalletInfoData();
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

    public void setData(Float amount, String toAddress, String fromAddress, String gasTax, Consumer<Void> consumer) {
        this.mAmount = amount;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.gasTax = gasTax;
        this.mVoidConsumer = consumer;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public DvmVirtualDialogFragment setClickConsumer(Consumer<Integer> pClickConsumer) {
        mClickConsumer = pClickConsumer;
        return this;
    }

    private void initView(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRootView = view.findViewById(R.id.rootView);

        mBinding.tvInAddress.setText(toAddress);
        mBinding.tvOutAddress.setText(TextUtils.isEmpty(fromAddress) ? toAddress : fromAddress);
        String gasTex = gasTax + " DST<font color='#0BBD8B'>" + getString(R.string.anim_device_reward_string_1) + "</font>";
        mBinding.tvGasTax.setText(Html.fromHtml(gasTex, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        String receiverDescSrt = getString(R.string.anim_dvm_reward_string_1) + "<font color='#0BBD8B'>300%</font> " + getString(R.string.anim_dvm_reward_string_2) + "<font color='#0BBD8B'>" + getString(R.string.anim_device_reward_string_5) + "</font>" + getString(R.string.anim_device_reward_string_2);
        mBinding.receiverDesc.setText(Html.fromHtml(receiverDescSrt, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        String walletReceiverTitle = getString(R.string.anim_device_reward_string_6) + "<font color='#FFE400'>" + getString(R.string.anim_device_reward_string_7) + "</font>";
        mBinding.receiverTitle.setText(Html.fromHtml(walletReceiverTitle, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        
        setLayoutParam();

        
        onStartAnim();

        if (mAmount == 0){
            mBinding.tvShare.setEnabled(false);
            mBinding.tvShare.setBackgroundResource(R.drawable.btn_share_gray);
            mBinding.tvShare.setTextColor(ContextCompat.getColor(mContext, R.color.default_hint_text_color));
        }

        
        mBinding.tvShare.setOnClickListener(v -> {
            
            if (System.currentTimeMillis() - clickTime <= 2000) {
                clickTime = System.currentTimeMillis();
                return;
            }
            clickTime = System.currentTimeMillis();
            if (null != mVoidConsumer) mVoidConsumer.accept(null);
            
        });
    }

    
    private void setLayoutParam() {
        int width = getDisplayMetricsWidth(getActivity().getWindowManager());
        
        
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBinding.receiverInfo.getLayoutParams();
        params.width = width - dip2px(48);
        mBinding.receiverInfo.setLayoutParams(params);

        ConstraintLayout.LayoutParams paramsTitle = (ConstraintLayout.LayoutParams) mBinding.receiverTitle.getLayoutParams();
        paramsTitle.width = width - dip2px(48);
        mBinding.receiverTitle.setLayoutParams(paramsTitle);

        
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mBinding.receiverDesc.getLayoutParams();
        params1.width = (int) (width / 1.92);
        mBinding.receiverDesc.setLayoutParams(params1);
    }

    
    private void setWalletInfoData() {
        WalletEntity wallet = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(toAddress, WalletUtil.MCC_COIN);


        mBinding.walletName.setText(wallet.getName());
        mBinding.walletNameAddress.setText(wallet.getMAddress());
        
        

        
        GlideUtil.showImg(mContext, R.mipmap.tt_logo, mBinding.walletLogo);
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

        startApngAnimation(mBinding.ivDvm);
        new Handler().postDelayed(() -> {
            animatorStep3();
            showCoinAnim();
        }, 600);

        
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.coin, "alpha", 0f, 1f);
        ObjectAnimator animatorScale = ObjectAnimator.ofFloat(mBinding.coin, "scaleX", 0f, 1f);
        ObjectAnimator animatorScaleTwo = ObjectAnimator.ofFloat(mBinding.coin, "scaleY", 0f, 1f);
        ObjectAnimator animatorTransY = ObjectAnimator.ofFloat(mBinding.coin, "translationY", dip2px(60), 0f);
        
        new Handler().postDelayed(() -> {
            AnimatorSet set = getAnimatorSet();
            set.playTogether(alpha, animatorScale, animatorScaleTwo, animatorTransY);
            set.setDuration(600);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    
                    AnimatorSet set = getAnimatorSet();
                    ObjectAnimator transY = ObjectAnimator.ofFloat(mBinding.coin, "translationY", 0f, dip2px(10), 0f);
                    transY.setRepeatCount(ValueAnimator.INFINITE);
                    set.setDuration(1500L);
                    set.play(transY);
                    set.start();
                }
            });
            set.start();
        }, 1200);

        
        

        
        


    }

    
    private void animatorStep3() {
        
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0000f, mAmount);
        valueAnimator.addUpdateListener(animation -> {
            String valueStr = "" + animation.getAnimatedValue();
            if (animation.getAnimatedValue() instanceof Float) {
                float value = (float) animation.getAnimatedValue();
                BigDecimal bigDecimal = BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
                valueStr = bigDecimal.toPlainString();
            }
            mBinding.tvValue.setText(valueStr);
        });
        AnimatorSet animatorSet = getAnimatorSet(700, valueAnimator);
        animatorSet.start();
        
        
        

    }

    
    private void showCoinAnim() {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.walletInfo, "alpha", 0f, 1f);
        ObjectAnimator alphaEvm = ObjectAnimator.ofFloat(mBinding.ivDvm, "alpha", 0f, 1f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.playTogether(alpha, alphaEvm);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBinding.tvShare.setVisibility(View.VISIBLE);
                mBinding.walletInfo.setVisibility(View.VISIBLE);

            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    
    public void shareOnClick() {
        animatorShowWalletInfo();
    }

    
    private void animatorShowWalletInfo() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.walletInfo, "alpha", 1f, 0f);

        
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mBinding.tvTitle, "alpha", 1f, 0f);
        ObjectAnimator alpha3 = ObjectAnimator.ofFloat(mBinding.llRoot, "alpha", 1f, 0f);

        
        int width = getDisplayMetricsWidth(getActivity().getWindowManager());
        
        ObjectAnimator translationReceiver = ObjectAnimator.ofFloat(mBinding.receiverInfo, "translationX", width - dip2px(48));
        ObjectAnimator alphaReceiver = ObjectAnimator.ofFloat(mBinding.receiverInfo, "alpha", 0f, 1f);

        
        ObjectAnimator translationReceiverTitle = ObjectAnimator.ofFloat(mBinding.receiverTitle, "translationX", width - dip2px(48));
        ObjectAnimator alphaReceiverTitle = ObjectAnimator.ofFloat(mBinding.receiverTitle, "alpha", 0f, 1f);

        
        
        ObjectAnimator transYWalletDetail = ObjectAnimator.ofFloat(mBinding.rlWalletDetail, "translationY", dip2px(55), 0f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(800L);
        animatorSet.playTogether(alpha,
                
                alpha2, alpha3);
        animatorSet.start();

        AnimatorSet animatorSet2 = getAnimatorSet();
        
        animatorSet2.playTogether(translationReceiver, alphaReceiver, translationReceiverTitle, alphaReceiverTitle,
                
                transYWalletDetail);


        
        ObjectAnimator alphaDvm = ObjectAnimator.ofFloat(mBinding.ivDvm, "alpha", 1f, 0f);
        ObjectAnimator scaleDvmX = ObjectAnimator.ofFloat(mBinding.ivDvm, "scaleX", 1f, 0f);
        ObjectAnimator scaleDvmY = ObjectAnimator.ofFloat(mBinding.ivDvm, "scaleY", 1f, 0f);
        ObjectAnimator alphaCoin = ObjectAnimator.ofFloat(mBinding.coin, "scaleY", 1f, 0f);
        ObjectAnimator scaleCoinX = ObjectAnimator.ofFloat(mBinding.coin, "scaleY", 1f, 0f);
        ObjectAnimator scaleCoinY = ObjectAnimator.ofFloat(mBinding.coin, "scaleY", 1f, 0f);

        AnimatorSet animatorSet3 = getAnimatorSet();
        animatorSet3.setDuration(500L);
        
        animatorSet3.playTogether(alphaDvm, scaleDvmX, scaleDvmY, alphaCoin, scaleCoinX, scaleCoinY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet2.start();
                animatorSet3.start();
            }
        });
        animatorSet3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (MediaPlayer player : mPlayerList) {
                    if (player.isPlaying()) {
                        player.stop();
                        player.release();
                    }
                }
            }
        });

        mBinding.addHashPower.setOnClickListener(v -> {
            
            mClickConsumer.accept(1);
            closeDialog();
        });
        String address = ChatStatusProvide.getAddress(mContext);
        int identity = ChatStatusProvide.getOtherUserIdentity(mContext, address, groupId);
        if (identity == 0) mBinding.thanks.setVisibility(View.INVISIBLE);
        LogUtil.d("：", identity == 0 ? "" : "");

        mBinding.thanks.setOnClickListener(v -> {
            
            mClickConsumer.accept(2);
            closeDialog();
        });
        mBinding.leave.setOnClickListener(v -> closeDialog());
    }

    
    @Deprecated
    private void animatorNumberCollapse(Animator afterAnim) {
        
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mAmount, 0.0000f);
        valueAnimator.addUpdateListener(animation -> {
            String valueStr = "" + animation.getAnimatedValue();
            if (animation.getAnimatedValue() instanceof Float) {
                float value = (float) animation.getAnimatedValue();
                BigDecimal bigDecimal = BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
                valueStr = bigDecimal.toPlainString();
            }
            mBinding.tvValue.setText(valueStr);
        });

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mBinding.llRoot, "translationX", dip2px(150f));
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mBinding.llRoot, "translationY", dip2px(220f));
        ObjectAnimator animatorSca = ObjectAnimator.ofFloat(mBinding.llRoot, "scaleX", 1f, 0.3f);

        AnimatorSet animatorSet = getAnimatorSet(500, (Animator) null);
        animatorSet.play(valueAnimator).with(animatorX).with(animatorY).with(animatorSca).after(afterAnim);
        animatorSet.start();
    }

    
    private void closeDialog() {
        @SuppressLint("Recycle") ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRootView, "rotationY", -225f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRootView, "scaleX", 0.3f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mRootView, "scaleY", 0.3f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setStartDelay(100);
        animatorSet.playTogether(animator1, animator2, animator3);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }
        });
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

    private int getDisplayMetricsWidth(WindowManager window) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return window.getCurrentWindowMetrics().getBounds().width();
        } else {
            return window.getDefaultDisplay().getWidth();
        }
    }

    
    private void startApngAnimation(ImageView view) {
        AssetStreamLoader assetLoader = new AssetStreamLoader(mContext, "dvm.png");
        
        APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
        
        view.setImageDrawable(apngDrawable);
        
        apngDrawable.setLoopLimit(1);
    }
}
