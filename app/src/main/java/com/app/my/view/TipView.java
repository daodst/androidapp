

package com.app.my.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;



public class TipView extends View {

    private static final int STATUS_DOWN = 1;
    private static final int STATUS_UP = 2;
    
    private int mStatus = STATUS_UP;


    
    private int mSeparateLineColor ;

    
    private int mCorner = dip2px(6);

    private Paint mPaint; 
    private Paint doPaint; 

    private Path mPath; 

    private int mBorderMargin = dip2px(5); 
    private int mItemWidth = dip2px(50); 
    private int mItemHeight = dip2px(48); 
    private int mTriangleTop = dip2px(50); 
    private int mHalfTriangleWidth = dip2px(6); 
    private int mItemBorder; 
    private int realLeft; 

    private List<TipItem> mItemList = new ArrayList<>(); 
    private List<Rect> mItemRectList = new ArrayList<>(); 


    private OnItemClickListener onItemClickListener; 
    private int choose = -1; 
    private int x; 
    private int y; 

    private PopupWindow mPopupWindow;

    public TipView(Context context, View rootView,int x,int y,List<TipItem> mItemList) {
        super(context);

        this.x = x; 
        this.y = y;  
        

        this.mPopupWindow = mPopupWindow;

        initPaint(); 
        setTipItemList(mItemList); 
        
        
        

        initView(); 
    }

    private void initPaint() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(14));

        doPaint = new Paint();
        doPaint.setAntiAlias(true);
        doPaint.setStyle(Paint.Style.FILL);
        doPaint.setColor(Color.DKGRAY);
    }

    private void initView() {

        
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        
        if (y/2<mItemHeight) {
            mStatus = STATUS_DOWN; 
            mTriangleTop = y + dip2px(6); 
            mItemBorder = mTriangleTop + dip2px(7); 
        } else {
            mStatus = STATUS_UP;   
            mTriangleTop = y - dip2px(6);
            mItemBorder = mTriangleTop - dip2px(7);
        }

        
        realLeft = x - (mItemWidth * mItemList.size()) / 2;
        if (realLeft < 0) {
            
            realLeft = mBorderMargin;
            
            if(x-mCorner<=realLeft) {
                x = realLeft + mCorner * 2;
            }
        } else if (realLeft + (mItemWidth*mItemList.size()) > mScreenWidth) {
            
            realLeft -= realLeft + (mItemWidth*mItemList.size())-mScreenWidth+mBorderMargin;
            
            if(x+mCorner>=realLeft+mItemWidth*mItemList.size()) {
                x = realLeft + mItemWidth * mItemList.size() - mCorner * 2;
            }
        }

    }


    private void addView(ViewGroup rootView) {
        rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas); 
        switch (mStatus) {
            case STATUS_DOWN:
                drawItemDown(canvas);
                break;
            case STATUS_UP:
                drawItemUp(canvas); 
                break;
            default:
                break;
        }
    }

    private void drawItemDown(Canvas canvas) {

        
        mItemRectList.clear();

        mPath.reset(); 

        
        if(choose!=-1) {
            doPaint.setColor(Color.DKGRAY);
        } else {
            doPaint.setColor(Color.BLACK);
        }

        mPath.moveTo(x, mTriangleTop);
        mPath.lineTo(x - mHalfTriangleWidth, mItemBorder);
        mPath.lineTo(x + mHalfTriangleWidth, mItemBorder);
        canvas.drawPath(mPath, doPaint);


        for (int i = 0; i < mItemList.size(); i++) {

            
            if(choose==i){
                mPaint.setColor(Color.DKGRAY);
            }else {
                mPaint.setColor(Color.BLACK);
            }

            
            if (i == 0) {
                mPath.reset();
                mPath.moveTo(realLeft+mItemWidth  ,       mItemBorder);
                mPath.lineTo(realLeft+mCorner ,   mItemBorder);
                mPath.quadTo(realLeft,mItemBorder
                        , realLeft,mItemBorder+mCorner);
                mPath.lineTo(realLeft,mItemBorder+mItemHeight-mCorner);
                mPath.quadTo(realLeft,mItemBorder+mItemHeight
                        ,realLeft+mCorner,mItemBorder+mItemHeight);
                mPath.lineTo(realLeft+mItemWidth,mItemBorder+mItemHeight);
                canvas.drawPath(mPath, mPaint);
                
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft+mItemWidth, mItemBorder
                        ,realLeft+mItemWidth, mItemBorder+mItemHeight , mPaint);

            }else if (i == mItemList.size() - 1) {
                
                mPath.reset();
                mPath.moveTo(realLeft +mItemWidth*(mItemList.size()-1) , mItemBorder);
                mPath.lineTo(realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth - mCorner, mItemBorder);
                mPath.quadTo(realLeft +mItemWidth*(mItemList.size()-1)+ + mItemWidth, mItemBorder
                        , realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth, mItemBorder  + mCorner);
                mPath.lineTo( realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth,mItemBorder+mItemHeight-mCorner);
                mPath.quadTo(realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth,mItemBorder+mItemHeight,
                        realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth-mCorner,mItemBorder+mItemHeight);
                mPath.lineTo(realLeft +mItemWidth*(mItemList.size()-1),mItemBorder+mItemHeight);
                canvas.drawPath(mPath, mPaint);
                
            }else {

                
                canvas.drawRect(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth, mItemBorder
                        , realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder+mItemHeight, mPaint);
                
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder
                        ,realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder+mItemHeight , mPaint);


            }
            
            mItemRectList.add(new Rect(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth,mItemBorder,realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() -(i+1)) * mItemWidth+mItemWidth,mItemBorder+mItemHeight));


        }

        
        drawTitle(canvas);


    }

    
    private void drawItemUp(Canvas canvas) {
        mItemRectList.clear();

        mPath.reset();
        if(choose!=-1) {
            doPaint.setColor(Color.DKGRAY);
        } else {
            doPaint.setColor(Color.BLACK);
        }

        mPath.moveTo(x, mTriangleTop);
        mPath.lineTo(x - mHalfTriangleWidth, mItemBorder);
        mPath.lineTo(x + mHalfTriangleWidth, mItemBorder);
        canvas.drawPath(mPath, doPaint);

        for (int i = 0; i < mItemList.size(); i++) {

            if(choose==i){
                mPaint.setColor(Color.DKGRAY);
            }else {
                mPaint.setColor(Color.BLACK);
            }



            if (i == 0) {
                mPath.reset();
                mPath.moveTo(realLeft+mItemWidth  ,       mItemBorder-mItemHeight);
                mPath.lineTo(realLeft+mCorner ,   mItemBorder-mItemHeight);
                mPath.quadTo(realLeft,mItemBorder-mItemHeight
                        , realLeft,mItemBorder-mItemHeight+mCorner);
                mPath.lineTo(realLeft,mItemBorder-mCorner);
                mPath.quadTo(realLeft,mItemBorder
                        ,realLeft+mCorner,mItemBorder);
                mPath.lineTo(realLeft+mItemWidth,mItemBorder);
                canvas.drawPath(mPath, mPaint);

                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft+mItemWidth, mItemBorder - mItemHeight
                        ,realLeft+mItemWidth, mItemBorder , mPaint);

            }else if (i == mItemList.size() - 1) {
                mPath.reset();
                mPath.moveTo(realLeft +mItemWidth*(mItemList.size()-1) , mItemBorder - mItemHeight);
                mPath.lineTo(realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth - mCorner, mItemBorder - mItemHeight);
                mPath.quadTo(realLeft +mItemWidth*(mItemList.size()-1)+ + mItemWidth, mItemBorder - mItemHeight
                        , realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth, mItemBorder - mItemHeight + mCorner);
                mPath.lineTo( realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth,mItemBorder-mCorner);
                mPath.quadTo(realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth,mItemBorder,
                        realLeft +mItemWidth*(mItemList.size()-1) + mItemWidth-mCorner,mItemBorder);
                mPath.lineTo(realLeft +mItemWidth*(mItemList.size()-1),mItemBorder);
                canvas.drawPath(mPath, mPaint);
            }else {
                canvas.drawRect(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth, mItemBorder - mItemHeight
                        , realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder, mPaint);
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder - mItemHeight
                        ,realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth+ mItemWidth, mItemBorder , mPaint);

            }

            mItemRectList.add(new Rect(realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() - (i+1)) * mItemWidth,mItemBorder-mItemHeight,realLeft +mItemWidth*(mItemList.size()-1)-(mItemList.size() -(i+1)) * mItemWidth+mItemWidth,mItemBorder));



        }

        drawTitle(canvas);

    }

    
    private void drawTitle(Canvas canvas) {
        TipItem tipItem;
        for(int i = 0;i<mItemRectList.size();i++) {
            tipItem = mItemList.get(i);
            mPaint.setColor(tipItem.getTextColor());
            if (mStatus == STATUS_UP) {
                canvas.drawText(tipItem.getTitle(), mItemRectList.get(i).left +mItemWidth/2- getTextWidth(tipItem.getTitle(), mPaint) / 2, mItemBorder - mItemHeight / 2 +  getTextHeight(mPaint)/2, mPaint);
            } else if (mStatus == STATUS_DOWN) {
                canvas.drawText(tipItem.getTitle(), mItemRectList.get(i).left +mItemWidth/2- getTextWidth(tipItem.getTitle(), mPaint) / 2, mItemRectList.get(i).bottom - mItemHeight / 2 +  getTextHeight(mPaint)/2, mPaint);
            }
        }

    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
    }



    public void setTipItemList(final List<TipItem> list) {
        mItemList.clear();
        List<TipItem> items = list;
        for (TipItem item : items) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                item.setTitle(updateTitle(item.getTitle()));
            } else {
                item.setTitle("");
            }

            mItemList.add(item);
        }
    }

    private String updateTitle(String title) {
        int textLength = title.length();
        String suffix = "";
        while (getTextWidth(title.substring(0, textLength) + "...", mPaint) > mItemWidth - dip2px(10)) {
            textLength--;
            suffix = "...";
        }
        return title.substring(0, textLength) + suffix;
    }

    public void setSeparateLineColor(int separateLineColor) {
        mSeparateLineColor = separateLineColor;
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }




    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom-fontMetrics.descent-fontMetrics.ascent;
    }


    private float getTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mItemRectList.size(); i++) {
                    if (onItemClickListener != null && isPointInRect(new PointF(event.getX(), event.getY()), mItemRectList.get(i))) {
                        
                        choose = i;
                        
                        postInvalidate(mItemRectList.get(i).left,mItemRectList.get(i).top,mItemRectList.get(i).right,mItemRectList.get(i).bottom);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < mItemRectList.size(); i++) {
                    if (onItemClickListener != null && isPointInRect(new PointF(event.getX(), event.getY()), mItemRectList.get(i))) {
                        
                        onItemClickListener.onItemClick(mItemList.get(i).getTitle(),i);
                        choose = -1;
                    }
                }

                if (onItemClickListener != null) {
                    onItemClickListener.dismiss(); 
                }

                
                mPopupWindow.dismiss();

                return true;
        }
        return true;
    }

    private void removeView() {
        ViewGroup vg = (ViewGroup) this.getParent();
        if (vg != null) {
            vg.removeView(this);
        }
    }

    private boolean isPointInRect(PointF pointF, Rect targetRect) {
        if (pointF.x < targetRect.left) {
            return false;
        }
        if (pointF.x > targetRect.right) {
            return false;
        }
        if (pointF.y < targetRect.top) {
            return false;
        }
        if (pointF.y > targetRect.bottom) {
            return false;
        }
        return true;
    }

    private void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String name, int position);
        void dismiss();
    }

    public void setPopupWindow (PopupWindow pop) {
        this.mPopupWindow = pop;
    }



    public static class Builder {

        private OnItemClickListener onItemClickListener;
        private Context mContext;
        private View mRootView;
        private List<TipItem> mTipItemList = new ArrayList<>();
        private int mSeparateLineColor = Color.WHITE;
        private int x ,y;
        private PopupWindow mPopupWindow;

        public Builder(Context context, View rootView, int x, int y) {
            mContext = context;
            mRootView = rootView;
            this.x = x;
            this.y = y;
        }

        public Builder addItem(TipItem tipItem) {
            mTipItemList.add(tipItem);
            return this;
        }

        public Builder addItems(List<TipItem> list) {
            mTipItemList.addAll(list);
            return this;
        }

        public Builder setSeparateLineColor(int color) {
            mSeparateLineColor = color;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public TipView create() {
            TipView flipShare = new TipView(mContext, mRootView,x,y,mTipItemList);
            flipShare.setOnItemClickListener(onItemClickListener);
            flipShare.setSeparateLineColor(mSeparateLineColor);
            mPopupWindow = new PopupWindow(flipShare, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.update();
            
            
            mPopupWindow.showAtLocation(mRootView, Gravity.CENTER,0,0);
            flipShare.setPopupWindow(mPopupWindow);
            return flipShare;
        }

    }

}
