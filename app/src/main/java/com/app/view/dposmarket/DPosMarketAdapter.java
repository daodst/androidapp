package com.app.view.dposmarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.home.pojo.DposListEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.utils.AllUtils;


public class DPosMarketAdapter extends BaseQuickAdapter<DposListEntity.ValidatorListEntity, BaseViewHolder> {
    public DPosMarketAdapter(@Nullable List<DposListEntity.ValidatorListEntity> data) {
        super(R.layout.activity_dpos_market_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DposListEntity.ValidatorListEntity item) {
        
        BigDecimal commission = new BigDecimal(item.commision).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        String amount = AllUtils.getTenDecimalValue(item.validatorDelegateAmount, 18, 18);
        String personAmount = AllUtils.getTenDecimalValue(item.persionDelegateAmount, 18, 18);

        helper.setText(R.id.tvTitle, mContext.getString(R.string.dpos_new_item_string_1))
                .setText(R.id.tvTitleValue, item.validatorName)
                .setText(R.id.tvPledgeNumber, mContext.getString(R.string.dpos_new_string_sift_2))
                .setText(R.id.tvPledgeNumberValue, amount + " NXN")
                .setText(R.id.tvCommissions, mContext.getString(R.string.dpos_new_item_string_3))
                .setText(R.id.tvCommissionsValue, commission.toPlainString() + "%")
                .setText(R.id.tvPledgeDesc, mContext.getString(R.string.dpos_new_item_string_6, personAmount) + " NXN")
        ;
        helper.addOnClickListener(R.id.btnPledge);
    }
}
