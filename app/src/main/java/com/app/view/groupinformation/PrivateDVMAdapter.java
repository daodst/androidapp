package com.app.view.groupinformation;

import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.BuildConfig;
import com.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;


public class PrivateDVMAdapter extends BaseQuickAdapter<EvmosMyGroupDataBean, BaseViewHolder> {
    private int decimal = 0;

    public PrivateDVMAdapter(@Nullable List<EvmosMyGroupDataBean> data) {
        super(R.layout.activity_private_dvm_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, EvmosMyGroupDataBean item) {
        if (null == item || null == item.data) {
            return;
        }
        if (item.data.isHasDvmContract()) {
            showPower(helper, item);
        } else {
            showDefault(helper, item);
        }
    }

    
    private void showDefault(@NonNull BaseViewHolder helper, EvmosMyGroupDataBean item) {
        EvmosMyGroupDataBean.Data data = item.data;
        helper.setGone(R.id.layoutDefault, true).setGone(R.id.layoutPower, false);

        helper.setText(R.id.tvGas, assetsExchange(data.power_amount))
                .setText(R.id.tvGasDay, assetsExchange(data.gas_day));

        helper.setText(R.id.tvPowerContract, TextUtils.isEmpty(data.auth_contract) ? "--" : data.auth_contract);
        helper.setText(R.id.tvBlockHeight, TextUtils.isEmpty(data.auth_height) ? "--" : data.auth_height);

        TextView textView = helper.getView(R.id.tvAwardDst);
        String s1 = mContext.getString(R.string.dvm_virtual_machine_string_2);
        String html = "<font color='#374843'>" + s1 + "</font> <font color='#0BBD8B'>" + assetsExchange(data.power_reward) + "</font> <font color='#364742'>" + mContext.getString(R.string.default_token_name2).toUpperCase() + "</font>";
        textView.setText(Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        helper.addOnClickListener(R.id.btnReward, R.id.btnPower);
    }

    
    private void showPower(@NonNull BaseViewHolder helper, EvmosMyGroupDataBean item) {
        EvmosMyGroupDataBean.Data data = item.data;
        helper.setGone(R.id.layoutDefault, false).setGone(R.id.layoutPower, true);

        helper.setText(R.id.tvGas2, assetsExchange(data.power_amount))
                .setText(R.id.tvGasDay2, assetsExchange(data.gas_day));

        String authContract = "";
        if (!TextUtils.isEmpty(data.auth_contract)){
            authContract = data.auth_contract;
        } else if(data.is_owner){
            authContract = data.cluster_name;
        }

        helper.setText(R.id.tvPowerContract2, TextUtils.isEmpty(authContract) ? "--" : authContract);

        String authHeight = "";
        if(data.is_owner){
            authHeight = mContext.getString(R.string.auth_always);
        } else {
            authHeight = data.auth_height;
        }
        helper.setText(R.id.tvBlockHeight2, TextUtils.isEmpty(authHeight) ? "--" : authHeight);



        TextView textView = helper.getView(R.id.tvAwardDstValue2);
        String s1 = mContext.getString(R.string.dvm_virtual_machine_string_2);
        String html = "<font color='#6CA195'>" + s1 + "</font> <font color='#0BBD8B'>" + assetsExchange(data.power_reward) + "</font> <font color='#6CA195'>" + mContext.getString(R.string.default_token_name2).toUpperCase() + "</font>";
        textView.setText(Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS));

        helper.addOnClickListener(R.id.btnReward2, R.id.tvPowerContract2, R.id.btnPowerDetail);
    }

    private String assetsExchange(String bigAmount) {
        if (decimal == 0) {
            String dstCoinName = BuildConfig.SCHEME.toLowerCase();
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


}
