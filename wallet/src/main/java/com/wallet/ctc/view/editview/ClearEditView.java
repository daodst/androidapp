

package com.wallet.ctc.view.editview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;



public class ClearEditView extends EditText implements
        View.OnFocusChangeListener, TextWatcher {
    
    private Drawable mClearDrawable;

    

    
    private boolean hasFoucs;

    public ClearEditView(Context context) {
        this(context, null);
    }

    public ClearEditView(Context context, AttributeSet attrs) {
        
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(getContext(), R.drawable.delete_selector);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

        
        setClearIconVisible(false);
        
        setOnFocusChangeListener(this);
        
        addTextChangedListener(this);
    }


    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {

                    if (null != clearClickListener){
                        clearClickListener.clearAction();
                    }
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
        if(mListener != null) {
            mListener.canVisible(visible);
        }
    }

    public interface CanVisibleListener {
        public void canVisible(boolean visible);
    }

    private CanVisibleListener mListener = null;

    
    public void setCanVisibleListener(CanVisibleListener listener) {
        mListener = listener;
    }


    
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
            if(mListener != null) {
                mListener.canVisible(s.length() > 0);
            }
        }
        if (null != clearClickListener){
            clearClickListener.clearAction();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }


    
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    private ClearClickListener clearClickListener;

    public void setClearClickListener(ClearClickListener clearClickListener) {
        this.clearClickListener = clearClickListener;
    }

    public interface ClearClickListener{
        void clearAction();
    }
}
