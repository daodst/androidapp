package com.wallet.ctc.view.stepview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.WidgetVeriticalStepItemBinding;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import common.app.utils.TimeUtil;


public class VerticalStepView extends LinearLayout implements VerticalStepViewIndicator.OnDrawIndicatorListener {
    private LinearLayout mTextContainer;
    private VerticalStepViewIndicator mStepsViewIndicator;
    private List<ChainBridgeDetailStepEntity> mTexts;
    private int mComplectingPosition;
    private int mUnComplectedTextColor = ContextCompat.getColor(getContext(), R.color.default_hint_text_color);
    private int mComplectedTextColor = ContextCompat.getColor(getContext(), R.color.default_theme_color);

    private int mTextSize = 14;
    private TextView mTextView;


    public VerticalStepView(Context context) {
        this(context, null);
    }

    public VerticalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_vertical_stepsview, this);
        mStepsViewIndicator = (VerticalStepViewIndicator) rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        mTextContainer = (LinearLayout) rootView.findViewById(R.id.rl_text_container);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    
    public VerticalStepView setStepViewTexts(List<ChainBridgeDetailStepEntity> texts) {
        mTexts = texts;
        if (texts != null) {
            mStepsViewIndicator.setStepNum(mTexts.size());
        } else {
            mStepsViewIndicator.setStepNum(0);
        }
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorComplectingPosition(int complectingPosition) {
        mComplectingPosition = complectingPosition;
        mStepsViewIndicator.setComplectingPosition(complectingPosition);
        return this;
    }

    
    public VerticalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    
    public VerticalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    
    public VerticalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    
    public VerticalStepView reverseDraw(boolean isReverSe) {
        this.mStepsViewIndicator.reverseDraw(isReverSe);
        return this;
    }

    
    public VerticalStepView setLinePaddingProportion(float linePaddingProportion) {
        this.mStepsViewIndicator.setIndicatorLinePaddingProportion(linePaddingProportion);
        return this;
    }


    
    public VerticalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    @Override
    public void ondrawIndicator() {
        if (mTextContainer != null) {
            mTextContainer.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mTexts != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mTexts.size(); i++) {
                    ChainBridgeDetailStepEntity entity = mTexts.get(i);
                    WidgetVeriticalStepItemBinding stepBinding = WidgetVeriticalStepItemBinding.inflate(LayoutInflater.from(getContext()));
                    stepBinding.title.setText(entity.title);
                    stepBinding.title.setTextColor(entity.current
                            ? ContextCompat.getColor(getContext(), R.color.default_theme_color)
                            : ContextCompat.getColor(getContext(), R.color.default_hint_text_color));
                    stepBinding.content.setText(entity.content);
                    if (entity.countdown > 0) {
                        stepBinding.countDown.setVisibility(View.VISIBLE);
                        stepBinding.countDown.setText(TimeUtil.getTimeMS(entity.countdown));
                    }
                    if (TextUtils.isEmpty(entity.withdrawCode)) {
                        stepBinding.withdraw.setVisibility(GONE);
                        stepBinding.withDrawCode.setVisibility(GONE);
                    } else {
                        stepBinding.withdraw.setVisibility(VISIBLE);
                        stepBinding.withDrawCode.setVisibility(VISIBLE);
                        stepBinding.withDrawCode.setText(entity.withdrawCode);
                        stepBinding.withDrawCode.setOnClickListener(v -> {
                            
                            AllUtils.copyText(entity.withdrawCode);
                        });
                    }



                    mTextContainer.addView(stepBinding.getRoot());
                }
            }
        }
    }
}
