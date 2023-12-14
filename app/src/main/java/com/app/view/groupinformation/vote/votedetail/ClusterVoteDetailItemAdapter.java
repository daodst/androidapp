package com.app.view.groupinformation.vote.votedetail;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ItemVoteDetialUpdateListBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.utils.AllUtils;


public class ClusterVoteDetailItemAdapter extends BaseQuickAdapter<EvmosClusterVoteDetailBean.Data.MessageEntity, ClusterVoteDetailItemAdapter.ClusterVoteDetailItemVH> {

    public ClusterVoteDetailItemAdapter(@Nullable List<EvmosClusterVoteDetailBean.Data.MessageEntity> data) {
        super(R.layout.item_vote_detial_update_list, data);
    }

    
    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull ClusterVoteDetailItemVH helper, EvmosClusterVoteDetailBean.Data.MessageEntity item) {

        if (!TextUtils.isEmpty(item.to_address) && null != item.amount && item.amount.size() > 0) {
            
            
            helper.mBinding.itemVoteDetialUpdateMoudle.setText(R.string.governance_pools_title);
            String builder = item.fromAddress.substring(0, 10) +
                    "..." +
                    item.fromAddress.substring(item.fromAddress.length() - 10);
            helper.mBinding.itemVoteDetialUpdateKey.setText(mContext.getString(R.string.in_wallet_address) + ":" + builder);

            helper.mBinding.itemVoteDetialUpdateValueName.setVisibility(View.VISIBLE);
            helper.mBinding.itemVoteDetialUpdateValue.setVisibility(View.VISIBLE);

            helper.mBinding.itemVoteDetialUpdateValueName.setText(R.string.amount_balance);
            EvmosClusterVoteDetailBean.Data.Amount amount = item.amount.get(0);
            BigDecimal amountDecimal = new BigDecimal(AllUtils.getTenDecimalValue(amount.amount, 18, 4));
            helper.mBinding.itemVoteDetialUpdateValue.setText(amountDecimal.toPlainString() + " " + amount.denom);

        } else if (!TextUtils.isEmpty(item.device_ratio) && !TextUtils.isEmpty(item.clusterId)) {
            

            
            

            helper.mBinding.itemVoteDetialUpdateMoudle.setText(R.string.pos_vote_title);

            BigDecimal multiply = new BigDecimal(item.device_ratio).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).stripTrailingZeros();

            helper.mBinding.itemVoteDetialUpdateKey.setText(mContext.getString(R.string.device_ratio) + ":" + multiply.toPlainString() + "%");

            helper.mBinding.itemVoteDetialUpdateValueName.setVisibility(View.GONE);
            helper.mBinding.itemVoteDetialUpdateValue.setVisibility(View.GONE);


        } else if (!TextUtils.isEmpty(item.approveAddress) && !TextUtils.isEmpty(item.approveEndBlock) && !TextUtils.isEmpty(item.clusterId)) {
            
            
            helper.mBinding.itemVoteDetialUpdateMoudle.setText(R.string.governance_authorization_title);
            String builder = item.approveAddress.substring(0, 10) +
                    "..." +
                    item.approveAddress.substring(item.approveAddress.length() - 10);
            helper.mBinding.itemVoteDetialUpdateKey.setText(mContext.getString(R.string.approveAddress) + builder);
            helper.mBinding.itemVoteDetialUpdateValueName.setText(R.string.block);
            helper.mBinding.itemVoteDetialUpdateValue.setText(": " + item.approveEndBlock);

            helper.mBinding.itemVoteDetialUpdateValueName.setVisibility(View.VISIBLE);
            helper.mBinding.itemVoteDetialUpdateValue.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(item.salary_ratio) && !TextUtils.isEmpty(item.clusterId)) {
            

            helper.mBinding.itemVoteDetialUpdateMoudle.setText(R.string.salary_ratio_title);

            BigDecimal multiply = new BigDecimal(item.salary_ratio).multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN).stripTrailingZeros();

            helper.mBinding.itemVoteDetialUpdateKey.setText(mContext.getString(R.string.salary_ratio) + multiply.toPlainString() + "%");

            helper.mBinding.itemVoteDetialUpdateValueName.setVisibility(View.GONE);
            helper.mBinding.itemVoteDetialUpdateValue.setVisibility(View.GONE);
        }

    }

    static class ClusterVoteDetailItemVH extends BaseViewHolder {
        public ItemVoteDetialUpdateListBinding mBinding;

        public ClusterVoteDetailItemVH(View view) {
            super(view);
            mBinding = ItemVoteDetialUpdateListBinding.bind(view);
        }
    }
}
