

package com.wallet.ctc.view.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wallet.ctc.R;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.pojo.CurrencyBean;


public class SeachRmbTypePopWindow extends PopupWindow {
   private List<CurrencyBean> data=new ArrayList<>();
   private CurrencyUnitAdapter mAdapter;
   private ListView listview;
    private View conentView;
    private IOnItemSelectListener mItemSelectListener;
    private Context context;

    public SeachRmbTypePopWindow(final Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.seach_rmb_type_pop_wallet, null);
        
        this.setContentView(conentView);
        this.setWidth(AllUtils.dip2px(context,105));
        
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        
        this.update();
        
        ColorDrawable dw = new ColorDrawable(0000000000);
        
        this.setBackgroundDrawable(dw);
        
        
        this.setAnimationStyle(R.style.AnimationPreview);
        listview=(ListView) conentView.findViewById(R.id.list_view);
        mAdapter=new CurrencyUnitAdapter(context,data);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mItemSelectListener.choose(position);
                dismiss();
            }
        });
    }
    public void bindData(List<CurrencyBean> list){
        data.clear();
        data.addAll(list);
        if(null!=mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setItemListener(IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public interface IOnItemSelectListener {
        void choose(int position);
    }

    
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            
            this.showAsDropDown(parent,0,-AllUtils.dip2px(context,10));
        } else {
            this.dismiss();
        }
    }
}
