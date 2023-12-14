package com.wallet.ctc.ui.me.chain_bridge2.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ItemChainBridgeChildOrderStepBinding;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import common.app.mall.util.ToastUtil;
import common.app.utils.TimeUtil;


public class ChildOrderStepView extends LinearLayout {
    public ChildOrderStepView(Context context) {
        super(context);
    }

    public ChildOrderStepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildOrderStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChildOrderStepView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    
    public void setStepViewTexts(List<ChainBridgeDetailStepEntity> items) {
        removeAllViews();
        if(null == items || items.size() == 0){
            return;
        }
        int size = items.size();
        int lastIndex = size -1;
        for (int i = 0; i < size; i++) {
            ChainBridgeDetailStepEntity item = items.get(i);
            ItemChainBridgeChildOrderStepBinding stepView = ItemChainBridgeChildOrderStepBinding.inflate(LayoutInflater.from(getContext()));
            if(i == 0) {
                
                stepView.topLine1.setVisibility(INVISIBLE);
            }
            if(i == lastIndex){
                
                stepView.topLine2.setVisibility(INVISIBLE);
                stepView.lineView.setVisibility(INVISIBLE);
            }
            int nextIndex = i+1;
            if (nextIndex >= size){
                nextIndex = lastIndex;
            }
            ChainBridgeDetailStepEntity nextItem = items.get(nextIndex);
            if(item.current) {
                stepView.topLine1.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.default_theme_color));
                if (nextItem.current){
                    stepView.topLine2.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.default_theme_color));
                    stepView.lineView.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.default_theme_color));
                    if(i == lastIndex){
                        
                        stepView.circleView.setImageResource(R.drawable.point_complete);
                    } else {
                        
                        stepView.circleView.setImageResource(R.drawable.point_ing);
                    }

                } else {
                    stepView.topLine2.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.line_gray));
                    stepView.lineView.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.line_gray));
                    
                    stepView.circleView.setImageResource(R.drawable.point_complete);
                }
            } else {
                stepView.topLine1.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.line_gray));
                stepView.circleView.setImageResource(R.drawable.point_undo);
                stepView.topLine2.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.line_gray));
                stepView.lineView.setBackgroundColor(ContextCompat.getColor(getContext(), common.app.R.color.line_gray));
            }


            stepView.title.setText(item.title);
            stepView.title.setTextColor(item.current
                    ? ContextCompat.getColor(getContext(), R.color.default_theme_color)
                    : ContextCompat.getColor(getContext(), R.color.default_hint_text_color));
            stepView.content.setText(item.content);
            if (item.countdown > 0) {
                stepView.countDown.setText(TimeUtil.getRemainTime(item.countdown));
            } else {
                stepView.countDown.setText("");
            }
            if (TextUtils.isEmpty(item.withdrawCode)) {
                stepView.withdraw.setVisibility(INVISIBLE);
                stepView.withDrawCode.setVisibility(INVISIBLE);
            } else {
                stepView.withdraw.setVisibility(VISIBLE);
                stepView.withDrawCode.setVisibility(VISIBLE);
                stepView.withDrawCode.setText(item.withdrawCode);
                stepView.withDrawCode.setOnClickListener(v -> {
                    
                    AllUtils.copyText(item.withdrawCode);
                    ToastUtil.showToast(R.string.copy_success);
                });
            }

            addView(stepView.getRoot());
        }
    }
}
