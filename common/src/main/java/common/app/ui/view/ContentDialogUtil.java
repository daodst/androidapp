package common.app.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import common.app.R;




public class ContentDialogUtil {

    private Context mContext;
    private Dialog mDialog;
    private View mContentView;
    private boolean isBottom;

    public ContentDialogUtil(Context context, View contentView) {
        this.mContext = context;
        this.mContentView = contentView;
        init();
    }

    public ContentDialogUtil(Context context, View contentView, boolean bottomShow) {
        this.isBottom = bottomShow;
        this.mContext = context;
        this.mContentView = contentView;
        init();
    }


    private void init() {
        mDialog = new Dialog(mContext, R.style.dialog);
        mDialog.setContentView(mContentView);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        if (isBottom) {
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
        }

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                View focusView = mDialog.getCurrentFocus();
                if (null != focusView) {
                    hideSoftInput(focusView.getWindowToken());
                }
            }
        });
    }

    public View findViewById(int viewId) {
        return mContentView.findViewById(viewId);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean cacel) {
        if (null != mDialog) {
            mDialog.setCanceledOnTouchOutside(cacel);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (null != mDialog) {
            mDialog.setOnDismissListener(listener);
        }
    }

    public void setCancelable(boolean cacel) {
        if (null != mDialog) {
            mDialog.setCancelable(cacel);
        }
    }

    public boolean isShowing(){
        return  mDialog.isShowing();
    }


    
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
