

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
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.XrpAssertBean;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.BaseActivity;
import common.app.utils.SpUtil;



public class SeachXrpAdapter extends BaseAdapter {
    private List<XrpAssertBean> list = new ArrayList<XrpAssertBean>();
    private List<AssertBean> addList = new ArrayList<>();
    private List<AssertBean> mustList = new ArrayList<>();
    private Context context;
    private WalletDBUtil walletDBUtil;
    private ColorMatrixColorFilter filter;
    public SeachXrpAdapter(Context context) {
        this.context = context;
        walletDBUtil= WalletDBUtil.getInstent(context);
        mustList= SettingPrefUtil.getMustAssets(context,"XRP");
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        filter = new ColorMatrixColorFilter(matrix);
    }

    public void bindData(List<XrpAssertBean> list) {
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
        XrpAssertBean mBean = list.get(position);
        
        holder.assetsLogo.clearColorFilter();
        if (mBean.getLogo().length() < 2) {
            holder.assetsLogo.setColorFilter(filter);
            GlideUtil.showImg(context, R.mipmap.xrp_logo, holder.assetsLogo);
        } else {
            String imgUrl = getHttpUrl(mBean.getLogo());
            GlideUtil.showImg(context, imgUrl, holder.assetsLogo);
        }
        holder.assetsName.setText(mBean.getCurrency());
        if (mBean.getCurrency().toUpperCase().equals("XRP")) {
            holder.assetsChoose.setVisibility(View.GONE);
        } else {
            holder.assetsChoose.setVisibility(View.VISIBLE);
        }
        holder.assetsConten.setText("");
        holder.assetsAddress.setText(mBean.getShortAddress());
        holder.assetsAddress.setVisibility(View.VISIBLE);
        if (isadd(mBean)) {
            holder.assetsChoose.setChecked(true);
        } else {
            holder.assetsChoose.setChecked(false);
        }
        holder.assetsChoose.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) {
                AssertBean assbean = new AssertBean(getHttpUrl(mBean.getLogo()), mBean.getCurrency(), mBean.getCurrency(), mBean.getIssuer(), "", mBean.getDecimals(), WalletUtil.XRP_COIN, 2);
                assbean.setTotal(mBean.getAmount());
                assbean.setWalletAddress("");
                addList.add(assbean);
                walletDBUtil.addAssets(assbean);
                AssertBean assbean2 = new AssertBean(getHttpUrl(mBean.getLogo()), mBean.getCurrency(), mBean.getCurrency(), mBean.getIssuer(), "", mBean.getDecimals(), WalletUtil.XRP_COIN, 2);
                assbean2.setTotal(mBean.getAmount());
                assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                walletDBUtil.addAssets(assbean2);
                ((BaseActivity) context).showLoading();
                notifyDataSetChanged();
            } else {
                walletDBUtil.delAssetsWallet(mBean.getCurrency(),"", WalletUtil.XRP_COIN);
                walletDBUtil.delAssetsWallet(SettingPrefUtil.getWalletAddress(context), mBean.getCurrency(), mBean.getIssuer());
                for (int i = 0; i < addList.size(); i++) {
                    String address = addList.get(i).getContract().toLowerCase();
                    String shortName = addList.get(i).getShort_name().toLowerCase();
                    if (address.equals(mBean.getIssuer().toLowerCase()) && shortName.equals(mBean.getCurrency().toLowerCase())) {
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

    public static String getHttpUrl(String url){
        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            url = SpUtil.getHostApi() + url;
        }
        return url;
    }

    
    private boolean isadd(XrpAssertBean mBean) {
        boolean isadd = false;
        for (int i = 0; i < addList.size(); i++) {
            String address = addList.get(i).getContract().toLowerCase();
            String shortName = addList.get(i).getShort_name().toLowerCase();
            if (address.equals(mBean.getIssuer().toLowerCase()) && shortName.equals(mBean.getCurrency().toLowerCase())) {
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

