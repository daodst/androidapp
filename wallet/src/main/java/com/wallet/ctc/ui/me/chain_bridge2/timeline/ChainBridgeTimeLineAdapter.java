package com.wallet.ctc.ui.me.chain_bridge2.timeline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ItemChainBridgeTimelineBinding;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.mall.util.ToastUtil;


public class ChainBridgeTimeLineAdapter extends RecyclerView.Adapter<ChainBridgeTimeLineAdapter.ViewHolder> {

    private Context mContext;
    private List<ChainBridgeDetailStepEntity> mDatas;
    public ChainBridgeTimeLineAdapter(Context context) {
        this.mContext = context;
        this.mDatas = new ArrayList<>();
    }

    public void bindDatas(List<ChainBridgeDetailStepEntity> steps) {
        this.mDatas.clear();
        if (null != steps) {
            mDatas.addAll(steps);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChainBridgeTimelineBinding vbinding = ItemChainBridgeTimelineBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(vbinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDatas.get(position), position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemChainBridgeTimelineBinding viewBinding;
        public ViewHolder(ItemChainBridgeTimelineBinding vBinding) {
            super(vBinding.getRoot());
            viewBinding = vBinding;
        }

        public void setData(ChainBridgeDetailStepEntity data, int position, int size){

            viewBinding.stepTv.setText(data.title);
            viewBinding.timeTv.setText(data.content);
            viewBinding.timeTv.setVisibility(View.GONE);
            if (data.isAlert()){
                viewBinding.circleView.setBackground(mContext.getDrawable(R.drawable.ico_circle_yellow));
                viewBinding.stepTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_tip_color));
            } else {
                viewBinding.circleView.setBackground(mContext.getDrawable(R.drawable.ico_circle_theme));
                viewBinding.stepTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_theme_color));
            }


            if (position == 0){
                viewBinding.lineView1.setVisibility(View.INVISIBLE);
            } else {
                viewBinding.lineView1.setVisibility(View.VISIBLE);
            }

            if (position == size-1){
                viewBinding.lineView2.setVisibility(View.INVISIBLE);
            } else {
                viewBinding.lineView2.setVisibility(View.VISIBLE);
            }


            viewBinding.hashTv.setOnClickListener(null);
            viewBinding.shareIv.setOnClickListener(null);
            if(TextUtils.isEmpty(data.withdrawCode)){
                viewBinding.hashLayout.setVisibility(View.GONE);
            } else {
                viewBinding.hashLayout.setVisibility(View.VISIBLE);
                viewBinding.hashTv.setText(data.withdrawCode);
                viewBinding.hashTv.setOnClickListener(view -> {
                    AllUtils.copyText(data.withdrawCode);
                    ToastUtil.showToast(R.string.copy_success);
                });
                viewBinding.shareIv.setOnClickListener(view -> {
                    
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, data.withdrawCode);
                    
                    sendIntent.setType("text/plain");
                    mContext.startActivity(Intent.createChooser(sendIntent, null));
                });
            }
        }
    }
}
