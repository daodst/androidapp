

package common.app.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import common.app.R;




public class TitleBarView extends RelativeLayout {
    private String title, righttitle;   
    private Drawable imgright, imgleft;
    private ImageView btnLeft; 
    private ImageView btnRight;    
    private TextView tvTitle, tvTitlerights;   
    private Integer backgroundcolor;
    private View dividerView;
    private boolean setleftimg, hideDivider;
    public TitleBarClickListener listener;
    private RelativeLayout view;

    public TitleBarView(Context context) {
        super(context);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Title_bar);
        init(context, typedArray);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Title_bar);
        init(context, typedArray);
    }

    
    public void setText(String str) {
        tvTitle.setText(str);
    }

    
    public void setRightText(String str) {
        tvTitlerights.setText(str);
        righttitle = str;
        setRightTextVisable(true);
    }

    
    public void setRightTextColor(int color) {
        tvTitlerights.setTextColor(color);
    }

    
    public void setRightTextOnclick(boolean pd) {
        if (pd) {
            tvTitlerights.setPressed(true);
            tvTitlerights.setClickable(true);
            tvTitlerights.setTextColor(getResources().getColor(R.color.default_text_three_color));
        } else {
            tvTitlerights.setPressed(false);
            tvTitlerights.setClickable(false);
            tvTitlerights.setTextColor(getResources().getColor(R.color.default_hint_text_color));
        }
    }

    
    public void setRightTextClickable(boolean pd) {
        if (pd) {
            tvTitlerights.setClickable(true);
        } else {
            tvTitlerights.setClickable(false);
        }
    }


    
    private void init(Context context, TypedArray ta) {
        title = ta.getString(R.styleable.Title_bar_titleText);
        righttitle = ta.getString(R.styleable.Title_bar_righttitleText);
        imgright = ta.getDrawable(R.styleable.Title_bar_rightimg);
        imgleft = ta.getDrawable(R.styleable.Title_bar_leftimg);
        setleftimg = ta.getBoolean(R.styleable.Title_bar_setleftimg, true);
        hideDivider = ta.getBoolean(R.styleable.Title_bar_hideDivider, false);
        backgroundcolor = ta.getColor(R.styleable.Title_bar_backgroundcolor, ContextCompat.getColor(context, R.color.default_titlebar_bg_color));
        ta.recycle();

        LayoutInflater.from(context).inflate(R.layout.titlebar, this);
        view = (RelativeLayout) findViewById(R.id.view);
        btnLeft = (ImageView) findViewById(R.id.btn_titlebar_left);
        btnRight = (ImageView) findViewById(R.id.btn_titlebar_right);
        tvTitle = (TextView) findViewById(R.id.tv_titlebar_name);
        tvTitlerights = (TextView) findViewById(R.id.btn_titlebar_rights);
        dividerView = findViewById(R.id.title_divider);
        if (null != imgright) {
            setRightImgVisable(true);
        }
        if (null != imgleft) {
            btnLeft.setImageDrawable(imgleft);
        }
        if (null != righttitle) {
            setRightTextVisable(true);
        }

        view.setBackgroundColor(backgroundcolor);

        setLeftBtnVisable(setleftimg);

        if (hideDivider) {
            dividerView.setVisibility(GONE);
        }


        tvTitle.setText(title);


        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.leftClick();
                }

            }
        });
        btnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.rightClick();
                }
            }
        });
        tvTitlerights.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.rightClick();
                }
            }
        });


    }

    
    public interface TitleBarClickListener {
        void leftClick();   

        void rightClick();  
    }

    
    public void setOnTitleBarClickListener(TitleBarClickListener listener) {
        this.listener = listener;
    }


    
    public void setLeftBtnVisable(boolean flag) {
        if (flag) {
            btnLeft.setVisibility(VISIBLE);
        } else {
            btnLeft.setVisibility(GONE);
        }
    }

    
    public void setRightImgVisable(boolean flag) {
        if (flag) {
            btnRight.setVisibility(VISIBLE);
            btnRight.setImageDrawable(imgright);
        } else {
            btnRight.setVisibility(GONE);
        }
    }

    public void setRightTextVisable(boolean flag) {
        if (flag) {
            tvTitlerights.setVisibility(VISIBLE);
            tvTitlerights.setText(righttitle);
        } else {
            tvTitlerights.setVisibility(GONE);
        }
    }

    public void setRightTitleBg(int background) {
        tvTitlerights.setBackgroundResource(background);
    }

    public TextView getRightTextView() {
        return tvTitlerights;
    }
}
