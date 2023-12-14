

package com.wallet.ctc.view.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


public class NoScrollViewPager extends ViewPager {
    private boolean isScroll;

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);   
    }

    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        
        
        
        if (isScroll) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        
        
        
        
        
        if (isScroll) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        
        super.setCurrentItem(item, smoothScroll);
    }
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

}
