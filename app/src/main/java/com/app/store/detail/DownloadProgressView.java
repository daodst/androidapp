package com.app.store.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;


public class DownloadProgressView extends View {
    
    private static final int MODE_NORMAL = 1;
    
    private static final int MODE_TOUCH = 2;

    
    private int mDefaultMinWidth;
    
    private int mDefaultMinHeight;

    
    private int mViewWidth;
    
    private int mViewHeight;

    
    private float mRadius;
    
    private Paint mBgPaint;
    
    private Paint mProgressPaint;
    
    private Paint mTextPaint;
    
    private int mProgress;
    
    private int mBgColor;
    
    private int mProgressBgColor;
    
    private int mPercentageTextColor;
    
    private int mPercentageTextColor2;
    
    private float mPercentageTextSize;
    
    private int mMaxProgress;
    
    private OnProgressUpdateListener mOnProgressUpdateListener;
    
    private int mControlMode;
    
    private float mTouchDownX;

    public DownloadProgressView(Context context) {
        this(context, null);
    }

    public DownloadProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        
        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressBgColor);
        mProgressPaint.setAntiAlias(true);
        
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mPercentageTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        
        mDefaultMinWidth = dip2px(context, 180f);
        mDefaultMinHeight = dip2px(context, 40f);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressView, defStyleAttr, 0);
        mBgColor = array.getColor(R.styleable.DownloadProgressView_dpv_bg, Color.argb(100, 169, 169, 169));
        mProgressBgColor = array.getColor(R.styleable.DownloadProgressView_dpv_progress_bg, Color.GRAY);
        
        mPercentageTextColor = array.getColor(R.styleable.DownloadProgressView_dpv_percentage_text_color, mProgressBgColor);
        
        mPercentageTextColor2 = array.getColor(R.styleable.DownloadProgressView_dpv_percentage_text_color2, Color.WHITE);
        
        mPercentageTextSize = array.getDimension(R.styleable.DownloadProgressView_dpv_percentage_text_size, sp2px(context, 15f));
        
        mProgress = array.getInt(R.styleable.DownloadProgressView_dpv_progress, 0);
        
        mMaxProgress = array.getInteger(R.styleable.DownloadProgressView_dpv_max_progress, 100);
        
        mControlMode = array.getInt(R.styleable.DownloadProgressView_dpv_control_mode, MODE_NORMAL);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        
        mRadius = Math.min(mViewWidth, mViewHeight) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        clipRound(canvas);
        
        drawBg(canvas);
        
        drawProgress(canvas);
        
        drawText(canvas);
    }

    

    private float getFrameLeft() {
        return getPaddingStart();
    }

    private float getFrameRight() {
        return mViewWidth - getPaddingEnd();
    }

    private float getFrameTop() {
        return getPaddingTop();
    }

    private float getFrameBottom() {
        return mViewHeight - getPaddingBottom();
    }

    

    
    private void clipRound(Canvas canvas) {
        Path path = new Path();
        RectF roundRect = new RectF(getFrameLeft(), getFrameTop(), getFrameRight(), getFrameBottom());
        path.addRoundRect(roundRect, mRadius, mRadius, Path.Direction.CW);
        canvas.clipPath(path);
    }

    
    private void drawBg(Canvas canvas) {
        canvas.drawRect(new RectF(getFrameLeft(), getPaddingTop(), getFrameRight(), getFrameBottom()), mBgPaint);
    }

    
    private void drawProgress(Canvas canvas) {
        RectF rect = new RectF(getFrameLeft(), getFrameTop(), getFrameRight() * getProgressRatio(), getFrameBottom());
        canvas.drawRect(rect, mProgressPaint);
    }

    
    private void drawText(Canvas canvas) {
        mTextPaint.setColor(mPercentageTextColor);
        
        Bitmap textBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        String textContent = mProgress + "%";
        
        float textY = mViewHeight / 2.0f - (mTextPaint.getFontMetricsInt().descent / 2.0f + mTextPaint.getFontMetricsInt().ascent / 2.0f);
        textCanvas.drawText(textContent, mViewWidth / 2.0f, textY, mTextPaint);
        
        mTextPaint.setColor(mPercentageTextColor2);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        textCanvas.drawRect(new RectF(getFrameLeft(), getFrameTop(), getFrameRight() * getProgressRatio(), getFrameBottom()), mTextPaint);
        
        canvas.drawBitmap(textBitmap, getFrameLeft(), getFrameTop(), mTextPaint);
        mTextPaint.setXfermode(null);
        textBitmap.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec, true), handleMeasure(heightMeasureSpec, false));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        
        if (mControlMode == MODE_TOUCH && action == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if (mControlMode == MODE_TOUCH) {
            int action = event.getAction();
            
            if (action == MotionEvent.ACTION_DOWN) {
                mTouchDownX = event.getX();
                return true;
            } else if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                
                float endX = event.getX();
                
                float distanceX = Math.abs(endX - mTouchDownX);
                float ratio = (distanceX * 1.0f) / (getFrameRight() - getFrameLeft());
                
                float progress = mMaxProgress * ratio;
                setProgress((int) progress);
                return true;
            }
            return super.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    
    private int handleMeasure(int measureSpec, boolean isWidth) {
        int result;
        if (isWidth) {
            result = mDefaultMinWidth;
        } else {
            result = mDefaultMinHeight;
        }
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    
    public DownloadProgressView setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
        return this;
    }

    
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            mProgress = progress;
            invalidate();
            if (mOnProgressUpdateListener != null) {
                mOnProgressUpdateListener.onProgressUpdate(progress);
            }
        }
    }

    
    public int getProgress() {
        return mProgress;
    }

    
    public int getMaxProgress() {
        return mMaxProgress;
    }

    public interface OnProgressUpdateListener {
        
        void onProgressUpdate(int progress);
    }

    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        mOnProgressUpdateListener = onProgressUpdateListener;
    }

    
    private float getProgressRatio() {
        return (mProgress / (mMaxProgress * 1.0f));
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
