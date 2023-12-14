

package common.app.base.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import common.app.R;
import common.app.utils.DisplayUtils;
import common.app.utils.SpanString;


public class TopBackBar extends LinearLayout {


    private Context mContext;

    
    public TopBackBar(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }


    public TopBackBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public TopBackBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    
    private String getString(@StringRes int strID) {
        return mContext.getString(strID);
    }

    
    private int getSize(TextView tv) {
        return (int) (tv.getTextSize() + 0.5f);
    }


    
    private TextView mLeftTv;
    private TextView mMiddleTv;

    public TextView getMiddleTv() {
        return mMiddleTv;
    }

    private TextView mRighter;
    private RelativeLayout mParent;

    private void initView() {
        View view = View.inflate(mContext, R.layout.common_top_bar, this);
        mLeftTv = (TextView) view.findViewById(R.id.common_top_bar_left_tv);
        mParent = (RelativeLayout) view.findViewById(R.id.common_topbar_parnt);
        mMiddleTv = (TextView) view.findViewById(R.id.common_top_bar_middle_tv);
        mRighter = (TextView) view.findViewById(R.id.common_top_bar_righter_tv);
        mLeftTv.setVisibility(GONE);

    }

    public TopBackBar setBgColor(@ColorRes int color) {
        mParent.setBackgroundResource(color);
        return this;
    }

    public TopBackBar setBgIntColor(@ColorInt int color) {
        mParent.setBackgroundColor(color);
        return this;
    }

    public TextView getRighter() {
        return mRighter;
    }

    
    public TopBackBar setLeftTv(final LeftClickListener listener) {
        setLeftDrawableListener(listener);
        return this;
    }


    public TopBackBar setBackground(@ColorRes int id) {
        this.mParent.setBackgroundColor(ContextCompat.getColor(mContext, id));
        return this;
    }

    public TopBackBar setRighterTvText(final @StringRes int strRes, final RighterClickListener listener) {
        setRighterTextListener(strRes, listener);
        return this;
    }

    private void setRighterTextListener(final @StringRes int strRes, final RighterClickListener listener) {
        mRighter.setVisibility(VISIBLE);
        mRighter.setText(strRes);
        if (listener != null) {
            mRighter.setOnClickListener(v -> {
                listener.OnClick(v);
            });
        }

    }

    public TopBackBar setRighterTvTextOnclick(@StringRes int content, @ColorRes int colorID, final RighterClickListener listener) {
        mRighter.setText(SpanString.getSpannableString(mContext, content, getSize(mRighter), colorID, "normal"));
        if (listener != null) {
            mRighter.setOnClickListener(v -> {
                listener.OnClick(v);
            });
        }
        return this;
    }


    public TopBackBar setRighterTvTextSize(float textSize) {
        mRighter.setTextSize(textSize);
        return this;
    }

    public TopBackBar setRighterBound(int width, int height) {
        mRighter.setWidth(DisplayUtils.dp2px(mContext, width));
        mRighter.setHeight(DisplayUtils.dp2px(mContext, height));
        return this;
    }

    public void setRighterDrawableListener(final @DrawableRes int left, final @DrawableRes int leftPress, RighterClickListener listener) {
        mRighter.setVisibility(VISIBLE);
        if (listener != null) {
            setRightDrawable(mRighter, left);
            mRighter.setOnClickListener(v -> {
                listener.OnClick(v);
            });
        }

    }

    public TopBackBar setLeftDrawableListener(@StringRes int content, @ColorRes int colorID, final @DrawableRes int left, final @DrawableRes int leftPress, LeftClickListener listener) {
        mLeftTv.setVisibility(VISIBLE);
        mLeftTv.setText(SpanString.getSpannableString(mContext, content, getSize(mLeftTv), colorID, "normal"));
        if (listener != null) {
            setLeftDrawable(mLeftTv, left);
            mLeftTv.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mLeftTv.getCompoundDrawables()[DRAWABLE_LEFT] == null) {
                        return false;
                    }
                    
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        setLeftDrawable(mLeftTv, left);
                        return false;
                    }
                    if (event.getX() > mLeftTv.getWidth() - mLeftTv.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) {
                        
                        setLeftDrawable(mLeftTv, leftPress);
                        return false;
                    }
                    return false;
                }
            });
            mLeftTv.setOnClickListener(v -> {
                listener.OnClick(v);
            });
        }
        return this;

    }


    
    public TopBackBar setLeftTv(String content, @ColorRes int colorID, final LeftClickListener listener) {
        mLeftTv.setText(SpanString.getSpannableString(content, getSize(mLeftTv), colorID, "normal"));
        mLeftTv.setOnClickListener(v -> {
            listener.OnClick(v);
        });
        return this;
    }

    public TopBackBar setLeftTv(String content, int start, int end, @ColorRes int colorID, final LeftClickListener listener) {
        setLeftDrawableListener(listener);
        mLeftTv.setTextColor(ContextCompat.getColor(mContext, colorID));
        mLeftTv.setText(SpanString.getTouchableSpanString(content, start, end, R.color.default_theme_color, R.color.default_theme_color, null));
        return this;
    }

    
    public TopBackBar setLeftTv(@StringRes int contentID, @ColorRes int colerID, final LeftClickListener listener) {
        setLeftDrawableListener(listener);
        
        mLeftTv.setText(SpanString.getSpannableString(mContext, contentID, getSize(mLeftTv), colerID, "normal"));
        return this;
    }

    
    private static final int DRAWABLE_LEFT = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_BUTTOM = 3;

    private Boolean mLeftPress = false;

    
    private void setLeftDrawableListener(final LeftClickListener listener) {
        mLeftTv.setVisibility(VISIBLE);
        if (listener != null) {
            setLeftDrawable(mLeftTv, R.drawable.top_bar_left);
            mLeftTv.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mLeftTv.getCompoundDrawables()[DRAWABLE_LEFT] == null) {
                        return false;
                    }
                    
                    if (event.getAction() != MotionEvent.ACTION_UP) {
                        setLeftDrawable(mLeftTv, R.drawable.top_bar_left);
                        return false;
                    }
                    if (event.getX() > mLeftTv.getWidth() - mLeftTv.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) {
                        
                        setLeftDrawable(mLeftTv, R.drawable.top_bar_left__press);
                        return false;
                    }
                    return false;
                }
            });
            mLeftTv.setOnClickListener(v -> {
                listener.OnClick(v);
            });
        }
    }

    
    private void setLeftDrawable(TextView tv, @DrawableRes int drawableID) {
        int size = getSize(tv);
        
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    
    private void setRightDrawable(TextView tv, @DrawableRes int drawableID) {
        int size = getSize(tv);
        
        Drawable drawable = ContextCompat.getDrawable(mContext, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(null, null, drawable, null);
    }


    public interface LeftClickListener {
        void OnClick(View v);
    }

    public interface RighterClickListener {
        void OnClick(View v);
    }


    public TopBackBar setMiddleTv(String content, @ColorRes int colorID) {
        mMiddleTv.setText(SpanString.getSpannableString(content, getSize(mMiddleTv), colorID));
        return this;
    }

    public TopBackBar setMiddleTv(@StringRes int contentID, @ColorRes int colorID) {
        mMiddleTv.setText(SpanString.getSpannableString(mContext, contentID, getSize(mMiddleTv), colorID));
        return this;
    }

    public TopBackBar setMiddleTv() {
        return this;
    }
}
