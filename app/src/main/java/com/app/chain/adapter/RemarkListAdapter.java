package com.app.chain.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.LayoutItemRemarkBinding;
import com.wallet.ctc.util.AllUtils;

import org.matrix.android.sdk.internal.session.remark.Remark;

import java.util.List;

import common.app.base.adapter.BaseAdapter;
import common.app.base.adapter.BaseHolder;
import common.app.utils.TimeUtil;
import im.vector.app.provide.ChatStatusProvide;


public class RemarkListAdapter extends BaseAdapter<Remark, RemarkListAdapter.RemarkHolder> {

    @NonNull
    @Override
    public RemarkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RemarkHolder(createBinding(LayoutItemRemarkBinding.class, parent));
    }

    
    public int getUnUpChainCount() {
        List<Remark> remarks = mDatas;
        int count = 0;
        if (null != remarks && remarks.size() > 0) {
            for (Remark remark : remarks) {
                if (remark.isSync() == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static class RemarkHolder extends BaseHolder<Remark, LayoutItemRemarkBinding> {

        public RemarkHolder(LayoutItemRemarkBinding layoutItemRemarkBinding) {
            super(layoutItemRemarkBinding);
            
        }

        @Override
        public void bindData(Remark remark, int position) {
            super.bindData(remark,position);
            ChatStatusProvide.showAvatarRenderer(mContext, "@" + mData.getAddress(), mData.getRemark(), mData.getAvatarUrl(), vb.logoIv);
            vb.nicknameTv.setText(mData.getRemark());
            vb.addrTv.setText(mContext.getString(R.string.sm_addr_titlte) + AllUtils.getAddressByUid(mData.getUserId()));
            vb.didTv.setText("DID：" + mData.getDid());

            
            if (mData.getSyncTime() != null && mData.getSyncTime() > 0) {
                vb.timeTv.setText(mContext.getString(R.string.sm_up_chian_time_title) + TimeUtil.getYYYYMMddHHMM(mData.getSyncTime() * 1000));
            } else {
                vb.timeTv.setText(mContext.getString(R.string.sm_up_chian_time_title) + "- -");
            }

            
            if (mData.isSync() == 0) {
                vb.stateTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_hint_text_color));
                vb.stateTv.setText(R.string.sm_un_effect);
            } else {
                vb.stateTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_theme_color));
                vb.stateTv.setText(R.string.sm_has_effect);
            }
        }
    }

}
