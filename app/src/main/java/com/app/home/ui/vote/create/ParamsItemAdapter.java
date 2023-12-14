package com.app.home.ui.vote.create;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ItemVoteParamsBinding;
import com.app.pojo.VoteParamsBean;

import java.util.ArrayList;
import java.util.List;


public class ParamsItemAdapter extends RecyclerView.Adapter<ParamsItemAdapter.ItemViewHolder> {

    private List<VoteParamsBean> mDatas;
    public interface SizeChangeListener{
        void onChange(int size);
    }
    private SizeChangeListener mChangeListener;
    public ParamsItemAdapter(SizeChangeListener listener) {
        mDatas = new ArrayList<>();
        mChangeListener = listener;
    }

    public void cleanAndAddAll(List<VoteParamsBean> datas) {
        if (null != datas) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
        postSizeChange();
    }

    public void add(VoteParamsBean data) {
        if (null != data) {
            mDatas.add(0, data);
            notifyDataSetChanged();

        }
        postSizeChange();
    }

    public void delete(int index) {
        if (mDatas.size() > 0 && index < mDatas.size()) {
            mDatas.remove(index);
            notifyDataSetChanged();
        }
        postSizeChange();
    }

    
    private void postSizeChange() {
        if (null != mChangeListener) {
            mChangeListener.onChange(mDatas.size());
        }
    }

    public List<VoteParamsBean> getDatas() {
        return mDatas;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemVoteParamsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        ItemVoteParamsBinding mViewBing;
        public ItemViewHolder(ItemVoteParamsBinding viewBing) {
            super(viewBing.getRoot());
            mViewBing = viewBing;
        }

        public void setData(VoteParamsBean data, int position) {
            mViewBing.subspaceTv.setText(data.subspace);
            mViewBing.keyTv.setText(data.key);
            mViewBing.valueTv.setText(data.value);
            mViewBing.delTv.setOnClickListener(view -> {
                delete(position);
            });
            if ((position+1) % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#F6F7FB"));
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.default_top_background_color));
            }
        }
    }
}
