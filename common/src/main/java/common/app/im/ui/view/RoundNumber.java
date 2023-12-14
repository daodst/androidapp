

package common.app.im.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import common.app.utils.DisplayUtils;

public class RoundNumber extends View {
    private int radius; 
    private float circleX; 
    private float circleY; 

    private Paint circlePaint; 
    private TextPaint textPaint; 
    private int textSize; 
    private Paint.FontMetrics textFontMetrics; 
    private float textMove; 

    private String message = "1";
    private boolean firstInit = true;
    private Context mContext;


    public RoundNumber(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initPaint();
    }

    
    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (firstInit) {
            firstInit = false;

            radius = w / 2;
            int[] position = new int[2];
            getLocationOnScreen(position);

            circleX = radius;
            circleY = radius;

            textSize = radius/2; 
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(DisplayUtils.sp2px(mContext, textSize));
            textFontMetrics = textPaint.getFontMetrics();
            textMove = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2; 
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(circleX, circleY, radius, circlePaint);
        canvas.drawText(message, circleX, circleY + textMove, textPaint);
    }



    
    public void setMessage(String message) {
        this.message = message;
        invalidate();
    }


}
