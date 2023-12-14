package com.app.root.wallet;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WalletBubbleLayoutView extends FrameLayout {
    private Map<String,DownEvent> mDownMap;
    private Map<String,PathData> mPathDataMap;
    private Map<String,Point> mLocMap;
    private Vibrator mVibrator;
    private boolean isMoving = false;
    private static final String TAG = "WalletBubble";
    private ViewGroup mGroupVew;
    private OnClickDetectorListener mDetectorListener;

    public WalletBubbleLayoutView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WalletBubbleLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WalletBubbleLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public WalletBubbleLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mDownMap = new HashMap<>();
        mLocMap = new HashMap<>();
        mPathDataMap = new HashMap<>();
        inflate(getContext(), R.layout.layout_wallet_bubbles, this);
        mGroupVew = findViewById(R.id.rootLayout);
        setTouchListener(findViewById(R.id.bubbleIv1));
        setTouchListener(findViewById(R.id.bubbleIv2));
        setTouchListener(findViewById(R.id.bubbleIv3));
        setTouchListener(findViewById(R.id.bubbleIv4));
        setTouchListener(findViewById(R.id.bubbleIv5));
        setTouchListener(findViewById(R.id.bubbleIv6));
        setTouchListener(findViewById(R.id.bubbleIv7));
        setTouchListener(findViewById(R.id.bubbleIv8));
        setTouchListener(findViewById(R.id.bubbleIv9));
        setTouchListener(findViewById(R.id.bubbleIv10));
        NxnWalletView walletView = findViewById(R.id.walletView);
        walletView.setType(NxnWalletView.TYPE_BSC);
        mVibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void setOnClickDetectorListener(OnClickDetectorListener listener){
        this.mDetectorListener = listener;
    }


    private void setTouchListener(View view) {
        view.setOnTouchListener((view1, motionEvent) -> {
            return onChildTouch(view1, motionEvent);
        });
    }

    private void onChildTouchUp(View view, MotionEvent motionEvent) {
        isMoving = false;
        String type = view.getId()+"";
        if (!TextUtils.isEmpty(type)) {
            PathData pathData =  mPathDataMap.get(type);
            if (null != pathData ) {
                
                Point point = getLocation(type);

                int width = pathData.rect.width();
                int height = pathData.rect.height();
                int startX =pathData.centerX - width/2;
                int startY = pathData.centerY - height/2;
                int dx = point.x-pathData.centerX;
                int dy = point.y-pathData.centerY;

                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                valueAnimator.setDuration(550);
                valueAnimator.setInterpolator(new OvershootInterpolator());
                valueAnimator.addUpdateListener(valueAnimator1 -> {
                    int value = (int) valueAnimator1.getAnimatedValue();
                    int left = startX + (int)(dx*value/100);
                    int top = startY + (int)(dy*value/100);
                    int right = left + width;
                    int bottom = top + height;
                    view.layout(left, top, right, bottom);
                    calMovePath(view, null);
                });
                valueAnimator.start();

            } else if(pathData != null){
                
            }
        }
        resumeFloatingAnmi(view);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = mGroupVew.getChildCount();
        for (int i=0; i<childCount; i++) {
            View childView = mGroupVew.getChildAt(i);
            if (childView instanceof ImageView) {
                int w = childView.getWidth();
                int h = childView.getHeight();
                int l = childView.getLeft();
                int t = childView.getTop();
                int cx = l + w/2;
                int cy = t + h/2;
                saveLocation(getViewType(childView), cx, cy);
            }
        }

        startFloatingAnmi();
    }

    private boolean onChildTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ViewParent view1 = getParent();
            view1.requestDisallowInterceptTouchEvent(true);
            String viewType = view.getId()+"";
            if (!TextUtils.isEmpty(viewType)) {
                DownEvent event = null;
                if (mDownMap.containsKey(viewType)) {
                    event = mDownMap.get(viewType);
                    event.x = motionEvent.getRawX();
                    event.y = motionEvent.getRawY();
                } else {
                    event = new DownEvent(motionEvent.getRawX(), motionEvent.getRawY());
                }
                mDownMap.put(viewType, event);
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float rawX = motionEvent.getRawX();
            float rawY = motionEvent.getRawY();
            String viewType = view.getId()+"";
            DownEvent event = mDownMap.get(viewType);
            float startX = event.x;
            float startY = event.y;
            int dx = (int) (rawX - startX);
            int dy = (int) (rawY - startY);
            onChildMove(view, dx, dy);
            startX = motionEvent.getRawX();
            startY = motionEvent.getRawY();
            event.x = startX;
            event.y = startY;
            mDownMap.put(viewType, event);
            return true;
        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            onChildTouchUp(view, motionEvent);
            ViewParent view1 = getParent();
            view1.requestDisallowInterceptTouchEvent(false);
        }
        return true;
    }


    private void onChildMove(View view, int dx, int dy) {
        isMoving = true;
        int toLeft = view.getLeft()+dx;
        int toTop = view.getTop()+dy;
        int toRight = view.getRight()+dx;
        int toBottom = view.getBottom()+dy;
        if (toLeft < 0) {
            toLeft = view.getLeft();
            toRight = view.getRight();
        }
        if (toTop < 0) {
            toTop = view.getTop();
            toBottom = view.getBottom();
        }
        if (toRight > getWidth()) {
            toRight = view.getRight();
            toLeft = view.getLeft();
        }
        if (toBottom > getHeight()) {
            toBottom = view.getBottom();
            toTop = view.getTop();
        }

        PathData pathData = calMovePath(view, new Rect(toLeft, toTop, toRight, toBottom));
        if (pathData != null && (pathData.isFirstSelected || !pathData.selected)) {
            view.layout(toLeft, toTop, toRight, toBottom);
            if (pathData.isFirstSelected) {
                onChildHited(view, pathData.toType);
            }
        }

        
    }

    private void onChildHited(View view, String toType){
        playVibrator();
        if (null != mDetectorListener) {
            mDetectorListener.onHit("", "");
        }
    }

    
    public void setTypeCount(String type, int count) {

        TextView bottomTx = findViewById(R.id.bootomTypeTv);
        if (NxnWalletView.TYPE_ETH.equalsIgnoreCase(type)) {
            bottomTx.setText("ethereum");
        } else if(NxnWalletView.TYPE_DST.equalsIgnoreCase(type)) {
            bottomTx.setText("DST");
        } else if(NxnWalletView.TYPE_BSC.equalsIgnoreCase(type)) {
            bottomTx.setText("BSC");
        }

        NxnWalletView walletView = findViewById(R.id.walletView);
        if (null != walletView) {
            walletView.setType(type);
            walletView.setCount(count);
        }
    }

    private void playVibrator() {
        if (mVibrator.hasVibrator()) {
            mVibrator.vibrate(300);
        }
    }

    
    private PathData calMovePath(View view, Rect viewToRect) {
        int childCount = mGroupVew.getChildCount();
        int viewIndex = indexOfChild(view);
        String viewType = getViewType(view);
        

        
        PathData viewToPathData = createPathData(view, viewToRect);
        if (null == viewToPathData) {
            Log.w(TAG, "create null return");
            return null;
        }
        List<PathData> pathList = new ArrayList<>();
        pathList.add(viewToPathData);


        
        NxnWalletView walletView = findViewById(R.id.walletView);
        PathData otherPathData = createPathData(walletView, null);
        if (null != otherPathData) {
            pathList.add(otherPathData);
        }

        
        if (pathList.size() > 1) {
            for (int i=0; i<pathList.size(); i++) {
                PathData data = pathList.get(i);
                data.setSelected(false);
                for (int j=0; j<pathList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    if (isHited(data, pathList.get(j))) {
                        data.setSelected(true);
                        if (data.isFirstSelected) {
                            data.setToType(pathList.get(j).type);
                        } else {
                            data.setToType("");
                        }
                    }
                }
                if (isToEdge(data)) {
                    data.isToEdge = true;
                } else {
                    data.isToEdge = false;
                }

                String keyType = data.type;
                PathData pData = null;
                if (!mPathDataMap.containsKey(keyType)) {
                    mPathDataMap.put(keyType, data);
                    pData = data;
                } else {
                    pData = mPathDataMap.get(keyType);
                    pData.updateData(data);
                    mPathDataMap.put(keyType, pData);
                }
                if (pData.isSelecteChanged) {
                    setViewSelected(mGroupVew.getChildAt(data.viewIndex), data.selected);
                }
            }
        } else {
            Log.w(TAG, "pasList="+pathList.size());
        }
        return mPathDataMap.get(viewType);
    }


    
    private boolean isToEdge(PathData pData1) {
        if (pData1 == null || pData1.rect == null) {
            return false;
        }
        int left = pData1.rect.left;
        int top = pData1.rect.top;
        int right = left + pData1.rect.width();
        int bottom = top + pData1.rect.height();
        int magin = 20;
        if (left - magin <=0) {
            
            return true;
        } else if(top -magin <=0) {
            return true;
        } else if(right+magin > getWidth()) {
            return true;
        } else if(bottom + magin > getHeight()) {
            return true;
        }
        return false;
    }

    
    public PathData createPathData(View view, Rect toRect) {
        if (null == view) {
            return null;
        }
        int viewIndex = mGroupVew.indexOfChild(view);
        String type = getViewType(view);
        if (TextUtils.isEmpty(type)) {
            Log.w(TAG, "createPathData() type is null "+view);
            return null;
        }
        Rect rect1 = null;
        if (toRect != null) {
            rect1 = toRect;
        } else {
            int left = (int) view.getX();
            int top = (int) view.getY();
            int right = left + view.getWidth();
            int bottom = top + view.getHeight();
            rect1 = new Rect(left, top, right, bottom);
        }


        Rect rect = rect1;
        PathData pathData = new PathData(type, rect, null);
        pathData.viewIndex = viewIndex;
        return pathData;
    }

    
    private boolean isHited(PathData pData1, PathData pData2) {
        if (pData1 == null || pData1.rect == null || pData2 == null || pData2.rect == null) {
            return false;
        }
        
        int x1 = pData1.rect.centerX();
        int y1 = pData1.rect.centerY();

        
        int x2 = pData2.rect.centerX();
        int y2 = pData2.rect.centerY();

        
        int allsum = pData1.rect.height()/2 + pData2.rect.height()/2 - 20;

        
        double distance = getPointDistance(x1, y1, x2, y2);
        if (distance <= allsum) {
            return true;
        } else {
            return false;
        }
    }

    
    public void setViewSelected(View view, boolean hited) {
        if (view instanceof NxnWalletView) {
            ((NxnWalletView)view).setSelected(hited);
        } else if(view instanceof NxnRootView) {

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



    
    private Map<String, ObjectAnimator> mFloatingAnmiMap = new HashMap<>();
    private int walletViewIndex = -1;
    private void startFloatingAnmi() {
        stopFloatingAnmi(null);
        String keyType = "";
        int childCount = mGroupVew.getChildCount();
        for (int i=0; i<childCount; i++) {
            View view = mGroupVew.getChildAt(i);
            if (view instanceof ImageView) {
                ObjectAnimator animator = ViewAnmiUtils.bubbleFloat(view, 2000+i*500, 8+i*2, -1);
                animator.start();
                keyType = getViewType(view);
                mFloatingAnmiMap.put(keyType, animator);
                walletViewIndex = i;
            }
        }
        if (!mFloatingAnmiMap.isEmpty()) {
            mFloatingAnmiMap.get(keyType).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (!isMoving && walletViewIndex != -1) {
                        View view = mGroupVew.getChildAt(walletViewIndex);
                        calMovePath(view, null);
                    }
                }
            });
        }
    }

    
    private void resumeFloatingAnmi(View view) {
        if (null == view || !(view instanceof NxnWalletView)) {
            return;
        }
        String viewType = getViewType(view);
        ObjectAnimator animator = mFloatingAnmiMap.get(viewType);
        if (animator != null && animator.isPaused()) {
            animator.resume();
        } else {
            if (animator != null) {
                animator.cancel();
                mFloatingAnmiMap.remove(viewType);
            }
            int i = indexOfChild(view);
            ObjectAnimator newAnimator = ViewAnmiUtils.bubbleFloat(view, 2000+i*500, 10+i*2, -1);
            newAnimator.start();
            mFloatingAnmiMap.put(viewType, newAnimator);
        }
    }

    
    private void stopFloatingAnmi(View view) {
        if (view == null) {
            if (!mFloatingAnmiMap.isEmpty()) {
                for (Map.Entry<String,ObjectAnimator> entry : mFloatingAnmiMap.entrySet()) {
                    entry.getValue().cancel();
                }
            }
            mFloatingAnmiMap.clear();
        } else if (view instanceof NxnWalletView) {
            String viewType = getViewType(view);
            ObjectAnimator animator = mFloatingAnmiMap.get(viewType);
            if (animator != null && animator.isRunning()) {
                animator.pause();
            }
        }
    }


    private String getViewType(View v){
        return v.getId()+"";
    }

    private Point getLocation(String viewType) {
        return mLocMap.get(viewType);
    }

    private void saveLocation(String viewType, int cx, int cy) {
        mLocMap.put(viewType, new Point(cx, cy));
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopFloatingAnmi(null);
    }
}
