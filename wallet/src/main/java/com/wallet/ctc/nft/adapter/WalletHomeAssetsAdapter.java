

package com.wallet.ctc.nft.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.GlideUtil;

import butterknife.BindView;
import common.app.my.view.CircularImage;
import common.app.pojo.CurrencyBean;
import common.app.utils.SpUtil;

public class WalletHomeAssetsAdapter extends SkyAdapter<AssertBean, WalletHomeAssetsAdapter.AssetsHolder> {

    @NonNull
    @Override
    public AssetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_assets, parent, false);
        return new AssetsHolder(v);
    }

    public static class AssetsHolder extends SkyHolder<AssertBean> {

        @BindView(R2.id.iv_logo)
        CircularImage ivLogo;
        @BindView(R2.id.tv_name)
        TextView tvName;
        @BindView(R2.id.tv_amount)
        TextView tvAmount;
        @BindView(R2.id.tv_valuation)
        TextView tvValuation;
        @BindView(R2.id.v_bottom)
        View vBottom;

        public AssetsHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(AssertBean data, int position) {
            super.bindData(data, position);
            if (data.getLogo() == 0) {
                if (data.getImg_path().length() > 2) {
                    if (data.getType() == WalletUtil.DM_COIN) {
                        GlideUtil.showDmImg(mContext, data.getImg_path(), ivLogo);
                    } else if (data.getType() == WalletUtil.ETH_COIN) {
                        GlideUtil.showEthImg(mContext, data.getImg_path(), ivLogo);
                    } else if (data.getType() == WalletUtil.MCC_COIN) {
                        GlideUtil.showMccImg(mContext, data.getImg_path(), ivLogo);
                    } else if (data.getType() == WalletUtil.OTHER_COIN) {
                        GlideUtil.showOtherImg(mContext, data.getImg_path(), ivLogo);
                    } else {
                        GlideUtil.showImg(mContext, data.getImg_path(), ivLogo);
                    }
                } else {
                    GlideUtil.showImg(mContext, R.mipmap.eth_logo, ivLogo);
                }
            } else {
                GlideUtil.showImg(mContext, data.getLogo(), ivLogo);
            }
            tvName.setText(data.getShort_name().toUpperCase());
            tvAmount.setText(data.getAssertsNum());
            String dcu = SpUtil.getDcu();
            if (dcu.equals(common.app.BuildConfig.CURRENCY_UNIT)) {
                dcu = BuildConfig.CURRENCY_SYMBOL;
            } else {
                Gson gson = new Gson();
                CurrencyBean bean = gson.fromJson(dcu, CurrencyBean.class);
                if (null != bean) {
                    dcu = bean.getCurrency_symbol();
                }
            }
            tvValuation.setText("≈ " + dcu + " " + data.getAssertsSumPrice());
        }
    }
}
