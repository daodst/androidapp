

package common.app.im.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import common.app.R;


public class RightIndexView extends ViewGroup {
    private static final String TAG = "RightIndexView";
    private Context mContext;
    private ArrayList<String> list = new ArrayList<>();

    
    private int rootBgColor;
    private int rootTouchBgColor;
    private int itemTouchBgColor;
    private int itemTextColor;
    private int itemTextTouchBgColor;
    private int itemTextSize;

    
    private int mOldViewIndex;

    
    private int mWidth;
    private int mHeight;
    
    private int mItemHeight;

    
    private int marginTop = 5;
    private int marginBottom = 5;

    
    private int mTouchSlop;
    
    private int yDown;
    private int yMove;

    
    private OnRightTouchMoveListener onRightTouchMoveListener;

    public void setOnRightTouchMoveListener(OnRightTouchMoveListener onRightTouchMoveListener) {
        this.onRightTouchMoveListener = onRightTouchMoveListener;
    }

    public RightIndexView(Context context) {
        this(context, null);
    }

    public RightIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.rightindexview);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.rightindexview_index_rootBgColor) {
                rootBgColor = typedArray.getColor(attr, Color.parseColor("#80808080"));

            } else if (attr == R.styleable.rightindexview_index_rootTouchBgColor) {
                rootTouchBgColor = typedArray.getColor(attr, Color.parseColor("#EE808080"));

            } else if (attr == R.styleable.rightindexview_index_itemTouchBgColor) {
                itemTouchBgColor = typedArray.getColor(attr, Color.parseColor("#000000"));

            } else if (attr == R.styleable.rightindexview_index_itemTextColor) {
                itemTextColor = typedArray.getColor(attr, Color.parseColor("#FFFFFF"));

            } else if (attr == R.styleable.rightindexview_index_itemTextTouchBgColor) {
                itemTextTouchBgColor = typedArray.getColor(attr, Color.parseColor("#FF0000"));

            } else if (attr == R.styleable.rightindexview_index_itemTextSize) {
                float a = typedArray.getDimension(attr, 12);
                float b = typedArray.getDimension(attr, 10);
                itemTextSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
                Log.i(TAG, itemTextSize + "===" + a + "\t" + b);

            }
        }
        
        typedArray.recycle();
        this.mContext = context;
        
        setBackgroundColor(rootBgColor);
        
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        
        int top = 5;
        
        int size = 0;
        if (list != null && list.size() > 0) {
            
            size = list.size();
            
            mItemHeight = (mHeight - marginTop - marginBottom) / size;
        }
        
        for (int i = 0; i < size; i++) {
            TextView textView = (TextView) getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.height = mItemHeight;
            layoutParams.width = mWidth;
            layoutParams.top = top;
            top += mItemHeight;
        }
        
        setMeasuredDimension(mWidth, mHeight);
    }

    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            
            TextView textView = (TextView) getChildAt(i);
            
            LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            
            textView.layout(0, layoutParams.top, layoutParams.width, layoutParams.top + layoutParams.height);
        }
        
    }

    public void setData(ArrayList<String> list) {
        if (list == null || list.size() <= 0) {
            list = new ArrayList<>();
            list.add(mContext.getString(R.string.contact_up));
            list.add(mContext.getString(R.string.contact_star));
            for (int i = 0; i < 26; i++) {
                list.add(String.valueOf((char) ('A' + i)));
            }
            list.add("#");
        }
        int size = list.size();
        this.list.addAll(list);
        for (int i = 0; i < size; i++) {
            addView(list.get(i), i);
        }
        
        
        
    }

    public void setData() {
        ArrayList<String> list = new ArrayList<>();
        list = new ArrayList<>();
        list.add(mContext.getString(R.string.contact_up));
        list.add(mContext.getString(R.string.contact_star));
        for (int i = 0; i < 26; i++) {
            list.add(String.valueOf((char) ('A' + i)));
        }
        list.add("#");
        int size = list.size();
        this.list.addAll(list);
        for (int i = 0; i < size; i++) {
            addView(list.get(i), i);
        }
        
        
        
    }

    private void addView(String firstPinYin, int position) {
        TextView textView = new TextView(mContext);
        textView.setText(firstPinYin);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTextColor(itemTextColor);
        textView.setTextSize(px2sp(mContext, itemTextSize));
        textView.setGravity(Gravity.CENTER);
        textView.setTag(position);
        addView(textView, position);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        int y = (int) event.getY();
        
        int tempIndex = computeViewIndexByY(y);
        if (tempIndex != -1) {
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = y;
                    drawTextView(mOldViewIndex, false);
                    drawTextView(tempIndex, true);
                    mOldViewIndex = tempIndex;
                    if (onRightTouchMoveListener != null) {
                        onRightTouchMoveListener.showTip(tempIndex, ((TextView) getChildAt(tempIndex)).getText().toString(), true);
                    }
                    
                    setBackgroundColor(rootTouchBgColor);
                    break;
                case MotionEvent.ACTION_MOVE:
                    yMove = y;
                    int distance = yDown - yMove;
                    if (Math.abs(distance) > mTouchSlop) {
                        
                        if (mOldViewIndex != tempIndex) {
                            
                            drawTextView(mOldViewIndex, false);
                            drawTextView(tempIndex, true);
                            mOldViewIndex = tempIndex;
                            setBackgroundColor(rootTouchBgColor);
                            if (onRightTouchMoveListener != null) {
                                onRightTouchMoveListener.showTip(tempIndex, ((TextView) getChildAt(tempIndex)).getText().toString(), true);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    drawTextView(mOldViewIndex, false);
                    drawTextView(tempIndex, false);
                    mOldViewIndex = tempIndex;
                    setBackgroundColor(rootBgColor);
                    if (onRightTouchMoveListener != null) {
                        onRightTouchMoveListener.showTip(tempIndex, ((TextView) getChildAt(tempIndex)).getText().toString(), false);
                    }
                    break;
            }
        } else {
            
            if (list != null && list.size() > 0) {
                drawTextView(mOldViewIndex, false);
                setBackgroundColor(rootBgColor);
                if (onRightTouchMoveListener != null) {
                    onRightTouchMoveListener.showTip(mOldViewIndex, ((TextView) getChildAt(mOldViewIndex)).getText().toString(), false);
                }
            }
        }
        return true;
    }

    
    private int computeViewIndexByY(int y) {
        int returnValue;
        if (y < marginTop || y > (marginTop + mItemHeight * list.size())) {
            returnValue = -1;
        } else {
            int times = (y - marginTop) / mItemHeight;
            int remainder = (y - marginTop) % mItemHeight;
            if (remainder == 0) {
                returnValue = --times;
            } else {
                returnValue = times;
            }
        }
        return returnValue;
    }

    
    private void drawTextView(int index, boolean isDrawStyle) {
        if (index < 0 || index >= list.size()) {
            return;
        }
        TextView textView = (TextView) getChildAt(index);
        if (textView == null) {
            return;
        }
        if (isDrawStyle) {
            textView.setBackgroundColor(itemTouchBgColor);
            textView.setTextColor(itemTextTouchBgColor);
        } else {
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(itemTextColor);
        }
    }

    public interface OnRightTouchMoveListener {
        void showTip(int position, String content, boolean isShow);
    }

    public void showTip(String content, boolean isShow, CenterTipView tipView) {
        if (isShow) {
            tipView.setVisibility(VISIBLE);
            tipView.setText(content);
        } else {
            tipView.setVisibility(INVISIBLE);
        }
    }

    public void scrollTo(int i, RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        recyclerView.stopScroll();
        int firstItem = layoutManager.findFirstVisibleItemPosition();
        int lastItem = layoutManager.findLastVisibleItemPosition();
        if (i <= firstItem) {
            recyclerView.scrollToPosition(i);
        } else if (i <= lastItem) {
            int top = recyclerView.getChildAt(i - firstItem).getTop();
            recyclerView.scrollBy(0, top);
        } else {
            recyclerView.scrollToPosition(i);
        }
    }

    
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int left;
        public int top;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
