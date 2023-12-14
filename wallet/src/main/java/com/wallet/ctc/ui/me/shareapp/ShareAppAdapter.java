

package com.wallet.ctc.ui.me.shareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShareAppAdapter extends RecyclerView.Adapter<ShareAppAdapter.ViewHolder> {


    private Context mActivity;
    private List<ShareAppBean> mData=new ArrayList<>();

    public ShareAppAdapter(Context mActivity) {
        this.mActivity = mActivity;
        mData = Collections.emptyList();
    }

    public void bind(List<ShareAppBean> mData) {
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ShareAppBean mEntity = mData.get(position);
        LogUtil.d(""+mEntity.getImage());
        GlideUtil.showImg(mActivity,mEntity.getImage(),holder.shareAd);
        if(mEntity.getChoose()==1){
            holder.choosedImg.setVisibility(View.VISIBLE);
        }else {
            holder.choosedImg.setVisibility(View.GONE);
        }
        holder.shareAd.setOnClickListener(v -> {
            for(int i=0;i<mData.size();i++){
                mData.get(i).setChoose(0);
            }
            mData.get(position).setChoose(1);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.share_ad)
        ImageView shareAd;
        @BindView(R2.id.choosed_img)
        ImageView choosedImg;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
