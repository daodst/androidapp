package com.app.view.privatedvm;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ActivityPrivateDvmDefaultItemBinding;
import com.app.databinding.ActivityPrivateDvmItemBinding;
import com.app.databinding.ActivityPrivateDvmPowerItemBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;


public class MyPrivateDVMAdapter extends BaseQuickAdapter<EvmosDvmListBean.Data, MyPrivateDVMAdapter.MyPrivateDVMVH> {
    private int decimal = 0;

    public MyPrivateDVMAdapter(@Nullable List<EvmosDvmListBean.Data> data) {
        super(R.layout.activity_private_dvm_item, data);
    }

    @Override
    protected void convert(@NonNull MyPrivateDVMAdapter.MyPrivateDVMVH helper, EvmosDvmListBean.Data item) {
        if (null == item) return;

        
        ImageView avatar = helper.getView(R.id.ivAvatar);
        GlideUtil.showImg(mContext, R.mipmap.icon_private_default_avatat, avatar);


        
        helper.addOnClickListener(R.id.btnReward, R.id.btnPower, R.id.btnReward2, R.id.tvPowerContract, R.id.tvPowerContract2, R.id.btnPowerDetail);

        if (item.isHasDvmContract()) {
            showPower(helper, item);
        } else {
            showDefault(helper, item);
        }
    }

    
    private void showDefault(@NonNull MyPrivateDVMAdapter.MyPrivateDVMVH helper, EvmosDvmListBean.Data data) {
        helper.setGone(R.id.layoutDefault, true).setGone(R.id.layoutPower, false);
        ActivityPrivateDvmDefaultItemBinding binding = helper.mBinding.layoutDefault;
        binding.tvName.setText(data.cluster_name);
        binding.tvGas.setText(assetsExchange(data.power_dvm));
        binding.tvGasDay.setText(assetsExchange(data.gas_day_dvm));
        binding.tvPowerContract.setText(TextUtils.isEmpty(data.auth_contract) ? "--" : data.auth_contract);
        binding.tvBlockHeight.setText(data.auth_height == 0 ? "--" : data.auth_height + "");

        String rewardAmount = assetsExchange(data.power_reward);
        String s1 = mContext.getString(R.string.dvm_virtual_machine_string_2);
        String html = "<font color='#374843'>" + s1 + "</font> <font color='#0BBD8B'>" + rewardAmount + "</font> <font color='#364742'>" + mContext.getString(R.string.default_token_name2).toUpperCase() + "</font>";
        binding.tvAwardDst.setText(Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        if(TextUtils.isEmpty(rewardAmount) || Float.parseFloat(rewardAmount) == 0){
            binding.btnReward.setBackgroundResource(R.drawable.button_gray_18);
            binding.btnReward.setTextColor(ContextCompat.getColor(mContext,R.color.default_hint_text_color));
        } else {
            binding.btnReward.setBackgroundResource(R.drawable.button_green_18);
            binding.btnReward.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        }
    }

    
    private void showPower(@NonNull MyPrivateDVMAdapter.MyPrivateDVMVH helper, EvmosDvmListBean.Data data) {
        ActivityPrivateDvmPowerItemBinding binding = helper.mBinding.layoutPower;
        helper.setGone(R.id.layoutDefault, false).setGone(R.id.layoutPower, true);
        binding.tvName2.setText(data.cluster_name);
        binding.tvGas2.setText(assetsExchange(data.power_dvm));
        binding.tvGasDay2.setText(assetsExchange(data.gas_day_dvm));


        String authContract = "";
        if (!TextUtils.isEmpty(data.auth_contract)){
            authContract = data.auth_contract;
        } else if(data.is_owner){
            authContract = data.cluster_name;
        }
        binding.tvPowerContract2.setText(TextUtils.isEmpty(authContract) ? "--" : authContract);

        String authHeight = "";
        if(data.is_owner){
            authHeight = mContext.getString(R.string.auth_always);
        } else {
            if (data.auth_height == 0){
                authHeight = "";
            } else {
                authHeight = data.auth_height+"";
            }
        }
        binding.tvBlockHeight2.setText(TextUtils.isEmpty(authHeight) ? "--" : authHeight);

        String rewardAmount = assetsExchange(data.power_reward);
        String html = "<font color='#0BBD8B'>" + rewardAmount + "</font> <font color='#6CA195'>" + mContext.getString(R.string.default_token_name2).toUpperCase() + "</font>";
        binding.tvAwardDstValue2.setText(Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        if(TextUtils.isEmpty(rewardAmount) || Float.parseFloat(rewardAmount) == 0){
            binding.btnReward2.setBackgroundResource(R.drawable.button_gray_18);
            binding.btnReward2.setTextColor(ContextCompat.getColor(mContext,R.color.default_hint_text_color));
        } else {
            binding.btnReward2.setBackgroundResource(R.drawable.button_green_18);
            binding.btnReward2.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        }

    }

    private String assetsExchange(String bigAmount) {
        if (decimal == 0) {
            String dstCoinName = mContext.getString(R.string.default_token_name2);
            AssertBean assertBean = WalletDBUtil.getInstent(mContext).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
            if (null != assertBean) {
                decimal = assertBean.getDecimal();
            }
            if (decimal == 0) {
                decimal = 18;
            }
        }
        return AllUtils.getTenDecimalValue(bigAmount, decimal, 3);
    }

    static class MyPrivateDVMVH extends BaseViewHolder {
        
        public ActivityPrivateDvmItemBinding mBinding;

        public MyPrivateDVMVH(View view) {
            super(view);
            mBinding = ActivityPrivateDvmItemBinding.bind(view);
        }
    }
}
