

package com.wallet.ctc.view.choosetime.picker;

import android.app.Activity;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import com.wallet.ctc.view.choosetime.popup.ConfirmPopup;
import com.wallet.ctc.view.choosetime.widget.WheelView;


public abstract class WheelPicker extends ConfirmPopup<View> {
    protected int textSize = WheelView.TEXT_SIZE;
    protected int textColorNormal = WheelView.TEXT_COLOR_NORMAL;
    protected int textColorFocus = WheelView.TEXT_COLOR_FOCUS;
    protected int lineColor = WheelView.LINE_COLOR;
    protected boolean lineVisible = true;
    protected int offset = WheelView.OFF_SET;

    public WheelPicker(Activity activity) {
        super(activity);
    }

    
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    
    public void setTextColor(@ColorInt int textColorFocus, @ColorInt int textColorNormal) {
        this.textColorFocus = textColorFocus;
        this.textColorNormal = textColorNormal;
    }

    
    public void setTextColor(@ColorInt int textColor) {
        this.textColorFocus = textColor;
    }

    
    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    
    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
    }

    
    public void setOffset(@IntRange(from = 1, to = 4) int offset) {
        this.offset = offset;
    }

}
