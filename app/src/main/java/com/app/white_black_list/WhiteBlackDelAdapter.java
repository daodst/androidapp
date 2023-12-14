package com.app.white_black_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.databinding.ItemWhiteBlackDelBinding;
import com.wallet.ctc.model.blockchain.EvmosUserBean;

import java.util.ArrayList;
import java.util.List;

import im.vector.app.provide.ChatStatusProvide;


public class WhiteBlackDelAdapter extends RecyclerView.Adapter<WhiteBlackDelAdapter.DelItemViewHolder> {

    private List<EvmosUserBean> mDatas;
    private Context mContext;
    public WhiteBlackDelAdapter(Context context, OnDelListener delListener) {
        this.mContext = context;
        mDatas = new ArrayList<>();
        this.mOnDelListener = delListener;
    }

    OnDelListener mOnDelListener;
    public interface OnDelListener{
        void onDel(EvmosUserBean userBean);
    }

    public void bindDatas(List<EvmosUserBean> datas) {
        if (null != datas) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DelItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DelItemViewHolder(ItemWhiteBlackDelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DelItemViewHolder holder, int position) {
        holder.setData(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public class DelItemViewHolder extends RecyclerView.ViewHolder {
        ItemWhiteBlackDelBinding mBinding;
        public DelItemViewHolder(ItemWhiteBlackDelBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void setData(EvmosUserBean data) {
            ChatStatusProvide.showAvatarRenderer(mContext, data.getAvatorUid(), data.nickName, data.logo, mBinding.logoIv);
            mBinding.delIv.setOnClickListener(view -> {
                if (null != mOnDelListener) {
                    mOnDelListener.onDel(data);
                }
            });
        }
    }
}
