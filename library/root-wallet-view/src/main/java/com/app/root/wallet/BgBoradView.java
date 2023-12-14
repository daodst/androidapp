package com.app.root.wallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BgBoradView extends View {

    private static final String TAG = "TestBoradView";

    private Paint mPathPaint, mPathSPaint, mLinePaint;
    private List<PathData> mPaths;
    private Random mRandom = new Random();
    private ColorMatrixColorFilter mDefaultColorFilter, mGreenColorFilter, mYellowColorFilter;
    private int[] lineArray = {R.mipmap.nxnvl00, R.mipmap.nxnvl01, R.mipmap.nxnvl02, R.mipmap.nxnvl03, R.mipmap.nxnvl04, R.mipmap.nxnvl05,
            R.mipmap.nxnvl06, R.mipmap.nxnvl07, R.mipmap.nxnvl08, R.mipmap.nxnvl09, R.mipmap.nxnvl10, R.mipmap.nxnvl11, R.mipmap.nxnvl12,
            R.mipmap.nxnvl13, R.mipmap.nxnvl14, R.mipmap.nxnvl15, R.mipmap.nxnvl16, R.mipmap.nxnvl17, R.mipmap.nxnvl18, R.mipmap.nxnvl19,
            R.mipmap.nxnvl20, R.mipmap.nxnvl21, R.mipmap.nxnvl22, R.mipmap.nxnvl23, R.mipmap.nxnvl24, R.mipmap.nxnvl25, R.mipmap.nxnvl26,
            R.mipmap.nxnvl27, R.mipmap.nxnvl28};
    private Map<String, Integer> lineMap = new HashMap<>();
    private Map<String, Long> lineTimeMap = new HashMap<>();
    private Map<Integer, Bitmap> cacheBitmapMap = new HashMap<>();
    public BgBoradView(Context context) {
        super(context);
        init();
    }

    public BgBoradView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BgBoradView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BgBoradView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        
        mDefaultColorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                1,0,0,0,0,
                0,1,0,0,0,
                0,0,1,0,0,
                0,0,0,1,0
        }));
        
        mGreenColorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0.5f,0,0,0,0,
                0,2,0,0,0,
                0,0,0.5f,0,0,
                0,0,0,1,0
        }));
        
        mYellowColorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                2,0,0,0,0,
                0,2,0,0,0,
                0,0,0.5f,0,0,
                0,0,0,1,0
        }));
    }

    private ColorMatrixColorFilter getColorFilter(String viewType) {
        if (NxnWalletView.TYPE_DST.equals(viewType)) {
            return mGreenColorFilter;
        } else if(NxnWalletView.TYPE_BSC.equals(viewType)) {
            return mYellowColorFilter;
        } else {
            return mDefaultColorFilter;
        }
    }

    private Bitmap getLineBitmap(String viewType) {
        if (!lineMap.containsKey(viewType)) {
            int index = mRandom.nextInt(lineArray.length);
            lineMap.put(viewType, index);
        }
        int index = lineMap.get(viewType);
        int resId = lineArray[index];
        Bitmap bitmap = null;

        if (cacheBitmapMap.containsKey(resId)) {
            Bitmap srcBitmap = cacheBitmapMap.get(resId);
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                bitmap = srcBitmap;
            }
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), resId);
            cacheBitmapMap.put(resId, bitmap);
        }

        Long lasttime = lineTimeMap.get(viewType);
        long hasTime = lasttime == null ? 0 : System.currentTimeMillis() - lasttime;
        if (hasTime == 0 || hasTime >= 40) {
            index++;
            if (index >= lineArray.length) {
                index = 0;
            }
            lineTimeMap.put(viewType, System.currentTimeMillis());
        }
        lineMap.put(viewType, index);
        return bitmap;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure()");

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "onLayout("+changed+","+left+","+top+", "+right+", "+bottom);
    }

    public void setPath(List<PathData> pathList) {
        if (null == mPathPaint) {
            mPathPaint = new Paint();
            mPathPaint.setStrokeWidth(3);
            mPathPaint.setColor(Color.GREEN);
            mPathPaint.setStyle(Paint.Style.STROKE);

            mPathSPaint = new Paint();
            mPathSPaint.setStrokeWidth(3);
            mPathSPaint.setColor(Color.RED);
            mPathSPaint.setStyle(Paint.Style.STROKE);

            mLinePaint = new Paint();
            mLinePaint.setStrokeWidth(3);
            mLinePaint.setColor(Color.GREEN);
        }
        if (null == mPaths) {
            mPaths = new ArrayList<>();
        }
        mPaths.clear();
        if (null != pathList) {
            mPaths.addAll(pathList);
        }
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaths == null || mPaths.size() == 0) {
            return;
        }
        canvas.save();

        int rootX = 0, rootY=0, rootRadios=0;
        for (PathData pathData : mPaths) {
            if (NxnRootView.TYPE_ROOT.equals(pathData.type)) {
                rootX = pathData.centerX;
                rootY = pathData.centerY;
                rootRadios = pathData.radios;
                break;
            }
        }

        for (PathData pathData : mPaths) {


            if (!NxnRootView.TYPE_ROOT.equals(pathData.type)) {

                double[][] array = getIntersection(rootX, rootY, rootRadios, pathData.centerX, pathData.centerY, pathData.radios);
                double[][] array2 = getIntersection(pathData.centerX, pathData.centerY, pathData.radios, rootX, rootY, rootRadios);
                if (array != null && array.length == 2) {
                    double[] xy1 = array[0];
                    double[] xy2 = array2[0];

                    
                    int lcx = (int) (xy1[0] + (xy2[0]-xy1[0]) /2);
                    int lcy = (int) (xy1[1] + (xy2[1] - xy1[1]) / 2);

                    Bitmap bitmap = getLineBitmap(pathData.type);
                    Matrix matrix = new Matrix();
                    int bW = bitmap.getWidth();
                    int bH = bitmap.getHeight();




                    
                    double degree = getDegree(rootX, rootY, pathData.centerX, pathData.centerY);


                    
                    double distance = getPointDistance((int) xy1[0], (int) xy1[1], (int) xy2[0], (int) xy2[1]);
                    float scaleY = (float) ((distance+50) / bH);
                    float scaleX = scaleY;
                    matrix.setScale(scaleX, scaleY);

                    int toX = (int) (lcx - bW/2*scaleX);
                    int toY = (int) (lcy - bH/2*scaleY);

                    matrix.postTranslate(toX, toY);

                    matrix.postRotate((float) degree, lcx, lcy);



                    mLinePaint.setColorFilter(getColorFilter(pathData.type));

                    canvas.drawBitmap(bitmap, matrix, mLinePaint);



                    

                }

            }
        }


        canvas.restore();
    }


    
    public double[] test(double x1, double y1, double r1, double x2, double y2, double r2) {
        if (x1 != x2) {
            Log.i(TAG, "test[("+x1+","+y1+"),("+x2+","+y2+")]");

            double k = Math.abs(y2-y1)/Math.abs(x2-x1);
            Log.i(TAG, "k="+k);
            double dx = r1 * k;
            double dy = Math.sqrt(r1*r1 - dx*dx);
            double x11 = x1+dx;
            double y11 = y1-dy;

            Log.i(TAG, "k="+k);

            return new double[]{x11, y11};
        } else {
            return new double[]{x1, y1-r1};
        }
    }


    
    public double[][] getSomePoint(double cx, double cy, double r, double x1, double y1, int count) {
        
        double dis = 10;
        int leftCount = count / 2;
        int rightCount = count / 2 + count %2;
        double[][] result = new double[count][2];
        for (int i=1; i<=leftCount; i++) {
            double dx = i*dis;
            double l = 0;
            if (x1 > cx) {
                l = Math.abs(Math.abs(x1-cx)-dx);
            } else {
                l = Math.abs(Math.abs(x1-cx)+dx);
            }

            double dy = Math.sqrt(r*r-l*l);
            double x = x1-dx;
            double y = 0;
            if (y1 > cy) {
                y = cy+dy;
            } else {
                y = cy-dy;
            }
            result[i-1] = new double[]{x,y};
        }
        for (int i=1; i<=rightCount; i++) {
            double dx = i*dis;
            double l = 0;
            if (x1 > cx) {
                l = Math.abs(x1-cx)+dx;
            } else {
                l =Math.abs(Math.abs(x1-cx)-dx);
            }
            double dy = Math.sqrt(r*r-l*l);
            double x = x1+dx;
            double y = 0;
            if (y1 > cy) {
                y = cy+dy;
            } else {
                y = cy-dy;
            }
            result[leftCount+i-1] = new double[]{x,y};
        }
        return result;
    }


    
    public double[][] getIntersection(double x1, double y1, double r1, double x2, double y2, double r2) {
        
        double k,b;
        if (x1-x2 != 0) {
            k = (y1-y2)/(x1-x2);
            b = y1 - k*x1;
            double[] xy1 = getIntersection(x1,y1,r1,k,b);
            if (xy1 != null && xy1.length == 2) {
                double xy1x = xy1[0];
                double xy1y = xy1[1];
                if (x2 > x1) {
                    if (xy1x < x1) {
                        xy1x = x1+Math.abs(x1-xy1x);
                        xy1[0] = xy1x;
                    }
                } else if(x2 < x1) {
                    if (xy1x > x1) {
                        xy1x = x1-Math.abs(x1-xy1x);
                        xy1[0] = xy1x;
                    }
                }

                if (y2 > y1) {
                    if (xy1y < y1) {
                        xy1y = y1 + Math.abs(y1-xy1y);
                        xy1[1] = xy1y;
                    }
                } else if(y2 < y1) {
                    if (xy1y > y1) {
                        xy1y = y1 - Math.abs(y1-xy1y);
                        xy1[1] = xy1y;
                    }
                }
            }



            double[] xy2 = getIntersection(x2,y2,r2,k,b);
            return new double[][]{xy1, xy2};
        } else {
            double[] xy1 = {x1, y1-r1};
            double[] xy2 = {x2, y2-r2};

            return new double[][]{xy1, xy2};
        }
    }

    
    public double[] getIntersection(double x0, double y0, double r, double k, double b) {
        double[] res = new double[2];
        double a = 1 + k * k;
        double b1 = -2 * x0 + 2 * k * b - 2 * y0 * k;
        double c = x0 * x0 - 2 * b * y0 + y0 * y0 + b * b - r * r;
        double delta = b1 * b1 - 4 * a * c;
        if (delta< 0) {

            res[0] = Double.NaN;
            res[1] = Double.NaN;
        } else if (delta == 0) {

            res[0] = -b1 / (2 * a);
            res[1] = k * res[0] + b;
        } else {

            double x1 = (-b1 + Math.sqrt(delta)) / (2 * a);
            double x2 = (-b1 - Math.sqrt(delta)) / (2 * a);
            double y1 = k * x1 + b;
            double y2 = k * x2 + b;
            res[0] = x1;
            res[1] = y1;
        }
        return res;
    }


    
    public double getDegree(double x1, double y1, double x2, double y2) {
        if (x2 == x1) {
            if (y2 > y1) {
                return 180;
            } else if (y2 < y1){
                return 0;
            } else {
                return 0;
            }
        } else {
            
            double slope = (y2 - y1) / (x2 - x1);
            
            double radians = Math.atan(slope);
            
            double degrees = Math.toDegrees(radians);

            if (x2 > x1) {
                return 90+degrees;
            } else {
                return 180 + 90+degrees;
            }

        }

    }


    
    public double getPointDistance(int x1, int y1, int x2, int y2) {
        
        double diffX = x2 - x1;
        double diffY = y2 - y1;

        
        double squareDiffX = Math.pow(diffX, 2);
        double squareDiffY = Math.pow(diffY, 2);

        
        double distance = Math.sqrt(squareDiffX + squareDiffY);
        return distance;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != cacheBitmapMap && !cacheBitmapMap.isEmpty()) {
            for (Bitmap bitmap : cacheBitmapMap.values()){
                if (null != bitmap && !bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
            cacheBitmapMap.clear();
        }
    }
}
