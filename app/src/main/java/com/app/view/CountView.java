

package com.app.view;



import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.app.my.interfaces.RiseNumberBaseListener;

import common.app.utils.AllUtils;




public class CountView extends TextView implements RiseNumberBaseListener {

    private static final int STOPPED = 0;

    private static final int RUNNING = 1;

    private int mPlayingState = STOPPED;

    private float number;

    private float fromNumber;

    private long duration = 1000;
    
    private int numberType = 2;

    private boolean flags = true;

    private EndListener mEndListener = null;

    final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

    public CountView(Context context) {
        super(context);
    }

    public CountView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public CountView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    public interface EndListener {
        public void onEndFinish();
    }

    public boolean isRunning() {
        return (mPlayingState == RUNNING);
    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, number);
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (flags) {
                    setText(AllUtils.format("0.00").format(Double.parseDouble(valueAnimator.getAnimatedValue().toString())));
                    if (valueAnimator.getAnimatedValue().toString().equalsIgnoreCase(number+"")) {
                        setText(AllUtils.format("0.00").format(Double.parseDouble(number+"")));
                    }
                } else {
                    setText(AllUtils.format("0.00").format(Double.parseDouble(valueAnimator.getAnimatedValue().toString())));
                    if (valueAnimator.getAnimatedValue().toString().equalsIgnoreCase(number + "")) {
                        setText(AllUtils.format("0.00").format(Double.parseDouble(number + "")));
                    }
                }
                if (valueAnimator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null) {
                        mEndListener.onEndFinish();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    private void runInt() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber, (int) number);
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                setText(valueAnimator.getAnimatedValue().toString());
                if (valueAnimator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null) {
                        mEndListener.onEndFinish();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    static int sizeOfInt(int x) {
        for (int i = 0;; i++) {
            if (x <= sizeTable[i]) {
                return i + 1;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void start() {

        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1) {
                runInt();
            } else {
                runFloat();
            }
        }
    }

    @Override
    public CountView withNumber(float number, boolean flag) {

        this.number = number;
        this.flags = flag;
        numberType = 2;
        fromNumber = 0;

        return this;
    }

    @Override
    public CountView withNumber(float number) {
        System.out.println(number);
        this.number = number;
        numberType = 2;
        fromNumber = 0;

        return this;
    }

    @Override
    public CountView withNumber(int number) {
        this.number = number;
        numberType = 1;
        fromNumber = 0;

        return this;
    }

    @Override
    public CountView setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public void setOnEnd(EndListener callback) {
        mEndListener = callback;
    }
}
