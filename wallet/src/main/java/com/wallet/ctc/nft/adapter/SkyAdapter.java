


package com.wallet.ctc.nft.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class SkyAdapter<T, V extends SkyHolder> extends RecyclerView.Adapter<V> {

    public interface OnAdapterItemClick<T> {
        public void onItemClick(T t, int position);
    }

    public interface OnAdapterItemLongClick<T> {
        public void onItemLongClick(T t, int position);
    }

    private OnAdapterItemClick l = null;
    private OnAdapterItemLongClick lc = null;

    public void setClickListener(OnAdapterItemClick<T> l) {
        this.l = l;
    }
    public void setLongClickListener(OnAdapterItemLongClick<T> lc) {
        this.lc = lc;
    }

    public List<T> mDatas = null;

    public void setItems(List<T> items) {
        mDatas = items;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        holder.setItemListener(l);
        holder.setItemLongClick(lc);
        holder.bindData(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }


}
