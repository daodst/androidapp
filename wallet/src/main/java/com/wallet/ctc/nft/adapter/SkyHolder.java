

package com.wallet.ctc.nft.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

public abstract class SkyHolder<T> extends RecyclerView.ViewHolder {
    public Context mContext;

    public T mData = null;

    public int mPosition = 0;

    public SkyAdapter.OnAdapterItemClick<T> mItemClickListener = null;
    public SkyAdapter.OnAdapterItemLongClick<T> tOnAdapterItemLongClick = null;

    public void setItemListener(SkyAdapter.OnAdapterItemClick<T> l) {
        mItemClickListener = l;
    }

    public void setItemLongClick(SkyAdapter.OnAdapterItemLongClick<T> l) {
        tOnAdapterItemLongClick = l;
    }

    public void onItemClick() {
        if (null != mItemClickListener) {
            mItemClickListener.onItemClick(mData, mPosition);
        }
    }

    public void onItemLongClick() {
        if (null != tOnAdapterItemLongClick) {
            tOnAdapterItemLongClick.onItemLongClick(mData, mPosition);
        }
    }

    public SkyHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick();
                return true;
            }
        });
    }

    public void bindData(T t, int position) {
        mData = t;
        mPosition = position;
    }
}
