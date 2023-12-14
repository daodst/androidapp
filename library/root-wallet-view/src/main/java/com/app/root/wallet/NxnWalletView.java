package com.app.root.wallet;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class NxnWalletView extends RelativeLayout{
    private static final String TAG = "NxnWalletView";
    private String type="";
    public ImageView bgIv, cellIv, typeIv;
    public TextView countTv;
    public static final String TYPE_BSC = "BSC";
    public static final String TYPE_ETH = "ETH";
    public static final String TYPE_DST = "DST";
    private MoveAnimator mMoveAnimator;
    public NxnWalletView(@NonNull Context context, String t) {
        super(context);
        this.type = t;
        init();
    }

    public NxnWalletView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NxnWalletView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NxnWalletView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public static List<NxnWalletView> createViews(Context context) {
        List<NxnWalletView> list = new ArrayList<>();
        NxnWalletView bsc  =new NxnWalletView(context, TYPE_BSC);
        NxnWalletView eth  =new NxnWalletView(context, TYPE_ETH);
        NxnWalletView dst  =new NxnWalletView(context, TYPE_DST);
        list.add(bsc);
        list.add(eth);
        list.add(dst);
        return list;
    }


    private void init() {
        inflate(getContext(), R.layout.item_nxn_wallet_cell, this);
        bgIv = findViewById(R.id.walletBgIv);
        cellIv = findViewById(R.id.walletCellIv);
        typeIv = findViewById(R.id.walletTypeIv);
        countTv = findViewById(R.id.countTv);
        setType(this.type);
        mMoveAnimator = new MoveAnimator();
    }

    public void setType(String type) {
        this.type = type;
        if (TYPE_BSC.equalsIgnoreCase(type)) {
            bgIv.setImageResource(R.mipmap.w_nxn_bsc_bg);
            cellIv.setImageResource(R.mipmap.w_nxn_bsc_cell);
            typeIv.setImageResource(R.mipmap.w_nxn_bsc);
        } else if(TYPE_ETH.equalsIgnoreCase(type)) {
            bgIv.setImageResource(R.mipmap.w_nxn_eth_bg);
            cellIv.setImageResource(R.mipmap.w_nxn_eth_cell);
            typeIv.setImageResource(R.mipmap.w_nxn_eth);
        } else if(TYPE_DST.equalsIgnoreCase(type)) {
            bgIv.setImageResource(R.mipmap.w_nxn_dst_bg);
            cellIv.setImageResource(R.mipmap.w_nxn_dst_cell);
            typeIv.setImageResource(R.mipmap.w_nxn_dst);
        }
    }

    public void setCount(int count){
        if (null != countTv) {
            countTv.setText(count+"");
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            bgIv.setVisibility(VISIBLE);
        } else {
            bgIv.setVisibility(INVISIBLE);
        }
    }

    public String getType() {
        return type;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void startMove(float dx, float dy) {
        mMoveAnimator.start(dx, dy);
    }

    protected class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() == null || getRootView().getParent() == null) {
                return;
            }
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 300f);
            float deltaX = (destinationX - getX()) * progress;
            float deltaY = (destinationY - getY()) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    private void move(float deltaX, float deltaY) {
        setX(getX() + deltaX);
        setY(getY() + deltaY);
    }
}
