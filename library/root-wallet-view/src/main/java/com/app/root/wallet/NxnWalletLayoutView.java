package com.app.root.wallet;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Path;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NxnWalletLayoutView extends RelativeLayout{

    private static final String TAG = "NxnWalletLayoutView";
    private BgBoradView mBordView;
    private Map<String,PathData> mPathDataMap;
    private Map<String,DownEvent> mDownMap;
    private Map<String,ClickCountData> mClickMap;
    private Vibrator mVibrator;
    private OnClickDetectorListener mDetectorListener;
    private boolean isMoving = false;
    private final float MAX_CELL_SCALE = 1.15f;
    private final float MIN_CELL_SCALE = 0.85f;
    private boolean hasLayouted = false;
    public NxnWalletLayoutView(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public NxnWalletLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public NxnWalletLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public NxnWalletLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        NxnRootView root  =new NxnRootView(context);
        mBordView = new BgBoradView(context);
        mBordView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mBordView);
        addView(root);
        setChildListener(root);
        mClickMap = new HashMap<>();
        List<NxnWalletView> list = NxnWalletView.createViews(context);
        for (int i=0; i<list.size(); i++) {
            NxnWalletView view = list.get(i);
            addView(view);
            setChildListener(view);
            String viewType = getViewType(view);
            mClickMap.put(viewType, new ClickCountData(viewType, getCount(viewType)));
        }
        mPathDataMap = new HashMap<>();
        mDownMap = new HashMap<>();

        mVibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);

        
        calCellViewScale();
    }

    public void setOnClickDetectorListener(OnClickDetectorListener listener){
        this.mDetectorListener = listener;
    }

    private void setChildListener(View childView) {
        childView.setOnClickListener(view -> {
            onChildClick(view);
        });
        childView.setOnLongClickListener(view -> {
            onChildLongClick(view);
            return true;
        });
        childView.setOnTouchListener((view, motionEvent) -> {
            return onChildTouch(view, motionEvent);
        });
    }

    private void playVibrator() {
        if (mVibrator.hasVibrator()) {
            mVibrator.vibrate(300);
        }
    }

    
    public void updateWalletCount(int dstCount, int ethCount, int bscCount) {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i=0; i<childCount; i++) {
                View view = getChildAt(i);
                if (view instanceof NxnWalletView) {
                    String viewType = getViewType(view);
                    if (NxnWalletView.TYPE_DST.equals(viewType)) {
                        ((NxnWalletView)view).setCount(dstCount);
                    } else if (NxnWalletView.TYPE_ETH.equals(viewType)) {
                        ((NxnWalletView)view).setCount(ethCount);
                    } else if (NxnWalletView.TYPE_BSC.equals(viewType)) {
                        ((NxnWalletView)view).setCount(bscCount);
                    }
                }
            }
        }
    }

    
    private void onChildClick(View view) {
        String type = getViewType(view);
        relayoutClickCellView(type);
        if (null != mDetectorListener) {
            mDetectorListener.onClick(type);
        }
    }

    
    private void relayoutClickCellView(String type){
        if(null == mClickMap || mClickMap.isEmpty() || !mClickMap.containsKey(type)){
            return;
        }
        
        String maxType = "";
        int maxCount = 0;
        int minCount = 0;
        for(Map.Entry<String, ClickCountData> entry : mClickMap.entrySet()){
            int count = entry.getValue().count;
            if (count > maxCount){
                maxCount = count;
                maxType = entry.getKey();
            }
            if(count < minCount){
                minCount = count;
            }
        }
        int centerCount = 0;
        for(Map.Entry<String, ClickCountData> entry : mClickMap.entrySet()){
            int count = entry.getValue().count;
            if (count < maxCount && count > minCount){
                centerCount = count;
                break;
            }
        }
        if(type.equals(maxType) && (centerCount > 0 && maxCount > centerCount)){
            
            
            Log.i(TAG, "has max count");
            return;
        }
        int newCount = maxCount + 1;
        setCount(type, newCount);
        ClickCountData countData =  mClickMap.get(type);
        countData.count = newCount;
        mClickMap.put(type, countData);
        if (calCellViewScale()){
            Log.i(TAG, "postInvalidate()");
            requestLayout();
        }
    }

    
    private void onChildLongClick(View view) {
        String type = getViewType(view);
        setViewSelected(view, true);
        if (null != mDetectorListener) {
            mDetectorListener.onLongClick(type);
        }
    }

    private void onChildHited(View view, String toType){
        String type = getViewType(view);
        playVibrator();
        if (null != mDetectorListener) {
            mDetectorListener.onHit(type, toType);
        }
    }

    private boolean onChildTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            
            ViewParent parentView = getParent();
            parentView.requestDisallowInterceptTouchEvent(true);

            String viewType = getViewType(view);
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
                setViewSelected(view, true);
            }

            stopFloatingAnmi(view);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float rawX = motionEvent.getRawX();
            float rawY = motionEvent.getRawY();
            String viewType = getViewType(view);
            DownEvent event = mDownMap.get(viewType);
            if (event == null) {
                event = new DownEvent(rawX, rawY);
                mDownMap.put(viewType, event);
            }
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
            
            ViewParent parentView = getParent();
            parentView.requestDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    private void onChildTouchUp(View view, MotionEvent motionEvent) {
        isMoving = false;
        String type = getViewType(view);
        if (!TextUtils.isEmpty(type)) {
           PathData pathData =  mPathDataMap.get(type);
           if (null != pathData && (pathData.selected || pathData.isToEdge || type.equals(NxnRootView.TYPE_ROOT))) {
               
              Point point = getLocation(type);
              if (point == null){
                  Log.e(TAG, "up location is no found");
                  return;
              }

              int width = pathData.rect.width();
              int height = pathData.rect.height();
              int startX =pathData.centerX - width/2;
              int startY = pathData.centerY - height/2;
               int dx = point.x-pathData.centerX;
               int dy = point.y-pathData.centerY;


               ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
               valueAnimator.setDuration(550);
               if(type.equals(NxnRootView.TYPE_ROOT)){
                   valueAnimator.setInterpolator(new OvershootBoundsInterpolator());
               } else {
                   valueAnimator.setInterpolator(new OvershootInterpolator());
               }
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
               
               saveLocation(type, pathData.centerX, pathData.centerY);
           }
        }
        setViewSelected(view, false);
        resumeFloatingAnmi(view);
        if (null != mDetectorListener) {
            mDetectorListener.onTouchUp(type);
        }
    }

    private void onChildMove(View view, int dx, int dy) {
        if (!isViewSelected(view)) {
            Log.w(TAG, "no selected :"+view);
            return;
        }
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
        if (pathData != null ) {
            view.layout(toLeft, toTop, toRight, toBottom);
            if (pathData.isFirstSelected) {
                onChildHited(view, pathData.toType);
            }
        }

        
    }


    
    private Map<String, ObjectAnimator> mFloatingAnmiMap = new HashMap<>();
    private int walletViewIndex = -1;
    private void startFloatingAnmi() {
        stopFloatingAnmi(null);
        String keyType = "";
        int childCount = getChildCount();
        for (int i=0; i<childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof NxnWalletView) {
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
                        View view =  getChildAt(walletViewIndex);
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


    
    private PathData calMovePath(View view, Rect viewToRect) {
        int childCount = getChildCount();
        int viewIndex = indexOfChild(view);
        String viewType = getViewType(view);
        

        
        PathData viewToPathData = createPathData(view, viewToRect);
        if (null == viewToPathData) {
            return null;
        }
        List<PathData> pathList = new ArrayList<>();
        pathList.add(viewToPathData);
        for (int i = 0; i < childCount; i++) {
            if (i == viewIndex) {
                continue;
            }
            View child = getChildAt(i);
            if (child instanceof NxnWalletView || child instanceof NxnRootView) {
                PathData otherPathData = createPathData(child, null);
                if (null != otherPathData) {
                    pathList.add(otherPathData);
                }
            }
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

                PathData pData = null;
                String keyType = data.type;
                if (!mPathDataMap.containsKey(keyType)) {
                    mPathDataMap.put(keyType, data);
                    pData = data;
                } else {
                    pData = mPathDataMap.get(keyType);
                    pData.updateData(data);
                    mPathDataMap.put(keyType, pData);
                }
                if (pData.isSelecteChanged) {
                    setViewSelected(getChildAt(data.viewIndex), data.selected);
                }
            }
        }
        mBordView.setPath(new ArrayList<>(mPathDataMap.values()));
        return mPathDataMap.get(viewType);
    }



    
    public PathData createPathData(View view, Rect toRect) {
        if (null == view) {
            return null;
        }
        int viewIndex = indexOfChild(view);
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


        Rect rect = getScaleRect(rect1, view.getScaleX(), view.getScaleY());
        PathData pathData = new PathData(type, rect, getCirclePath(rect));
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


    
    private boolean calCellViewScale() {
        if (mClickMap.isEmpty()) {
            Log.e(TAG, "click data is empty");
            return false;
        }
        List<ClickCountData> list = new ArrayList<>(mClickMap.values());

        Collections.sort(list, new Comparator<ClickCountData>() {
            @Override
            public int compare(ClickCountData count1, ClickCountData count2) {
                return count2.count - count1.count;
            }
        });

        int maxCount = 0,centerCount=0, minCount=0;
        int listSize = list.size();
        if (listSize > 2) {
            int lastIndex = listSize - 1;
            maxCount = list.get(0).count;
            minCount = list.get(lastIndex).count;
            if (maxCount == minCount) {
                for (int i=0; i<listSize; i++){
                    list.get(i).updateScale(1f);
                }
            } else {
                for (int i=0; i<listSize; i++){
                    int count = list.get(i).count;
                    if (count < maxCount && count > minCount) {
                        centerCount = count;
                        break;
                    }
                }
                if (centerCount > 0) {
                    for (int i=0; i<listSize; i++) {
                        if (i == 0){
                            list.get(0).updateScale(MAX_CELL_SCALE);
                        } else if(i < lastIndex){
                            list.get(i).updateScale(1f);
                        } else {
                            list.get(i).updateScale(MIN_CELL_SCALE);
                        }
                    }
                } else {
                    for (int i=0; i<listSize; i++) {
                        if (list.get(i).count == maxCount){
                            list.get(i).updateScale(MAX_CELL_SCALE);
                        } else {
                            list.get(i).updateScale(1f);
                        }
                    }
                }
            }
        } else if(listSize == 2){
            maxCount = list.get(0).count;
            minCount = list.get(1).count;
            if (maxCount != minCount) {
                list.get(0).updateScale(MAX_CELL_SCALE);
            }
            list.get(1).updateScale(1f);
        } else if(listSize == 1) {
            list.get(0).updateScale(1f);
        }

        boolean hasUpdated = false;
        for (ClickCountData data : list) {
            String keyType = data.viewType;
            ClickCountData oldData = mClickMap.get(keyType);
            if (data.isUpdated) {
                hasUpdated = true;
            }
            oldData.updateScale(data.getScale());
            mClickMap.put(keyType, oldData);
        }
        return hasUpdated;
    }


    private float getCellViewScale(String viewType) {
        float scale = 0;
        if (mClickMap.containsKey(viewType)) {
            scale = mClickMap.get(viewType).getScale();
        }
        if (scale <= 0) {
            return 1f;
        } else {
            return scale;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        int wh = Math.min(width, height);
        int rootWith = wh/4;
        int rootHeight = rootWith;

        int maxSuggestWith = (wh - rootWith-rootWith/4)/2;
        int suggestWith = (int) (maxSuggestWith / MAX_CELL_SCALE);
        int suggestHegiht = suggestWith;





        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof NxnWalletView) {
                String type = getViewType(child);
                float scale = getCellViewScale(type);
                int scaleSuggestWith = (int) (suggestWith*scale);
                int ScaleSuggestHegiht = (int) (suggestHegiht*scale);

                measureChild(child, scaleSuggestWith, ScaleSuggestHegiht);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = scaleSuggestWith;
                params.height = ScaleSuggestHegiht;
                child.setLayoutParams(params);
            } else if(child instanceof NxnRootView) {
                measureChild(child, rootWith, rootHeight);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = rootWith;
                params.height = rootHeight;
                child.setLayoutParams(params);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = getWidth();
        int height = getHeight();
        int l = 0, t = 0,r = 0, b = 0;
        int childCount = getChildCount();
        View rootView = null;

        if (width < height) {
            
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int childW = child.getMeasuredWidth();
                int childH = child.getMeasuredHeight();
                if (child instanceof NxnRootView) {
                    rootView = child;
                    
                    l = width / 2 - childW/2;
                    t = height / 2 - childH/2 + childH;
                    r = l + childW;
                    b = t + childH;
                    child.layout(l, t, r, b);

                    saveLocation(NxnRootView.TYPE_ROOT, l+childW/2, t+childH/2);
                } else if(child instanceof NxnWalletView){
                    String type = getViewType(child);
                    Point point = getLocation(type);
                    if (point != null) {
                        l = point.x - childW /2;
                        t = point.y - childH / 2;
                        r = l + childW;
                        b = t + childH;
                    } else {
                        if (NxnWalletView.TYPE_ETH.equalsIgnoreCase(type)) {
                            
                            l = childW/3; 
                            t = childH*2/3;
                            r = l + childW;
                            b = t + childH;
                        } else if(NxnWalletView.TYPE_DST.equalsIgnoreCase(type)) {
                            
                            l = width - childW - childW/3; 
                            t = childH / 2;  
                            r = l + childW;
                            b = t + childH;
                        } else if(NxnWalletView.TYPE_BSC.equalsIgnoreCase(type)) {
                            
                            l = width - childW - childW/2;
                            b = height - childH/3;
                            r = l + childW;
                            t = b - childH;
                        }
                        
                        saveLocation(type, l+childW/2, t+childH/2);
                    }
                    if (l > 0) {
                        child.layout(l, t, r,b);
                    }
                } else {
                    child.layout(left, top, right, bottom);
                }
            }

        } else {
            
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int childW = child.getMeasuredWidth();
                int childH = child.getMeasuredHeight();
                if (child instanceof NxnRootView) {
                    rootView = child;
                    
                    l = width / 2 - childW/2 + childW;
                    t = height / 2 - childH/2;
                    r = l + childW;
                    b = t + childH;
                    child.layout(l, t, r, b);
                    saveLocation(NxnRootView.TYPE_ROOT, l+childW/2, t+childH/2);
                } else if(child instanceof NxnWalletView){
                    String type = ((NxnWalletView)child).getType();
                    Point point = getLocation(type);
                    if (point != null) {
                        l = point.x - childW /2;
                        t = point.y - childH / 2;
                        r = l + childW;
                        b = t + childH;
                    } else {
                        if (NxnWalletView.TYPE_ETH.equalsIgnoreCase(type)) {
                            
                            l = childW/3; 
                            t = childH/3;
                            r = l + childW;
                            b = t + childH;
                        } else if(NxnWalletView.TYPE_DST.equalsIgnoreCase(type)) {
                            
                            l = childW*2/3; 
                            t = childH+ childH / 3;  
                            r = l + childW;
                            b = t + childH;
                        } else if(NxnWalletView.TYPE_BSC.equalsIgnoreCase(type)) {
                            
                            l = width - childW - childW/2;
                            b = height - childH/3;
                            r = l + childW;
                            t = b - childH;
                        }
                        
                        saveLocation(type, l+childW/2, t+childH/2);
                    }
                    if (l > 0) {
                        child.layout(l, t, r,b);
                    }
                } else {
                    child.layout(left, top, right, bottom);
                }
            }
        }


        
        calMovePath(rootView, null);
        startFloatingAnmi();
        hasLayouted = true;
    }


    public Rect findCanRect(Rect rootRect, List<Rect> usedRects, int childW, int childH) {
        int parentW = getMeasuredWidth();
        int parentH = getMeasuredHeight();
        int tH = parentH - rootRect.top;
        int bH = parentH - rootRect.top - rootRect.height();
        int topCount = 0;
        int topClumCount = 0;
        int botoomCount = 0;
        if (tH > childH && childW > 0) {
            topClumCount = parentW / childW;
            topCount = tH / childH;
        }

        int rectSize = usedRects.size();
        if (topCount > 0 && (topCount+topClumCount) > rectSize) {
            
            int size = rectSize % topClumCount;
            int hSize = 6;
            int l = size * childW;
            int t = childW / 5;
            int r = l + childW;
            int b = t + childW;
            return new Rect(l, t, r, b);
        }

        if (bH > childH && childW > 0) {
            botoomCount = parentW / childW;
        }
        if (botoomCount > 0 && botoomCount > (usedRects.size() - topCount)) {
            int size = usedRects.size() - topCount;
            int  l = size * childW;
            int t = bH;
            int r = l + childW;
            int b = t + childW;
            return new Rect(l, t, r, b);
        }
        int r = 0+childW;
        int b = 0+childH;
        return new Rect(0, 0, r, b);
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
            if (i == 0) {
                path.moveTo(x+centerX, y+centerY);
            } else {
                path.lineTo(x+centerX, y+centerY);
            }
        }
        path.close();
        return path;
    }


    
    public double getPointDistance(int x1, int y1, int x2, int y2) {
        
        double diffX = x2 - x1;
        double diffY = y2 - y1;

        
        double squareDiffX = Math.pow(diffX, 2);
        double squareDiffY = Math.pow(diffY, 2);

        
        double distance = Math.sqrt(squareDiffX + squareDiffY);
        return distance;
    }


    
    public Rect getScaleRect(Rect rect, float scaleX, float scaleY) {
        int width = (int) (rect.width() * scaleX / 2);
        int height = (int) (rect.height() * scaleY / 2);
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int left = centerX - width;
        int top = centerY - height;
        int right = left + width*2;
        int bottom = top + height*2;
        return new Rect(left,top, right, bottom);
    }


    
    public double[][] getIntersection(double x1, double y1, double r1, double x2, double y2, double r2) {
        
        double k,b;
        if (x1-x2 != 0) {
            k = (y1-y2)/(x1-x2);
            b = y1 - k*x1;
        } else {
            k = 0;
            b = y1-y2;
        }
        double[] xy1 = getIntersection(x1,y1,r1,k,b);
        double[] xy2 = getIntersection(x2,y2,r2,k,b);
        return new double[][]{xy1, xy2};
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




    private SharedPreferences mSp;
    private SharedPreferences getSp() {
        if (mSp == null) {
            mSp = getContext().getSharedPreferences("nxnWSp", Context.MODE_PRIVATE);
        }
        return mSp;
    }

    private int setCount(String type, int newCount) {
        getSp().edit().putInt("count"+type, newCount).commit();
        return newCount;
    }

    private int getCount(String type) {
        String key = "count"+type;
        return getSp().getInt(key, 0);
    }

    private Point getLocation(String type) {
        String land = getWidth() > getHeight() ? "land" : "port";
        String keyX = land+"LocX"+type;
        String keyY = land+"LocY"+type;
        int x = getSp().getInt(keyX, -1);
        int y = getSp().getInt(keyY, -1);
        if (x != -1 && y != -1) {
            return new Point(x,y);
        }
        return null;
    }

    private void saveLocation(String type, int x, int y) {
        if (x != -1 && y!= -1) {
            String land = getWidth() > getHeight() ? "land" : "port";
            String keyX = land+"LocX"+type;
            String keyY = land+"LocY"+type;
            getSp().edit().putInt(keyX, x).putInt(keyY, y).commit();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getViewType(View view) {
        if (view == null) {
            return "";
        }
        if (view instanceof NxnWalletView) {
            return ((NxnWalletView)view).getType();
        } else if(view instanceof NxnRootView) {
            return NxnRootView.TYPE_ROOT;
        }
        return "";
    }

    
    private void setViewSelected(View view, boolean hited) {
        if (view instanceof NxnWalletView) {
            ((NxnWalletView)view).setSelected(hited);
        } else if(view instanceof NxnRootView) {

        }
    }

    private boolean isViewSelected(View view) {
        if (view instanceof NxnWalletView) {
            return ((NxnWalletView)view).isSelected();
        } else if(view instanceof NxnRootView) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopFloatingAnmi(null);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (isAttachedToWindow() && visibility==View.VISIBLE && hasLayouted) {
            startFloatingAnmi();
        }
    }




}
