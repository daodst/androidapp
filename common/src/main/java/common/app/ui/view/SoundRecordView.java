

package common.app.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;




public class SoundRecordView extends View {

    private static final String TAG = "SoundRecordView";
    private float mWidth;

    private float mHeight;


    
    private Paint mXLinePaint;
    private int lineColor = 0x006da8ff;

    
    private int XlineColor = 0xff2A67CD;

    private float lineSize = 0f;

    
    private int waveColor = 0xffd6d6d6;
    
    private float waveWidth = 2f;
    
    private float space = 4f;

    
    private float x2 = 0;


    
    private ArrayList<Integer> waveLines = new ArrayList<>();

    
    private List<Integer> startWaveLines = new ArrayList<>();

    
    private Paint mWavePaint;

    private float halfW;

    private float halfH;

    
    private int mDecibel;

    private float Zoom = 3f;


    int startSize = 0;

    public SoundRecordView(Context context) {
        this(context, null);
    }

    public SoundRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.lineSize = dip2px(getContext(), lineSize);
        mXLinePaint = new Paint();
        mXLinePaint.setColor(XlineColor);
        mXLinePaint.setAntiAlias(true);
        mXLinePaint.setStrokeWidth(lineSize);


        mWavePaint = new Paint();
        mWavePaint.setColor(waveColor);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStrokeWidth(waveWidth);
        mWavePaint.setStrokeCap(Paint.Cap.ROUND);
        mWavePaint.setTextSize(dip2px(getContext(), 0f));
        mWavePaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void changeWaveColor(@ColorRes int colorId) {
        waveColor = ContextCompat.getColor(getContext(), colorId);
        mWavePaint.setColor(waveColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        
        canvas.drawLine(0f, halfH, mWidth, halfH, mXLinePaint);

        int size = waveLines.size();
        if (size == 0) {
            return;
        }
        if (this.x2 >= this.halfW) {
            this.x2 = this.halfW;
            if (startSize == 0) {
                startSize = startWaveLines.size();
            }
            if (startSize > waveLines.size()) {
                
                startSize = waveLines.size();
            }
            for (int i = 0; i < startSize; i++) {
                float x = (halfW - (i * (waveWidth + space)));
                int y = waveLines.get(i);


                canvas.drawLine(x, halfH, x, halfH - y, mWavePaint);

                canvas.drawLine(x, halfH, x, halfH + y, mWavePaint);
            }
            startWaveLines.clear();
        } else {
            for (int i = 0; i < size; i++) {
                float x = (i * (waveWidth + space));
                int y = startWaveLines.get(i);


                canvas.drawLine(x, halfH, x, halfH - y, mWavePaint);
                canvas.drawLine(x, halfH, x, halfH + y, mWavePaint);
            }
            this.x2 = (size) * (waveWidth + space);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.halfW = mWidth;
        this.halfH = mHeight / 2;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        waveLines.clear();
        startWaveLines.clear();
    }

    
    public void setWaveColor(int color) {
        try {
            this.waveColor = getResources().getColor(color);
        } catch (Exception e) {

        }
    }

    
    public void setWaveWidth(float width) {
        this.waveWidth = width;
    }

    
    public void setSpace(float space) {
        this.space = space;
    }

    public void setZoom(float zoom) {
        if (zoom == 0) {
            zoom = 1;
        }
        this.Zoom = zoom;
    }


    
    public void setDecibel(double decibel) {

        this.mDecibel = (int) getValue(decibel);
        waveLines.add(0, this.mDecibel);
        startWaveLines.add(this.mDecibel);
        invalidate();
    }


    
    public double getValue(double decibel) {
        double y;
        Log.i("deb", "x>>>>>>=" + decibel);
        y = decibel / Zoom;
        if (y < 1) {
            y = 1;
        }

        Log.i("deb", "y=" + y);
        
        if (y > dip2px(this.getContext(), halfH - 6f)) {
            y = dip2px(this.getContext(), halfH - 6f);
        }
        return y;
    }

    public void destroy() {
        Log.i("sound", "reset-size=" + waveLines.size());
        this.x2 = 0;
        startSize = 0;
        waveLines.clear();
        startWaveLines.clear();
    }

    
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }
}
