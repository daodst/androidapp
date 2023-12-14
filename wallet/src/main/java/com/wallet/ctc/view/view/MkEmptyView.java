
package com.wallet.ctc.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.wallet.ctc.R;


public class MkEmptyView extends LinearLayout {


    public MkEmptyView(Context context) {
        super(context);
        initView(context);
    }

    public MkEmptyView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MkEmptyView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MkEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mk_orders_empty, this, true);
    }
}
