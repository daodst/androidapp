package com.benny.openlauncher.util;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.benny.openlauncher.R;
import com.benny.openlauncher.floatingwindow.CustomMoveButton;


public class AddFloatingButtonHelper {
    public static AddFloatingButtonHelper getInstance() {
        return new AddFloatingButtonHelper();
    }

    @SuppressLint("ClickableViewAccessibility")
    public View initFloatingButton(Activity pActivity, View.OnClickListener floatButtonClick) {
        DisplayMetrics dm = pActivity.getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WRAP_CONTENT;
        params.height = WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = widthPixels;  
        params.y = heightPixels - 360;

        
        View floatRootView = LayoutInflater.from(pActivity).inflate(R.layout.custom_dragger_button, null);

        CustomMoveButton moveButton = new CustomMoveButton(pActivity.getApplicationContext());
        moveButton.setImageResource(R.mipmap.homepae);
        moveButton.setForegroundGravity(Gravity.CENTER);

        floatRootView.setOnTouchListener(new ItemViewTouchListener(pActivity.getWindowManager(), params));
        floatRootView.setOnClickListener(v -> {
            
            Tool.createScaleInScaleOutAnim(floatRootView, () -> floatButtonClick.onClick(floatRootView));
        });

        
        floatRootView.setVisibility(View.GONE);
        pActivity.getWindowManager().addView(floatRootView, params);

        return floatRootView;
    }

    static class ItemViewTouchListener implements View.OnTouchListener {
        private final WindowManager mWindowManager;
        private final WindowManager.LayoutParams mLayoutParams;

        private float x = 0F, y = 0F;

        public ItemViewTouchListener(WindowManager pWindowManager, WindowManager.LayoutParams pLayoutParams) {
            mWindowManager = pWindowManager;
            mLayoutParams = pLayoutParams;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getRawX();
                    y = event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float nowX = event.getRawX();
                    float nowY = event.getRawY();
                    float movedX = nowX - x;
                    float movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mLayoutParams.x += movedX;
                    mLayoutParams.y += movedY;
                    
                    mWindowManager.updateViewLayout(v, mLayoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
