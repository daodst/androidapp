

package com.wallet.ctc.ui.blockchain.seach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.NewAssertBean;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;



public class SeachAdapter extends BaseAdapter {
    private List<NewAssertBean> list = new ArrayList<NewAssertBean>();
    private List<AssertBean> addList = new ArrayList<>();
    private List<AssertBean> mustList = new ArrayList<>();
    private WalletDBUtil walletDBUtil;
    private Context context;
    private int type;

    public SeachAdapter(Context context) {
        this.context = context;
        walletDBUtil=WalletDBUtil.getInstent(context);
    }

    public void bindData(List<NewAssertBean> list, int type) {
        this.list = list;
        this.type = type;
        if(type== WalletUtil.DM_COIN){
            mustList= SettingPrefUtil.getMustAssets(context,"DM");
        }else if(type== WalletUtil.MCC_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,context.getString(R.string.default_token_name).toUpperCase());
        }else if(type== WalletUtil.OTHER_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,context.getString(R.string.default_other_token_name).toUpperCase());
        }else if(type== WalletUtil.XRP_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,"XRP");
        }else if(type== WalletUtil.TRX_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,"TRX");
        }else if(type == WalletUtil.HT_COIN) {
            mustList=SettingPrefUtil.getMustAssets(context, "HT");
        }else if(type == WalletUtil.BNB_COIN) {
            mustList=SettingPrefUtil.getMustAssets(context, "BNB");
        }else if (type == WalletUtil.ETF_COIN) {
            mustList=SettingPrefUtil.getMustAssets(context, context.getString(R.string.default_etf).toUpperCase());
        }else if (type == WalletUtil.DMF_COIN) {
            mustList=SettingPrefUtil.getMustAssets(context, context.getString(R.string.default_dmf_hb).toUpperCase());
        }else if (type == WalletUtil.DMF_BA_COIN) {
            mustList = SettingPrefUtil.getMustAssets(context, context.getString(R.string.default_dmf_ba).toUpperCase());
        }
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
        NewAssertBean mBean = list.get(position);
        if (type == WalletUtil.DM_COIN) {
            GlideUtil.showDmImg(context, mBean.getLogo(), holder.assetsLogo);
        } else if (type == WalletUtil.MCC_COIN) {
            GlideUtil.showMccImg(context, mBean.getLogo(), holder.assetsLogo);
        }else if (type == WalletUtil.OTHER_COIN) {
            GlideUtil.showOtherImg(context, mBean.getLogo(), holder.assetsLogo);
        }
        if (type == WalletUtil.XRP_COIN) {
            
            holder.assetsName.setText(mBean.getCurrency().toUpperCase());
            holder.assetsConten.setText(mBean.getIssuer());
        }else {
            holder.assetsName.setText(mBean.getCc().toUpperCase());
            holder.assetsConten.setText(mBean.getName());
        }
        holder.assetsChoose.setVisibility(View.VISIBLE);
        if (type == WalletUtil.DM_COIN) {
            if(mBean.getCc().toUpperCase().equals("DM")) {
                holder.assetsChoose.setVisibility(View.GONE);
            }
            for(int i=0;i<mustList.size();i++){
                if(mBean.getCc().toUpperCase().equals(mustList.get(i).getShort_name().toUpperCase())){
                    holder.assetsChoose.setVisibility(View.GONE);
                    break;
                }
            }
        }else if(type == WalletUtil.MCC_COIN){
            if(mBean.getCc().toUpperCase().equals(context.getString(R.string.default_token_name).toUpperCase())){
                holder.assetsChoose.setVisibility(View.GONE);
            }
            for(int i=0;i<mustList.size();i++){
                if(mBean.getCc().toUpperCase().equals(mustList.get(i).getShort_name().toUpperCase())){
                    holder.assetsChoose.setVisibility(View.GONE);
                    break;
                }
            }
        }else if(type == WalletUtil.OTHER_COIN){
            if(mBean.getCc().toUpperCase().equals(context.getString(R.string.default_other_token_name).toUpperCase())){
                holder.assetsChoose.setVisibility(View.GONE);
            }
            for(int i=0;i<mustList.size();i++){
                if(mBean.getCc().toUpperCase().equals(mustList.get(i).getShort_name().toUpperCase())){
                    holder.assetsChoose.setVisibility(View.GONE);
                    break;
                }
            }
        }else if (type == WalletUtil.XRP_COIN) {
            
            if (mBean.getCurrency().toUpperCase().equals("XRP")) {
                holder.assetsChoose.setVisibility(View.GONE);
            } else {
                holder.assetsChoose.setVisibility(View.VISIBLE);
            }
        }
        if (isadd(mBean)) {
            holder.assetsChoose.setChecked(true);
        }else{
            holder.assetsChoose.setChecked(false);
        }
        holder.assetsChoose.setOnClickListener(v->{
            if(((CheckBox)v).isChecked()){
                AssertBean assbean = new AssertBean(mBean.getLogo(), mBean.getCc(), mBean.getName(), "", "", mBean.getDecimal(), type, 2);
                assbean.setTotal(mBean.getTotal());
                assbean.setAward(mBean.getAward());
                assbean.setUrl(mBean.getUrl());
                assbean.setMineral(mBean.getMineral());
                assbean.setDesc(mBean.getDesc());
                assbean.setCreator(mBean.getCreator());
                assbean.setWalletAddress("");
                walletDBUtil.addAssets(assbean);
                addList.add(assbean);
                AssertBean assbean2 = new AssertBean(mBean.getLogo(), mBean.getCc(), mBean.getName(), "", "", mBean.getDecimal(), type, 2);
                assbean2.setTotal(mBean.getTotal());
                assbean2.setAward(mBean.getAward());
                assbean2.setUrl(mBean.getUrl());
                assbean2.setMineral(mBean.getMineral());
                assbean2.setDesc(mBean.getDesc());
                assbean2.setCreator(mBean.getCreator());
                assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                walletDBUtil.addAssets(assbean2);
                notifyDataSetChanged();
            } else {
                walletDBUtil.delAssetsWallet(mBean.getCc(),mBean.getCurrency(),type);
                walletDBUtil.delAssetsWallet(SettingPrefUtil.getWalletAddress(context), mBean.getCc());
                for (int i = 0; i < addList.size(); i++) {
                    String shortName = addList.get(i).getShort_name().toLowerCase();
                    if (shortName.equals(mBean.getCc().toLowerCase())) {
                        addList.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    
    private boolean isadd(NewAssertBean mBean) {
        boolean isadd = false;
        LogUtil.d("isadd");
        for (int i = 0; i < addList.size(); i++) {
            if (addList.get(i).getShort_name().equals(mBean.getCc())) {
                isadd = true;
                break;
            }
        }
        return isadd;
    }

    class ViewHolder {
        @BindView(R2.id.assets_logo)
        CircularImage assetsLogo;
        @BindView(R2.id.assets_name)
        TextView assetsName;
        @BindView(R2.id.assets_conten)
        TextView assetsConten;
        @BindView(R2.id.assets_choose)
        CheckBox assetsChoose;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

