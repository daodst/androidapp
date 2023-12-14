package com.app.white_black_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ItemWhiteBlackListBinding;
import com.wallet.ctc.model.blockchain.EvmosUserBean;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.TimeUtil;
import im.vector.app.provide.ChatStatusProvide;


public class WhiteBlackListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EvmosUserBean> mDatas;
    private boolean isEdited;
    private OnDelListener mOnDelListener;
    public WhiteBlackListAdapter(Context context, OnDelListener delListener) {
        this.mContext = context;
        this.mDatas = new ArrayList<>();
        this.mOnDelListener = delListener;
    }

    public interface OnDelListener{
        void onDel(EvmosUserBean userBean);
    }

    public void setEdited(boolean edited) {
        this.isEdited = edited;
        notifyDataSetChanged();
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void bindDatas(List<EvmosUserBean> datas) {
        if (null != datas) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemWhiteBlackListBinding binding;
        if (view == null) {
            binding = ItemWhiteBlackListBinding.inflate(LayoutInflater.from(mContext), viewGroup, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (ItemWhiteBlackListBinding) view.getTag();
        }
        setData(binding, i);
        return view;
    }

    private void setData(ItemWhiteBlackListBinding viewBinding, int position) {
        EvmosUserBean data = mDatas.get(position);
        if (isEdited) {
            viewBinding.delIv.setVisibility(View.VISIBLE);
            viewBinding.delIv.setOnClickListener(view -> {
                if (null != mOnDelListener) {
                    mOnDelListener.onDel(data);
                }
            });
        } else {
            viewBinding.delIv.setVisibility(View.GONE);
            viewBinding.delIv.setOnClickListener(null);
        }

        
        ChatStatusProvide.showAvatarRenderer(mContext, data.getAvatorUid(), data.nickName, data.logo, viewBinding.logoIv);
        viewBinding.nicknameTv.setText(data.nickName);
        viewBinding.addrTv.setText(mContext.getString(R.string.sm_addr_titlte)+data.address);
        viewBinding.didTv.setText("DID："+data.mobile);

        
        if (data.update_time > 0) {
            viewBinding.timeTv.setText(mContext.getString(R.string.sm_up_chian_time_title)+ TimeUtil.getYYYYMMddHHMM(data.update_time*1000));
        } else {
            viewBinding.timeTv.setText(mContext.getString(R.string.sm_up_chian_time_title)+"- -");
        }

        
        if (data.isEffect) {
            viewBinding.stateTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_theme_color));
            viewBinding.stateTv.setText(R.string.sm_has_effect);
        } else {
            viewBinding.stateTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_hint_text_color));
            viewBinding.stateTv.setText(R.string.sm_un_effect);
        }

    }
}
