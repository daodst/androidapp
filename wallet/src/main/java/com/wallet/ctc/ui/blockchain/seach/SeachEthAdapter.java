

package com.wallet.ctc.ui.blockchain.seach;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
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
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SeachEthAdapter extends BaseAdapter {
    private List<EthAssertBean> list = new ArrayList<EthAssertBean>();
    private List<AssertBean> addList = new ArrayList<>();
    private List<AssertBean> mustList = new ArrayList<>();
    private Context context;
    private WalletDBUtil walletDBUtil;
    private int type;
    private ColorMatrixColorFilter filter;
    public SeachEthAdapter(Context context) {
        this.context = context;
        walletDBUtil=WalletDBUtil.getInstent(context);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        filter = new ColorMatrixColorFilter(matrix);
    }

    public void bindData(List<EthAssertBean> list,int type) {
        this.list = list;
        this.type = type;
        if(type== WalletUtil.ETH_COIN){
            mustList= SettingPrefUtil.getMustAssets(context,"ETH");
        }else if(type== WalletUtil.ETF_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,context.getString(R.string.default_etf).toUpperCase());
        }else if(type== WalletUtil.DMF_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,context.getString(R.string.default_dmf_hb).toUpperCase());
        }else if(type== WalletUtil.DMF_BA_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,context.getString(R.string.default_dmf_ba).toUpperCase());
        }else if(type== WalletUtil.HT_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,"HT");
        }else if(type== WalletUtil.BNB_COIN){
            mustList=SettingPrefUtil.getMustAssets(context,"BNB");
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
        EthAssertBean mBean = list.get(position);
        holder.assetsLogo.clearColorFilter();
        
        if (mBean.getLogo().length() < 2) {
            GlideUtil.showImg(context, walletDBUtil.getMustWallet(type).get(0).getLogo(), holder.assetsLogo);
            holder.assetsLogo.setColorFilter(filter);
        } else {
            if (mBean.getLogo().startsWith("res://")) {
                Uri uri = Uri.parse(mBean.getLogo());
                String host = uri.getHost(); 
                String path = uri.getPath(); 
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                int logo = context.getResources().getIdentifier(path, host, context.getPackageName());
                GlideUtil.showImg(context, logo, holder.assetsLogo);
            }else {
                GlideUtil.showImg(context, mBean.getLogo(), holder.assetsLogo);
            }

        }
        holder.assetsName.setText(mBean.getSymbol());
        if (TextUtils.isEmpty(mBean.getAddress())&&mBean.getSymbol().equalsIgnoreCase(walletDBUtil.getMustWallet(type).get(0).getShort_name())) {
            holder.assetsChoose.setVisibility(View.GONE);
        }else {
            holder.assetsChoose.setVisibility(View.VISIBLE);
            for(int i=0;i<mustList.size();i++){
                if(mBean.getSymbol().toUpperCase().equals(mustList.get(i).getShort_name().toUpperCase())&&mBean.getAddress().toUpperCase().equals(mustList.get(i).getContract().toUpperCase())){
                    holder.assetsChoose.setVisibility(View.GONE);
                    break;
                }
            }

        }
        holder.assetsConten.setText(mBean.getName());
        holder.assetsAddress.setText(mBean.getShortAddress());
        holder.assetsAddress.setVisibility(View.VISIBLE);
        if (isadd(mBean)) {
            holder.assetsChoose.setChecked(true);
        } else {
            holder.assetsChoose.setChecked(false);
        }
        holder.assetsChoose.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked()) {
                AssertBean assbean = new AssertBean(mBean.getLogo(), mBean.getSymbol(), mBean.getName(), mBean.getAddress(), "", mBean.getDecimals(), type, 2);
                assbean.setWalletAddress("");
                addList.add(assbean);
                walletDBUtil.addAssets(assbean);
                AssertBean assbean2 = new AssertBean(mBean.getLogo(), mBean.getSymbol(), mBean.getName(), mBean.getAddress(), "", mBean.getDecimals(), type, 2);
                assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                walletDBUtil.addAssets(assbean2);
                notifyDataSetChanged();
            } else {
                walletDBUtil.delAssetsWallet(mBean.getSymbol(),mBean.getAddress(),type);
                walletDBUtil.delAssetsWallet(SettingPrefUtil.getWalletAddress(context), mBean.getSymbol(), mBean.getAddress());
                for (int i = 0; i < addList.size(); i++) {
                    String address = addList.get(i).getContract().toLowerCase();
                    String shortName = addList.get(i).getShort_name().toLowerCase();
                    if (address.equals(mBean.getAddress().toLowerCase()) && shortName.equals(mBean.getSymbol().toLowerCase())) {
                        addList.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        });

        
        convertView.setOnClickListener(view -> {
            String contract = mBean.getAddress();
            if (!TextUtils.isEmpty(contract)) {
                AllUtils.copyText(contract);
            }
        });

        return convertView;
    }

    
    private boolean isadd(EthAssertBean mBean) {
        boolean isadd = false;
        for (int i = 0; i < addList.size(); i++) {
            String address = addList.get(i).getContract();
            if (!TextUtils.isEmpty(address)&&address.equalsIgnoreCase(mBean.getAddress())) {
                mBean.setSymbol(addList.get(i).getShort_name());
                mBean.setAddress(address);
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

