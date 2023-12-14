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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.app.R;
import com.app.databinding.DialogFragmentDevicesAwardBinding;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.FastClickUtils;
import com.wallet.ctc.util.GlideUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import common.app.utils.LogUtil;
import im.vector.app.provide.ChatStatusProvide;


public class DevicesAwardDialogFragment extends DialogFragment {
    private Context mContext;
    private DialogFragmentDevicesAwardBinding mBinding;
    private ConstraintLayout mRootView;
    private ImageView mImagePhone, mImageCoin1, mImageCoin2, mImageCoin3;
    private FrameLayout mFlCoin1, mFlCoin2, mFlCoin3;

    private String groupId;
    
    private float mAmount = 0.0f;
    
    private String toAddress, fromAddress, gasTax;
    private Consumer<Void> mVoidConsumer;
    private Runnable mClickConsumer;

    private final List<AnimatorSet> mSetList = new ArrayList<>();
    private final List<MediaPlayer> mPlayerList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DialogFragmentDevicesAwardBinding.inflate(inflater);
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

    public DevicesAwardDialogFragment setClickConsumer(Runnable pClickConsumer) {
        mClickConsumer = pClickConsumer;
        return this;
    }

    private void initView(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRootView = view.findViewById(R.id.rootView);
        mImagePhone = view.findViewById(R.id.imagePhone);
        mImageCoin1 = view.findViewById(R.id.imageCoin1);
        mImageCoin2 = view.findViewById(R.id.imageCoin2);
        mImageCoin3 = view.findViewById(R.id.imageCoin3);
        mFlCoin1 = view.findViewById(R.id.flCoin1);
        mFlCoin2 = view.findViewById(R.id.flCoin2);
        mFlCoin3 = view.findViewById(R.id.flCoin3);

        mBinding.tvInAddress.setText(toAddress);
        mBinding.tvOutAddress.setText(TextUtils.isEmpty(fromAddress) ? toAddress : fromAddress);
        String gasTex = gasTax + "<font color='#0BBD8B'>" + getString(R.string.anim_device_reward_string_1) + "</font>";
        mBinding.tvGasTax.setText(Html.fromHtml(gasTex, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        String receiverDescSrt = getString(R.string.anim_device_reward_string_2) + " <br>" + getString(R.string.anim_device_reward_string_3) + " <br>" + getString(R.string.anim_device_reward_string_4, BuildConfig.EVMOS_FAKE_UNINT) + "<font color='#0BBD8B'>" + getString(R.string.anim_device_reward_string_5) + "</font>";
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
            
            if (FastClickUtils.isFastClick()) {
                return;
            }
            if (null != mVoidConsumer) mVoidConsumer.accept(null);
            
        });
    }

    
    private void setWalletInfoData() {
        WalletEntity wallet = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(toAddress, WalletUtil.MCC_COIN);


        mBinding.walletName.setText(wallet.getName());
        mBinding.walletNameAddress.setText(wallet.getMAddress());
        
        

        
        GlideUtil.showImg(mContext, R.mipmap.tt_logo, mBinding.walletLogo);
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
        
        ObjectAnimator animatorScale2 = ObjectAnimator.ofFloat(mImagePhone, "scaleX", 1f, 2.2f);
        ObjectAnimator animatorScale3 = ObjectAnimator.ofFloat(mImagePhone, "scaleY", 1f, 2.2f);
        ObjectAnimator animatorScale4 = ObjectAnimator.ofFloat(mImagePhone, "scaleX", 2.2f, 2.2f, 2.1f);
        ObjectAnimator animatorScale5 = ObjectAnimator.ofFloat(mImagePhone, "scaleY", 2.2f, 2.2f, 2.1f);

        
        AnimatorSet animatorSet = getAnimatorSet(100, (ObjectAnimator) null);
        ObjectAnimator animatorTranslation = ObjectAnimator.ofFloat(mImagePhone, "translationY", dip2px(-360f));
        animatorSet.setDuration(200);
        animatorSet.play(animatorTranslation).with(animatorScale2).with(animatorScale3);
        animatorTranslation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                
                animatorStep3();
            }
        });

        
        int width = getDisplayMetricsWidth(getActivity().getWindowManager());
        ObjectAnimator animatorTranslation2 = ObjectAnimator.ofFloat(mImagePhone, "translationX", (width - 460));
        animatorTranslation2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImagePhone.postDelayed(() -> {
                    mImagePhone.setImageResource(R.mipmap.anm_icon_phone);
                }, 500);
            }
        });
        
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(mImagePhone, "rotation", -25f);
        ObjectAnimator animatorRotationY = ObjectAnimator.ofFloat(mImagePhone, "rotationY", 170F);

        animatorSet.setDuration(700);
        
        animatorSet.play(animatorTranslation2).with(animatorRotation).with(animatorRotationY).with(animatorScale4).with(animatorScale5).after(animatorTranslation);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImagePhone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
                showCoinAnim();
            }
        });
        animatorSet.start();
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
        ObjectAnimator transOneX = ObjectAnimator.ofFloat(mImageCoin1, "translationX", dip2px(200f), 0f);
        ObjectAnimator transOneY = ObjectAnimator.ofFloat(mImageCoin1, "translationY", dip2px(100f), 0f);

        ObjectAnimator transTwoX = ObjectAnimator.ofFloat(mImageCoin2, "translationX", dip2px(30f), 0f);
        ObjectAnimator transTwoY = ObjectAnimator.ofFloat(mImageCoin2, "translationY", dip2px(30f), 0f);

        ObjectAnimator transThreeX = ObjectAnimator.ofFloat(mImageCoin3, "translationX", dip2px(50f), 0f);
        ObjectAnimator transThreeY = ObjectAnimator.ofFloat(mImageCoin3, "translationY", dip2px(50f), 0f);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.walletInfo, "alpha", 0f, 1f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.playTogether(transOneX, transOneY, transTwoX, transTwoY, transThreeX, transThreeY, alpha);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mImageCoin1.setVisibility(View.VISIBLE);
                mImageCoin2.setVisibility(View.VISIBLE);
                mImageCoin3.setVisibility(View.VISIBLE);
                mBinding.tvShare.setVisibility(View.VISIBLE);
                mBinding.walletInfo.setVisibility(View.VISIBLE);

            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    
    private void hideCoinAnim(AnimatorSet set, Consumer<Boolean> consumer) {
        ObjectAnimator transOneX = ObjectAnimator.ofFloat(mFlCoin1, "scaleX", 1f, 0f);
        ObjectAnimator transOneY = ObjectAnimator.ofFloat(mFlCoin1, "scaleY", 1f, 0f);

        ObjectAnimator transTwoX = ObjectAnimator.ofFloat(mFlCoin2, "scaleX", 1f, 0f);
        ObjectAnimator transTwoY = ObjectAnimator.ofFloat(mFlCoin2, "scaleY", 1f, 0f);

        ObjectAnimator transThreeX = ObjectAnimator.ofFloat(mFlCoin3, "scaleX", 1f, 0f);
        ObjectAnimator transThreeY = ObjectAnimator.ofFloat(mFlCoin3, "scaleY", 1f, 0f);

        
        ObjectAnimator animatorTransY1 = ObjectAnimator.ofFloat(mFlCoin1, "translationY", 0f, dip2px(100f));

        
        ObjectAnimator animatorTransY2 = ObjectAnimator.ofFloat(mFlCoin2, "translationY", 0f, dip2px(30f));

        
        ObjectAnimator animatorTransY4 = ObjectAnimator.ofFloat(mFlCoin3, "translationY", 0f, dip2px(50f));


        set.play(transOneX).with(transOneY).with(transTwoX).with(transTwoY).with(transThreeX).with(transThreeY).with(animatorTransY1).with(animatorTransY2).with(animatorTransY4);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animation.setDuration(500L);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
                
                
                
                mImageCoin1.setVisibility(View.INVISIBLE);
                mImageCoin2.setVisibility(View.INVISIBLE);
                mImageCoin3.setVisibility(View.INVISIBLE);
                
                mBinding.walletInfo.setVisibility(View.INVISIBLE);
                
                mBinding.rlWalletDetail.setVisibility(View.VISIBLE);

                
                consumer.accept(true);
            }
        });
    }

    
    public void shareOnClick() {
        ObjectAnimator animatorScale = ObjectAnimator.ofFloat(mImagePhone, "scaleX", 1f, 0f);
        ObjectAnimator animatorScaleTwo = ObjectAnimator.ofFloat(mImagePhone, "scaleY", 1f, 0f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                
                mImagePhone.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.playTogether(animatorScale, animatorScaleTwo);
        animatorSet.setDuration(800);
        hideCoinAnim(animatorSet, result -> {
            
            animatorShowWalletInfo(mClickConsumer);
        });
        animatorSet.start();
    }

    
    private void animatorShowWalletInfo(Runnable consumer) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBinding.walletInfo, "alpha", 1f, 0f);

        
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mBinding.tvTitle, "alpha", 1f, 0f);
        ObjectAnimator alpha3 = ObjectAnimator.ofFloat(mBinding.llRoot, "alpha", 1f, 0f);

        
        int width = getDisplayMetricsWidth(getActivity().getWindowManager());
        
        ObjectAnimator translationReceiver = ObjectAnimator.ofFloat(mBinding.receiverInfo, "translationX", width - dip2px(48));
        ObjectAnimator alphaReceiver = ObjectAnimator.ofFloat(mBinding.receiverInfo, "alpha", 0f, 1f);

        
        ObjectAnimator translationReceiverTitle = ObjectAnimator.ofFloat(mBinding.receiverTitle, "translationX", width - dip2px(48));
        ObjectAnimator alphaReceiverTitle = ObjectAnimator.ofFloat(mBinding.receiverTitle, "alpha", 0f, 1f);

        
        ObjectAnimator alphaWalletDetail = ObjectAnimator.ofFloat(mBinding.rlWalletDetail, "alpha", 0f, 1f);
        ObjectAnimator transYWalletDetail = ObjectAnimator.ofFloat(mBinding.rlWalletDetail, "translationY", dip2px(55), 0f);

        AnimatorSet animatorSet = getAnimatorSet();
        animatorSet.setDuration(800L);
        animatorSet.playTogether(alpha,
                
                alpha2, alpha3,
                
                translationReceiver, alphaReceiver, translationReceiverTitle, alphaReceiverTitle,
                
                alphaWalletDetail, transYWalletDetail);
        animatorSet.start();

        String address = ChatStatusProvide.getAddress(mContext);
        int identity = ChatStatusProvide.getOtherUserIdentity(mContext, address, groupId);
        if (identity == 0) mBinding.thanks.setVisibility(View.INVISIBLE);
        LogUtil.d("：", identity == 0 ? "" : "");

        mBinding.thanks.setOnClickListener(v -> {
            consumer.run();
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
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getDisplayMetricsWidth(WindowManager window) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return window.getCurrentWindowMetrics().getBounds().width();
        } else {
            return window.getDefaultDisplay().getWidth();
        }
    }
}
