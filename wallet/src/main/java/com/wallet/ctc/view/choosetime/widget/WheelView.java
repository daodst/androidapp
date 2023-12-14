

package com.wallet.ctc.view.choosetime.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import java.util.LinkedList;
import java.util.List;


public class WheelView extends ScrollView {
    public static final int TEXT_SIZE = 20;
    public static final int TEXT_COLOR_FOCUS = 0XFF0288CE;
    public static final int TEXT_COLOR_NORMAL = 0XFFBBBBBB;
    public static final int LINE_COLOR = 0XFF83CDE6;
    public static final int OFF_SET = 1;
    private static final int DELAY = 50;

    private Context context;
    private LinearLayout views;
    private LinkedList<String> items = new LinkedList<String>();
    private int offset = OFF_SET; 

    private int displayItemCount; 

    private int selectedIndex = OFF_SET;
    private int initialY;

    private Runnable scrollerTask = new ScrollerTask();
    private int itemHeight = 0;
    private int[] selectedAreaBorder;
    private OnWheelViewListener onWheelViewListener;

    private Paint paint;
    private int viewWidth;
    private int textSize = TEXT_SIZE;
    private int textColorNormal = TEXT_COLOR_NORMAL;
    private int textColorFocus = TEXT_COLOR_FOCUS;
    private int lineColor = LINE_COLOR;
    private boolean lineVisible = true;
    private boolean isUserScroll = false;
    private float previousY = 0;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        
        setFadingEdgeLength(0);
        if (Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        addView(views);
    }

    private void startScrollerTask() {
        initialY = getScrollY();
        postDelayed(scrollerTask, DELAY);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        
        views.removeAllViews();

        for (String item : items) {
            views.addView(createView(item));
        }

        
        refreshItemView(itemHeight * (selectedIndex - offset));
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(item);
        tv.setTextSize(textSize);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(15);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }


    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            
            if (position == i) {
                itemView.setTextColor(textColorFocus);
            } else {
                itemView.setTextColor(textColorNormal);
            }
        }
    }

    
    private void onSelectedCallBack() {
        if (null != onWheelViewListener) {
            
            int realIndex = selectedIndex - offset;
            onWheelViewListener.onSelected(isUserScroll, realIndex, items.get(this.selectedIndex));
        }
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(new LineDrawable());
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        isUserScroll = true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                float delta = ev.getY() - previousY;
                if (selectedIndex == offset && delta > 0) {
                    
                    setSelectedIndex(items.size() - offset * 2 - 1);
                } else if (selectedIndex == items.size() - offset - 1 && delta < 0) {
                    
                    setSelectedIndex(0);
                } else {
                    startScrollerTask();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void _setItems(List<String> list) {
        items.clear();
        items.addAll(list);

        
        for (int i = 0; i < offset; i++) {
            items.addFirst("");
            items.addLast("");
        }

        initData();

    }

    
    public void setItems(List<String> list) {
        _setItems(list);
        
        setSelectedIndex(0);
    }

    
    public void setItems(List<String> list, int index) {
        _setItems(list);
        setSelectedIndex(index);
    }

    
    public void setItems(List<String> list, String item) {
        _setItems(list);
        setSelectedItem(item);
    }

    
    public int getTextSize() {
        return textSize;
    }

    
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    
    public int getTextColor() {
        return textColorFocus;
    }

    
    public void setTextColor(@ColorInt int textColorNormal, @ColorInt int textColorFocus) {
        this.textColorNormal = textColorNormal;
        this.textColorFocus = textColorFocus;
    }

    
    public void setTextColor(@ColorInt int textColor) {
        this.textColorFocus = textColor;
    }

    
    public boolean isLineVisible() {
        return lineVisible;
    }

    
    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    
    public int getLineColor() {
        return lineColor;
    }

    
    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
    }

    
    public int getOffset() {
        return offset;
    }

    
    public void setOffset(@IntRange(from = 1, to = 4) int offset) {
        if (offset < 1 || offset > 4) {
            throw new IllegalArgumentException("Offset must between 1 and 4");
        }
        this.offset = offset;
    }

    
    private void setSelectedIndex(@IntRange(from = 0) final int index) {
        isUserScroll = false;
        this.post(new Runnable() {
            @Override
            public void run() {
                
                smoothScrollTo(0, index * itemHeight);
                
                selectedIndex = index + offset;
                onSelectedCallBack();
            }
        });
    }

    
    public void setSelectedItem(String item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                
                setSelectedIndex(i - offset);
                break;
            }
        }
    }

    
    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    
    public int getSelectedIndex() {
        return selectedIndex - offset;
    }

    
    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    
    public interface OnWheelViewListener {
        
        void onSelected(boolean isUserScroll, int selectedIndex, String item);
    }

    private class ScrollerTask implements Runnable {

        @Override
        public void run() {
            
            if (itemHeight == 0) {
                return;
            }
            int newY = getScrollY();
            if (initialY - newY == 0) { 
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
                if (remainder == 0) {
                    selectedIndex = divided + offset;
                    onSelectedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollTo(0, initialY - remainder + itemHeight);
                                selectedIndex = divided + offset + 1;
                                onSelectedCallBack();
                            }
                        });
                    } else {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollTo(0, initialY - remainder);
                                selectedIndex = divided + offset;
                                onSelectedCallBack();
                            }
                        });
                    }
                }
            } else {
                startScrollerTask();
            }
        }

    }

    private class LineDrawable extends Drawable {

        public LineDrawable() {
            if (viewWidth == 0) {
                viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            }

            
            if (!lineVisible) {
                return;
            }

            if (null == paint) {
                paint = new Paint();
                paint.setColor(lineColor);
                paint.setStrokeWidth(dip2px(1f));
            }
        }

        @Override
        public void draw(Canvas canvas) {
            if (null == selectedAreaBorder) {
                selectedAreaBorder = new int[2];
                selectedAreaBorder[0] = itemHeight * offset;
                selectedAreaBorder[1] = itemHeight * (offset + 1);
            }
            canvas.drawLine(viewWidth / 6, selectedAreaBorder[0], viewWidth * 5 / 6, selectedAreaBorder[0], paint);
            canvas.drawLine(viewWidth / 6, selectedAreaBorder[1], viewWidth * 5 / 6, selectedAreaBorder[1], paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

}
