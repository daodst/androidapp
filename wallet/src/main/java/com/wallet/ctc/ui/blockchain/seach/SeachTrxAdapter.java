

package com.wallet.ctc.ui.blockchain.seach;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TrxSeachBean;
import com.wallet.ctc.model.blockchain.TrxSeachDeatilBean;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.BaseActivity;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.SpUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SeachTrxAdapter extends BaseAdapter {
    private List<TrxSeachBean> list = new ArrayList<TrxSeachBean>();
    private List<AssertBean> addList = new ArrayList<>();
    private List<AssertBean> mustList = new ArrayList<>();
    private Context context;
    private WalletDBUtil walletDBUtil;
    private ColorMatrixColorFilter filter;
    public SeachTrxAdapter(Context context) {
        this.context = context;
        walletDBUtil= WalletDBUtil.getInstent(context);
        mustList= SettingPrefUtil.getMustAssets(context,"XRP");
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        filter = new ColorMatrixColorFilter(matrix);
    }

    public void bindData(List<TrxSeachBean> list) {
        this.list = list;
    }

    public void bindAddedData(List<AssertBean> addList) {
        this.addList = addList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_seach, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TrxSeachBean mBean = list.get(position);
        holder.assetsLogo.clearColorFilter();
        
        if (mBean.getImg_url().length() < 2) {
            GlideUtil.showImg(context, R.mipmap.trx_logo, holder.assetsLogo);
            holder.assetsLogo.setColorFilter(filter);
        } else {
            String imgUrl = getHttpUrl(mBean.getImg_url());
            GlideUtil.showImg(context, imgUrl, holder.assetsLogo);
        }
        holder.assetsName.setText(mBean.getAbbr());
        if (mBean.getAbbr().toUpperCase().equals("TRX")) {
            holder.assetsChoose.setVisibility(View.GONE);
        } else {
            holder.assetsChoose.setVisibility(View.VISIBLE);
        }
        holder.assetsConten.setText(mBean.getName());
        holder.assetsAddress.setText(mBean.getContract_address());
        holder.assetsAddress.setVisibility(View.VISIBLE);
        if (isadd(mBean)) {
            holder.assetsChoose.setChecked(true);
        } else {
            holder.assetsChoose.setChecked(false);
        }
        holder.assetsChoose.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) {
                seachTrxDetail(mBean.getContract_address());
            } else {
                walletDBUtil.delAssetsWallet(mBean.getAbbr(),mBean.getContract_address(), WalletUtil.TRX_COIN);
                walletDBUtil.delAssetsWallet(SettingPrefUtil.getWalletAddress(context), mBean.getAbbr(), mBean.getContract_address());
                for (int i = 0; i < addList.size(); i++) {
                    String address = addList.get(i).getContract().toLowerCase();
                    String shortName = addList.get(i).getShort_name().toLowerCase();
                    if (address.equalsIgnoreCase(mBean.getContract_address()) && shortName.equalsIgnoreCase(mBean.getAbbr())) {
                        addList.remove(i);
                        break;
                    }
                }
                ((BaseActivity) context).showLoading();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
    private TrxApi mApi = new TrxApi();
    public void seachTrxDetail(String address){
        Map<String, Object> params = new TreeMap();
        params.put("contract", address);
        mApi.getCoinDetail(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TrxSeachDeatilBean>(context) {
                    @Override
                    public void onNexts(TrxSeachDeatilBean baseEntity) {
                        TrxSeachDeatilBean.DataBean.TokenInfoBean mBean=baseEntity.getData().get(0).getTokenInfo();
                        AssertBean assbean = new AssertBean(mBean.getTokenLogo(), mBean.getTokenAbbr(), mBean.getTokenName(), mBean.getTokenId(), "", mBean.getTokenDecimal()+"", WalletUtil.TRX_COIN, 2);
                        assbean.setWalletAddress("");
                        addList.add(assbean);
                        walletDBUtil.addAssets(assbean);
                        AssertBean assbean2 = new AssertBean(mBean.getTokenLogo(), mBean.getTokenAbbr(), mBean.getTokenName(), mBean.getTokenId(), "", mBean.getTokenDecimal()+"", WalletUtil.TRX_COIN, 2);
                        assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                        walletDBUtil.addAssets(assbean2);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public static String getHttpUrl(String url){
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            url = SpUtil.getHostApi() + url;
        }
        return url;
    }

    
    private boolean isadd(TrxSeachBean mBean) {
        boolean isadd = false;
        for (int i = 0; i < addList.size(); i++) {
            String address = addList.get(i).getContract().toLowerCase();
            String shortName = addList.get(i).getShort_name().toLowerCase();
            if (address.equalsIgnoreCase(mBean.getContract_address()) && shortName.equalsIgnoreCase(mBean.getAbbr())) {
                isadd = true;
                break;
            }
        }
        return isadd;
    }

    class ViewHolder {
        @BindView(R2.id.assets_logo)
        ImageView assetsLogo;
        @BindView(R2.id.assets_name)
        TextView assetsName;
        @BindView(R2.id.assets_conten)
        TextView assetsConten;
        @BindView(R2.id.assets_choose)
        CheckBox assetsChoose;
        @BindView(R2.id.assets_address)
        TextView assetsAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

