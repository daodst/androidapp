

package com.wallet.ctc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;



public class HorizontalScrollScrollView extends HorizontalScrollView{

    public HorizontalScrollScrollView(Context context)
    {
        super(context);
    }

    public HorizontalScrollScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HorizontalScrollScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    public interface ScrollViewListener {

        void onScrollChanged(HorizontalScrollScrollView observableScrollView, int x, int y , int oldx, int oldy);
    }
    private ScrollViewListener mScrollViewListener=null;

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        mScrollViewListener = scrollViewListener;
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
