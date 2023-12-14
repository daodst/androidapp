

package com.wallet.ctc.ui.me.virtualphone;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.R;
import com.wallet.ctc.model.me.SMVirtualPhoneEntity;

import java.util.List;

import common.app.mall.util.ToastUtil;


public class SMVirtualPhoneAdapter extends BaseQuickAdapter<SMVirtualPhoneEntity, BaseViewHolder> {

    public interface TransferClickListener {
        void onTransferMobiel(String mobile);
    }
    private TransferClickListener mTransferListener;
    public SMVirtualPhoneAdapter(@Nullable List<SMVirtualPhoneEntity> data, TransferClickListener listener) {
        super(R.layout.sm_activity_virtual_phone_item, data);
        this.mTransferListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, SMVirtualPhoneEntity item) {
        helper.setText(R.id.tv_phone, item.phoneNumber)
                .setTextColor(R.id.tv_phone, item.checked
                        ? ContextCompat.getColor(mContext, R.color.white)
                        : ContextCompat.getColor(mContext, R.color.default_text_three_color));

        RelativeLayout root = helper.getView(R.id.rl_root);
        ImageView logo = helper.getView(R.id.iv_logo);
        ImageView call = helper.getView(R.id.iv_call);
        ImageView check = helper.getView(R.id.iv_check);
        if (item.checked) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.sm_bg_virtual_phone);
            root.setBackground(drawable);
            logo.setImageResource(R.mipmap.sm_virtual_icon_logo_check);
            call.setImageResource(R.mipmap.sm_virtual_icon_phone);
            call.setVisibility(View.VISIBLE);
            check.setImageResource(R.mipmap.sm_virtual_icon_checked);
        } else {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.sm_bg_virtual_phone_white);
            root.setBackground(drawable);
            logo.setImageResource(R.mipmap.sm_virtual_icon_logo_uncheck);
            call.setVisibility(View.GONE);
            check.setImageResource(R.mipmap.sm_virtual_icon_uncheck);
        }
        logo.setOnClickListener(view -> {
            if(item.checked){
                ToastUtil.showToast(R.string.hint_did_trans_current);
                return;
            }
            if (null != mTransferListener) {
                mTransferListener.onTransferMobiel(item.phoneNumber);
            }
        });
    }
}
