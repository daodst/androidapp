

package com.wallet.ctc.view.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.wallet.ctc.R;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.WalletSpUtil;


public class SeachTypePopWindow extends PopupWindow {
    LinearLayout productGohome;
    LinearLayout gouwuche;
    LinearLayout eosLin;
    LinearLayout otherCoin;
    private View conentView;
    private IOnItemSelectListener mItemSelectListener;
    private Context context;

    public SeachTypePopWindow(final Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.seach_type_pop_wallet, null);
        
        this.setContentView(conentView);
        this.setWidth(AllUtils.dip2px(context,68));
        
        this.setHeight(LayoutParams.WRAP_CONTENT);
        
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        
        this.update();
        
        ColorDrawable dw = new ColorDrawable(0000000000);
        
        this.setBackgroundDrawable(dw);
        
        
        this.setAnimationStyle(R.style.AnimationPreview);
        productGohome=(LinearLayout)conentView.findViewById(R.id.product_gohome);
        gouwuche=(LinearLayout)conentView.findViewById(R.id.gouwuche);
        eosLin=(LinearLayout)conentView.findViewById(R.id.eos_lin);
        otherCoin=(LinearLayout)conentView.findViewById(R.id.other_coin);
        if(WalletSpUtil.getEnableMcc() ==0){
            productGohome.setVisibility(View.GONE);
        }
        if(WalletSpUtil.getEnableDm()==0){
            eosLin.setVisibility(View.GONE);
        }
        if(WalletSpUtil.getEnableEth()==0){
            gouwuche.setVisibility(View.GONE);
        }
        if(WalletSpUtil.getEnableOther()==0){
            otherCoin.setVisibility(View.GONE);
        }
        productGohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemSelectListener.choose(0);
            }
        });
        gouwuche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemSelectListener.choose(2);
            }
        });
        eosLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemSelectListener.choose(1);
            }
        });
        otherCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemSelectListener.choose(3);
            }
        });
    }

    public void setItemListener(IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public interface IOnItemSelectListener {
        void choose(int position);
    }

    
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            
            this.showAsDropDown(parent, -AllUtils.dip2px(context,10),0);
        } else {
            this.dismiss();
        }
    }
}
