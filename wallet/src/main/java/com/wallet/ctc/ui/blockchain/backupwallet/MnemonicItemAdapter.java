package com.wallet.ctc.ui.blockchain.backupwallet;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.databinding.ItemMnemonicLayoutBinding;
import com.wallet.ctc.model.blockchain.BackUpMnemonicBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class MnemonicItemAdapter extends RecyclerView.Adapter<MnemonicItemAdapter.ViewHolder> {

    private List<BackUpMnemonicBean> mDatas;
    private final int mItemCount;

    
    public MnemonicItemAdapter(int itemCount) {
        mDatas = new ArrayList<>();
        mItemCount = itemCount;
    }

    public void bindDatas(List<BackUpMnemonicBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    
    public List<String> getResult() {
        List<String> list = new ArrayList<>();
        if (mDatas.size() > 0) {
            for(BackUpMnemonicBean bean : mDatas) {
                list.add(bean.getText());
            }
        }
        return list;
    }

    
    public void addItem(BackUpMnemonicBean data) {
        mDatas.add(data);
        notifyDataSetChanged();
    }

    
    public void removeItem(int position){
        if (mDatas.size() > 0 && position < mDatas.size()) {
            mDatas.remove(position);
            notifyDataSetChanged();
        }
    }

    
    public void setChoose(boolean choosed, int position) {
        if (mDatas.size() > 0 && position < mDatas.size()) {
            mDatas.get(position).setChoose(choosed);
            notifyDataSetChanged();
        }
    }

    
    public void setUnChoosedAndShuffle(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (mDatas.size() > 0) {
            boolean success = false;
            for (int i=0; i<mDatas.size(); i++) {
                if (text.equals(mDatas.get(i).getText()) && mDatas.get(i).isChoose()) {
                    mDatas.get(i).setChoose(false);
                    success = true;
                    break;
                }
            }
            if (success) {
                Collections.shuffle(mDatas);
                notifyDataSetChanged();
            }
        }
    }

    
    public void changeChooseAndShuffle(boolean choosed, int position) {
        if (mDatas.size() > 0 && position < mDatas.size()) {
            mDatas.get(position).setChoose(choosed);
            Collections.shuffle(mDatas);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMnemonicLayoutBinding binding = ItemMnemonicLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getItem(position), position);
    }

    public BackUpMnemonicBean getItem(int position) {
        if (mDatas.size() > 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public abstract void onBindView(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position);
    public abstract void onItemClick(ItemMnemonicLayoutBinding view, BackUpMnemonicBean data, int position);


    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemMnemonicLayoutBinding mViewB;
        public ViewHolder(ItemMnemonicLayoutBinding binding) {
            super(binding.getRoot());
            mViewB = binding;
        }

        public void setData(BackUpMnemonicBean item, int position) {
            itemView.setOnClickListener(view -> {
                onItemClick(mViewB, item, position);
            });
            onBindView(mViewB, item, position);
        }
    }
}
