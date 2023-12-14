

package common.app.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import common.app.R;
import common.app.utils.AllUtils;



public class FloatView extends LinearLayout {

    private WindowManager wm;
    private WindowManager.LayoutParams wlp;
    private float x;
    private float y;
    private float newX;
    private float newY;
    private final long minTime = 300;
    private Context mContext;
    private boolean isAdded;
    private int screenWidth, screenHeight;
    private boolean isRelease;
    private boolean isLongPress=false;
    Runnable run = new Runnable() {
        @Override
        public void run() {
            
            if(isRelease){
                return;
            }
            
            isLongPress = true;
        }
    };


    public FloatView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.floatview, null);
        myView.findViewById(R.id.zaixiankefu).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if (!isLongPress) {

                    }
                }
                return true;
            }
        });
        myView.findViewById(R.id.close).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if (!isLongPress) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        myView.findViewById(R.id.fuwurexian).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if (!isLongPress) {

                    }
                }
                return true;
            }
        });
        this.addView(myView);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wlp = new WindowManager.LayoutParams();
    }
    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        x=event.getRawX();
        y=event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isRelease = false;
                isLongPress = false;
                newX=event.getX();
                newY=event.getY();
                
                postDelayed(run,minTime);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isLongPress){
                    update();
                }
                break;
            case MotionEvent.ACTION_UP:
                
                isRelease = true;
                break;
        }
        return false;
    }

    private void update() {
        if(wlp==null){
            return;
        }
        
        wlp.x = (int)(x-newX);
        wlp.y=(int)(y-newY);
        wm.updateViewLayout(this,wlp);
    }
    public void show(){
        
        if(isAdded){
            return;
        }
        isAdded = true;
        wlp.gravity= Gravity.LEFT| Gravity.TOP;
        wlp.width = AllUtils.dip2px(mContext,50);
        wlp.height = AllUtils.dip2px(mContext,50);
        wlp.x=screenWidth- AllUtils.dip2px(mContext,50);
        wlp.y=screenHeight- AllUtils.dip2px(mContext,100);
        wlp.format = PixelFormat.TRANSLUCENT;
        wlp.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        if (Build.VERSION.SDK_INT>=26) {
            wlp.type= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else if(Build.VERSION.SDK_INT > 24) {
            wlp.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else {
            wlp.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        wm.addView(this,wlp);
        wm.updateViewLayout(this,wlp);
    }
    public void dismiss(){
        if(isAdded) {
            isAdded = false;
            wm.removeView(this);
        }
    }
}
