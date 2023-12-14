package com.benny.openlauncher.floatingwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;

import com.benny.openlauncher.AppObject;

import common.app.base.them.Eyes;


public class CustomMoveButton extends AppCompatImageView {
    private final int statusHeight;
    int sW;
    int sH;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private boolean isMove = false;
    private Context context;
    private WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams wmParams = AppObject.get().getMywmParams();
    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private long mDownTime;
    private long mUpTime;
    private OnSpeakListener listener;

    public CustomMoveButton(Context context) {
        this(context, null);
        this.context = context;
    }

    public CustomMoveButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomMoveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sW = wm.getDefaultDisplay().getWidth();
        sH = wm.getDefaultDisplay().getHeight();
        statusHeight = getStatusHeight(context);
    }

    
    public static int getStatusHeight(Context context) {
        
        return Eyes.getStatusBarHeight(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        x = event.getRawX();
        y = event.getRawY() - statusHeight;  
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:  
                
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mDownTime = System.currentTimeMillis();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:  
                updateViewPosition();
                isMove = true;
                break;
            case MotionEvent.ACTION_UP:  
                
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                mUpTime = System.currentTimeMillis();
                
                if (mUpTime - mDownTime < 500) {
                    if (Math.abs(mStartX - mLastX) < 20.0 && Math.abs(mStartY - mLastY) < 20.0) {
                        if (listener != null) {
                            listener.onSpeakListener();
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(this, wmParams); 
    }

    
    public interface OnSpeakListener {
        void onSpeakListener();
    }

    public void setOnSpeakListener(OnSpeakListener listener) {
        this.listener = listener;
    }
}
