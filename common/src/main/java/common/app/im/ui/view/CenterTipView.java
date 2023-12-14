

package common.app.im.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import common.app.R;
import common.app.utils.LogUtil;



public class CenterTipView extends View {
    private static final String TAG = "CenterTipView";
    
    private Paint mPaint;
    
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    
    private int bgColor;
    
    private String text;
    
    private int textColor;
    
    private int textSize;
    
    private int type;
    
    public static final int TYPE_ROUND = 0;
    public static final int TYPE_CIRCLE = 1;

    private int mWidth;
    private int mHeight;
    private int mMin;

    
    private Rect mBound;

    
    public void setText(String text) {
        this.text = text;
        LogUtil.i(TAG, this.text);
        postInvalidate();
    }

    public String getText() {
        return text;
    }

    public CenterTipView(Context context) {
        this(context, null);
    }

    public CenterTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.centertipview);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.centertipview_tips_bgColor) {
                bgColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.centertipview_tips_textColor) {
                textColor = typedArray.getColor(attr, Color.WHITE);

            } else if (attr == R.styleable.centertipview_tips_text) {
                text = typedArray.getString(attr);

            } else if (attr == R.styleable.centertipview_tips_type) {
                type = typedArray.getInt(R.styleable.centertipview_tips_type, 0);

            } else if (attr == R.styleable.centertipview_tips_textSize) {
                textSize = typedArray.getDimensionPixelSize(attr,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

            }
        }
        
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mMin = Math.min(mWidth, mHeight);
        
        setMeasuredDimension(mMin, mMin);
    }

    @Override
    public void draw(Canvas canvas) {
        
        canvas.setDrawFilter(paintFlagsDrawFilter);
        mPaint.setColor(bgColor);
        if (type == TYPE_ROUND) {
            
            RectF rectF = new RectF(0, 0, mWidth, mHeight);
            canvas.drawRoundRect(rectF, 10, 10, mPaint);
        } else if (type == TYPE_CIRCLE) {
            
            canvas.drawCircle(mMin >> 1, mMin >> 1, mMin >> 1, mPaint);
        }
        
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        if (mBound == null) {
            mBound = new Rect();
        }
        LogUtil.i(TAG, mPaint + "\t" + text);
        mPaint.getTextBounds(text, 0, text.length(), mBound);
        canvas.drawText(text, (mWidth - mBound.width() / 2 - 3) >> 2, (mHeight + mBound.height()) >> 1, mPaint);
        
        super.draw(canvas);
    }
}
