package com.benny.openlauncher.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.Tool;



public class RoundRectImageDrawable extends Drawable {

    private final Paint mPaint;
    private final Bitmap mBitmap;
    private final BitmapShader mBitmapShader;

    private RectF rectF;

    public RoundRectImageDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        
        mPaint.setAntiAlias(true);
        rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Matrix matrix = new Matrix();
        float scale = 1.0F;
        
        int iconSize = Tool.dp2px(AppSettings.get().getIconSize());
        scale = Math.max(iconSize * 1.0f / bitmap.getWidth(), iconSize * 1.0f / bitmap.getHeight());
        matrix.setScale(scale, scale);
        mBitmapShader.setLocalMatrix(matrix);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(left, top, right, bottom);
    }


    @Override
    public void draw(Canvas canvas) {
        
        mPaint.setShader(mBitmapShader);
        canvas.drawRoundRect(rectF, 30, 30, mPaint);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
