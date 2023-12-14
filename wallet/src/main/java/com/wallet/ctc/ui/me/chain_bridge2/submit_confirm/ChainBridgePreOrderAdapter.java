package com.wallet.ctc.ui.me.chain_bridge2.submit_confirm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ItemChainBridgePreOrderBinding;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;


public class ChainBridgePreOrderAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChainBridgeOrderBean> mDatas;
    public ChainBridgePreOrderAdapter(Context context){
        this.mContext = context;
        this.mDatas = new ArrayList<>();
    }
    public void bindDatas(List<ChainBridgeOrderBean> datas){
        this.mDatas.clear();
        if (null != datas){
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ItemChainBridgePreOrderBinding viewBinding;
        if (contentView == null) {
            viewBinding = ItemChainBridgePreOrderBinding.inflate(LayoutInflater.from(mContext), viewGroup, false);
            contentView = viewBinding.getRoot();
            contentView.setTag(viewBinding);
        } else {
            viewBinding = (ItemChainBridgePreOrderBinding) contentView.getTag();
        }
        ChainBridgeOrderBean order = mDatas.get(i);
        String tenAmount = AllUtils.getTenDecimalValue(order.amount, 18, 6);
        viewBinding.numTv.setText(mContext.getString(R.string.chain_bridge_pipei_num_title)+tenAmount);
        int index = i+1;
        viewBinding.countTv.setText(mContext.getString(R.string.order)+index);
        viewBinding.exRadioTv.setText("1 = "+order.getRate());
        return contentView;
    }


}
