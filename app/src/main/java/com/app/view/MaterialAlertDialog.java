

package com.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.app.R;



public class MaterialAlertDialog {

    private Context mContext;

    private Dialog mContentDialog;
    private View mContentView;
    private LayoutInflater mLayoutInflater;

    private TextView mTitleView;
    private TextView mContentTv;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private Button mNeutralBtn;

    private String mTitle;
    private String mContent;
    private SpannableString mSpannableContent;

    private String mPositiveText;
    private String mNegativeText;
    private String mNeutralText;

    private boolean mShowPositiveBtn = true, mShowNegativeBtn = true, mShowNeutralBtn = false;


    private OnBtnClickListener mOnBtnClickListener;

    public interface OnBtnClickListener {
        void onNegativeBtnClick();
        void onPositiveBtnClick();
        void onNeutralBtnClick();
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.mOnBtnClickListener = listener;
    }

    
    public MaterialAlertDialog(Context context, String title, String content) {
        this.mContext = context;
        this.mTitle = title;
        this.mContent = content;
        this.mContentDialog = new Dialog(context, R.style.dialogDim);
        this.mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public MaterialAlertDialog(Context context, String title, SpannableString content) {
        this.mContext = context;
        this.mTitle = title;
        this.mSpannableContent = content;
        this.mContentDialog = new Dialog(context, R.style.dialogDim);
        this.mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    
    public void setCanceledOnTouchOutside(boolean outSideCanceled) {
        if (null != mContentDialog) {
            mContentDialog.setCanceledOnTouchOutside(outSideCanceled);
        }
    }

    public void setCancelable(boolean cancelable) {
        if (null != mContentDialog) {
            mContentDialog.setCancelable(cancelable);
        }
    }



    
    public void setBackKeyEnable(boolean enable) {
        if (null!= mContentDialog && !enable) {
            mContentDialog.setOnKeyListener((dialog, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            });
        }
    }


    public void show() {
        if (null == mContentView) {
            mContentView = mLayoutInflater.inflate(R.layout.material_alert_dialog,null);
        }

        mTitleView = (TextView) mContentView.findViewById(R.id.title);
        mContentTv = (TextView) mContentView.findViewById(R.id.content);
        mPositiveBtn = (Button) mContentView.findViewById(R.id.positive_btn);
        mNegativeBtn = (Button) mContentView.findViewById(R.id.negative_btn);
        mNeutralBtn = (Button) mContentView.findViewById(R.id.neutral_btn);

        if (null != mTitleView &&  !TextUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        }
        if (null != mContentTv) {
            if (!TextUtils.isEmpty(mContent)) {
                mContentTv.setText(mContent);
            }
            if (!TextUtils.isEmpty(mSpannableContent)) {
                mContentTv.setText(mSpannableContent);
            }
        }

        if (null != mPositiveBtn) {
            mPositiveBtn.setOnClickListener(v->{
                if (null != mOnBtnClickListener) {
                    mOnBtnClickListener.onPositiveBtnClick();
                }
            });
            if (!TextUtils.isEmpty(mPositiveText)) {
                mPositiveBtn.setText(mPositiveText);
            }
        }

        if (null != mNegativeBtn) {
            mNegativeBtn.setOnClickListener(v->{
                if (null != mOnBtnClickListener) {
                    mOnBtnClickListener.onNegativeBtnClick();
                }
            });
            if (!TextUtils.isEmpty(mNegativeText)) {
                mNegativeBtn.setText(mNegativeText);
            }
        }

        if (null != mNeutralBtn) {
            mNeutralBtn.setOnClickListener(v -> {
                if (null != mOnBtnClickListener) {
                    mOnBtnClickListener.onNeutralBtnClick();
                }
            });
            if (!TextUtils.isEmpty(mNeutralText)) {
                mNeutralBtn.setText(mNeutralText);
            }
        }

        
        mPositiveBtn.setVisibility(mShowPositiveBtn ? View.VISIBLE : View.GONE);
        mNegativeBtn.setVisibility(mShowNegativeBtn ? View.VISIBLE : View.GONE);
        mNeutralBtn.setVisibility(mShowNeutralBtn ? View.VISIBLE : View.GONE);


        mContentDialog.setContentView(mContentView);
        mContentDialog.show();
    }

    public void setPositiveBtnText(String text) {
        this.mPositiveText = text;
        if (null != mPositiveBtn && !TextUtils.isEmpty(text)) {
            mPositiveBtn.setText(text);
        }
    }

    public void setNegativeBtnText(String text) {
        this.mNegativeText = text;
        if (null != mNegativeBtn && !TextUtils.isEmpty(text)) {
            mNegativeBtn.setText(text);
        }
    }

    public void setNeutralBtnText(String text) {
        this.mNeutralText = text;
        if (null != mNeutralBtn && !TextUtils.isEmpty(text)) {
            mNeutralBtn.setText(text);
        }
    }


    public void setPositiveBtnIsShow(boolean show) {
        mShowPositiveBtn = show;
        if (null != mPositiveBtn) {
            mPositiveBtn.setVisibility(mShowPositiveBtn ? View.VISIBLE : View.GONE);
        }
    }

    public void setNegativeBtnIsShow(boolean show) {
        mShowNegativeBtn = show;
        if (null != mNegativeBtn) {
            mNegativeBtn.setVisibility(mShowNegativeBtn ? View.VISIBLE : View.GONE);
        }
    }

    public void setNeutralBtnIsShow(boolean show) {
        mShowNeutralBtn = show;
        if (null != mNeutralBtn) {
            mNeutralBtn.setVisibility(mShowNeutralBtn ? View.VISIBLE : View.GONE);
        }
    }

    public void dismiss() {
        if (null != mContentDialog) {
            mContentDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (null != mContentDialog) {
            return mContentDialog.isShowing();
        } else {
            return false;
        }
    }

    public void setSystemFloating() {
        if (null != mContentDialog && null != mContentDialog.getWindow()) {
            if(Build.VERSION.SDK_INT > 24) {
                mContentDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            }else {
                mContentDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            }
        }
    }
}
