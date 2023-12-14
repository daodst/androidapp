package com.wallet.ctc.ui.me.chain_bridge2.detail;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChainBridgeDetailItemBinding;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.model.me.ChainBridgeDetailStepEntity;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.mall.util.ToastUtil;
import common.app.utils.TimeUtil;


public class ChainBridgeDetailAdapter extends BaseQuickAdapter<ChainBridgeOrderBean, ChainBridgeDetailAdapter.MyViewHolder> {
    
    public ChainBridgeDetailAdapter(@Nullable List<ChainBridgeOrderBean> data) {
        super(R.layout.activity_chain_bridge_detail_item, data);
    }

    
    @Override
    protected void convert(@NonNull ChainBridgeDetailAdapter.MyViewHolder helper, ChainBridgeOrderBean data) {
        


        
        helper.binding.statusImage.setImageResource(data.getChildStatusImgRes());
        helper.binding.statusName.setText(data.getChildStatusShowTitle(mContext));
        helper.binding.statusName.setTextColor(ContextCompat.getColor(mContext, data.getChildStatusTitleColorRes()));


        int buyWalletType = ChatSdk.chainNameToType(data.buy_chain);
        AssertBean buyAssert = WalletUtil.getUsdtAssert(buyWalletType);
        AssertBean buyChainAssert = getShowAssert(buyWalletType);

        int sellWalletType = ChatSdk.chainNameToType(data.sell_chain);
        AssertBean sellAssert = WalletUtil.getUsdtAssert(sellWalletType);
        AssertBean sellChainAssert = getShowAssert(sellWalletType);
        String amount = AllUtils.getTenDecimalValue(data.amount, 18, 6);


        helper.binding.payAmount.setText(amount+ " " +buyAssert.getShortNameUpCase() +" ("+data.getShowBuyChain()+")");

        String receiveAmount = "";
        try {
            receiveAmount = new BigDecimal(amount).multiply(new BigDecimal(data.getRate())).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        helper.binding.receiverNum.setText(TextUtils.isEmpty(receiveAmount) ? "--" : receiveAmount);
        String realReceiveAmount = "";
        
        if (data.order_type == 4){
            
            realReceiveAmount = receiveAmount;
        } else {
            realReceiveAmount = "0";
        }
        helper.binding.receiverAmount.setText(realReceiveAmount+ " "+ sellAssert.getShortNameUpCase() +" ("+data.getShowSellChain()+")");

        
        helper.binding.price.setText("1="+data.getRate());


        
        String depositAmount = data.getDepositAmount(sellAssert.getDecimal());
        String depositStatus = "";
        String depositLockTime = "- -";
        if ("0".equals(depositAmount)){
            
            helper.binding.deposit.setText("0 "+sellAssert.getShortNameUpCase()+" ("+data.getShowSellChain()+")");
        } else {
            
            if (data.deposit_status == 2){
                
                depositStatus = mContext.getString(R.string.chain_b_deposite_has_quhui);
            } else {
                
                depositStatus = mContext.getString(R.string.chain_b_depo_wait_quhui);
                depositLockTime = TimeUtil.getYYYYMMddHHMM(data.deposit_lock_end_time*1000);
            }
        }
        helper.binding.deposit.setText(depositAmount+" "+sellAssert.getShortNameUpCase()+" ("+data.getShowSellChain()+") "+ depositStatus);
        helper.binding.lockInTime.setText(depositLockTime);
        
        helper.binding.sellChainIv.setImageResource(sellChainAssert.getLogo());
        helper.binding.buyChainIv.setImageResource(buyChainAssert.getLogo());
        helper.binding.seller.setText(data.seller);
        helper.binding.seller.setOnClickListener(view -> {
            AllUtils.copyText(data.seller);
            ToastUtil.showToast(R.string.copy_success);
        });
        helper.binding.orderNum.setText(data.order_key);
        helper.binding.orderLockInTime.setText(TimeUtil.getYYYYMMddHHMM(data.end_lock_time*1000));
        
        if (data.isChildOrderOver()){
            helper.binding.orderMade.setText(TimeUtil.getYYYYMMddHHMM(data.update_time*1000));
        } else {
            helper.binding.orderMade.setText(R.string.chain_b_order_no_complete);
        }

        helper.binding.paymentAddr.setText(data.payer);
        helper.binding.paymentAddr.setOnClickListener(view -> {
            AllUtils.copyText(data.payer);
            ToastUtil.showToast(R.string.copy_success);
        });
        helper.binding.withdrawAddr.setText(data.buyer);
        helper.binding.withdrawAddr.setOnClickListener(view -> {
            AllUtils.copyText(data.buyer);
            ToastUtil.showToast(R.string.copy_success);
        });

        
        List<ChainBridgeDetailStepEntity> list = data.getChildOrderSteps(mContext);
        helper.binding.orderStep.setStepViewTexts(list);

        helper.binding.rlReceiver.setVisibility(data.isExpand ? View.VISIBLE : View.GONE);
        helper.binding.llHide.setVisibility(data.isExpand ? View.VISIBLE : View.GONE);
        helper.binding.bodyStep.setVisibility(data.isExpand ? View.VISIBLE : View.GONE);

        helper.addOnClickListener(R.id.rlBottom);
        if (data.isExpand) {
            helper.binding.tvExpand.setText(mContext.getString(R.string.chain_b_order_step_shouqi));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_chain_bridge_close);
            helper.binding.tvExpand.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        } else {
            helper.binding.tvExpand.setText(mContext.getString(R.string.chain_b_order_step_zhankai));
            Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_chain_bridge_open);
            helper.binding.tvExpand.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    static class MyViewHolder extends BaseViewHolder {
        private ActivityChainBridgeDetailItemBinding binding;

        public MyViewHolder(View view) {
            super(view);
            binding = ActivityChainBridgeDetailItemBinding.bind(view);
        }
    }


    
    private AssertBean getShowAssert(int walletType) {
        List<AssertBean> mustAsset = WalletDBUtil.getInstent(mContext).getMustWallet(walletType);
        if (null != mustAsset && mustAsset.size() > 0) {
            return mustAsset.get(0);
        }
        return null;
    }

}
