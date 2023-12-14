

package com.wallet.ctc.view.choosetime.popup;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.CallSuper;
import androidx.annotation.StyleRes;

import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.choosetime.util.ScreenUtils;


public abstract class BasicPopup<V extends View> implements DialogInterface.OnKeyListener {
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected Activity activity;
    protected int screenWidthPixels;
    protected int screenHeightPixels;
    private PopupDialog popupDialog;
    private int width = 0, height = 0;
    private boolean isFillScreen = false;
    private boolean isHalfScreen = false;
    private boolean isPrepared = false;
    private int gravity = Gravity.BOTTOM;

    
    public BasicPopup(Activity activity) {
        this.activity = activity;
        DisplayMetrics displayMetrics = ScreenUtils.displayMetrics(activity);
        screenWidthPixels = displayMetrics.widthPixels;
        screenHeightPixels = displayMetrics.heightPixels;
        popupDialog = new PopupDialog(activity);
        popupDialog.setOnKeyListener(this);
    }

    
    protected abstract V makeContentView();

    
    private void onShowPrepare() {
        if (isPrepared) {
            return;
        }
        popupDialog.getWindow().setGravity(gravity);
        setContentViewBefore();
        V view = makeContentView();
        popupDialog.setContentView(view);
        setContentViewAfter(view);
        LogUtil.d("do something before popup show");
        if (width == 0 && height == 0) {
            
            width = screenWidthPixels;
            if (isFillScreen) {
                height = MATCH_PARENT;
            } else if (isHalfScreen) {
                height = screenHeightPixels / 2;
            } else {
                height = WRAP_CONTENT;
            }
        } else if (width == 0) {
            width = screenWidthPixels;
        } else if (height == 0) {
            height = WRAP_CONTENT;
        }
        popupDialog.setSize(width, height);
        isPrepared = true;
    }

    
    public void setFillScreen(boolean fillScreen) {
        isFillScreen = fillScreen;
    }

    
    public void setHalfScreen(boolean halfScreen) {
        isHalfScreen = halfScreen;
    }

    
    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (gravity == Gravity.CENTER) {
            setWidth((int) (screenWidthPixels * 0.7));
        }
    }

    
    protected void setContentViewBefore() {
    }

    
    protected void setContentViewAfter(V contentView) {
    }

    
    public void setAnimationStyle(@StyleRes int animRes) {
        popupDialog.setAnimationStyle(animRes);
    }

    
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        popupDialog.setOnDismissListener(onDismissListener);
        LogUtil.d("popup setOnDismissListener");
    }

    
    public void setSize(int width, int height) {
        
        this.width = width;
        this.height = height;
    }

    
    public void setWidth(int width) {
        this.width = width;
    }

    
    public void setHeight(int height) {
        this.height = height;
    }

    
    public boolean isShowing() {
        return popupDialog.isShowing();
    }

    
    @CallSuper
    public void show() {
        onShowPrepare();
        popupDialog.show();
        LogUtil.d("popup show");
    }

    
    public void dismiss() {
        popupDialog.dismiss();
        LogUtil.d("popup dismiss");
    }

    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return onKeyDown(keyCode, event);
        }
        return false;
    }

    
    public Window getWindow() {
        return popupDialog.getWindow();
    }

    
    public ViewGroup getRootView() {
        return popupDialog.getRootView();
    }

}
