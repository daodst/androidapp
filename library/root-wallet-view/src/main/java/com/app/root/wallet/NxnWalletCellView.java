package com.app.root.wallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class NxnWalletCellView extends View {

    private int mWith, mHeight;
    private Paint mBitmapPaint, mPathPaint;
    private List<String> wallets = new ArrayList<>();


    public NxnWalletCellView(Context context) {
        super(context);
        init();
    }

    public NxnWalletCellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NxnWalletCellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NxnWalletCellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBitmapPaint = new Paint();
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(3);
        mPathPaint.setColor(Color.GREEN);
        for (int i=0; i<10; i++) {
            wallets.add("wallet"+i);
        }
    }

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWith = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.save();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.w_nxn_child);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int r = Math.min(centerX, centerY) * 2 / 3;
        canvas.translate(centerX, centerY);


        
        int circleMargin = bitmap.getWidth();
        int left = centerX;
        int top = centerY - r;
        int size = wallets.size();
        Path path = new Path();
        int wh = width/2;
        int hh = height / 2;

        for (int i=0; i<wallets.size(); i++) {
            if (i == 0) {
                left = (int) (r*cos(360/size*i));
                top = (int) (r*sin(360/size*i));
                path.moveTo(left, top);
            } else {
                left = (int)(r*cos(360/size*i));
                top = (int) (r*sin(360/size*i));
                path.lineTo(left, top);
            }

            Matrix matrix = new Matrix();
            matrix.postTranslate(left-wh, top-hh);
            canvas.drawBitmap(bitmap, matrix, mBitmapPaint);
        }
        path.close();
        mPathPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPathPaint);
    }

    float sin(int num){
        return (float) Math.sin(num*Math.PI/180);
    }
    float cos(int num){
        return (float) Math.cos(num*Math.PI/180);
    }



    public Path getCirclePath(Rect rect) {
        int radios = rect.height() / 2;
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        Path path = new Path();
        int size = 30;
        for (int i=0; i<size; i++) {
            int x = (int) (radios*cos(360/size*i));
            int y = (int) (radios*sin(360/size*i));
            path.moveTo(x+centerX, y+centerY);
        }
        path.close();
        return path;
    }
}
