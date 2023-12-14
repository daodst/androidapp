package common.app.base.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public abstract class BaseHolder<T, V extends ViewBinding> extends RecyclerView.ViewHolder {
    public Context mContext;

    public T mData = null;

    public int mPosition = 0;

    public V vb = null;
    public BaseAdapter.OnItemClick<T> mItemClickListener = null;
    public BaseAdapter.OnItemLongClick<T> mOnItemLongClick = null;
    public BaseAdapter.OnViewClick<T> mViewClick = null;

    public void setItemListener(BaseAdapter.OnItemClick<T> l) {
        mItemClickListener = l;
    }

    public void setItemLongClick(BaseAdapter.OnItemLongClick<T> l) {
        mOnItemLongClick = l;
    }

    public void setViewClick(BaseAdapter.OnViewClick<T> l) {
        mViewClick = l;
    }

    public void onItemClick() {
        if (null != mItemClickListener) {
            mItemClickListener.onItemClick(mData, mPosition);
        }
    }

    public void onViewClick(View v) {
        if (null != mItemClickListener) {
            mItemClickListener.onItemClick(mData, mPosition);
        }
    }

    public void onItemLongClick() {
        if (null != mOnItemLongClick) {
            mOnItemLongClick.onItemLongClick(mData, mPosition);
        }
    }

    public BaseHolder(V v) {
        super(v.getRoot());
        vb = v;
        mContext = itemView.getContext();
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
