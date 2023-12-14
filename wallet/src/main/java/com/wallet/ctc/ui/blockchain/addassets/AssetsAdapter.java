

package com.wallet.ctc.ui.blockchain.addassets;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;
import common.app.my.view.MyAlertDialog;



public class AssetsAdapter extends BaseAdapter {
    private List<AssertBean> list = new ArrayList<AssertBean>();
    private List<AssertBean> choose = new ArrayList<AssertBean>();
    private WalletDBUtil walletDBUtil;
    private Context context;
    private ColorMatrixColorFilter filter;
    MyAlertDialog myAlertDialog;

    public AssetsAdapter(Context context) {
        this.context = context;
        walletDBUtil=WalletDBUtil.getInstent(context);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        filter = new ColorMatrixColorFilter(matrix);
        myAlertDialog=new MyAlertDialog(context,context.getString(R.string.del_assets));
    }

    public void bindData(List<AssertBean> list,List<AssertBean> choose) {
        this.list = list;
        this.choose=choose;
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
                    R.layout.item_assets, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AssertBean mBean=list.get(position);
        holder.assetsLogo.clearColorFilter();
        if(mBean.getImg_path().length()>2){
            GlideUtil.showImg(context, mBean.getImg_path(), holder.assetsLogo);
        }else if(mBean.getLogo()!=0){
            GlideUtil.showImg(context, mBean.getLogo(), holder.assetsLogo);
        }else {
            holder.assetsLogo.setColorFilter(filter);
            GlideUtil.showImg(context, walletDBUtil.getMustWallet(mBean.getType()).get(0).getLogo(), holder.assetsLogo);
        }
        holder.assetsChoose.setVisibility(View.VISIBLE);
        if(mBean.getLevel()==0){
            holder.assetsChoose.setVisibility(View.GONE);
        }
        if(null!=mBean.getContract()&&mBean.getContract().length()>16){
            holder.assetsAddress.setText(mBean.getShortAddress());
            holder.assetsAddress.setVisibility(View.VISIBLE);
        }else {
            holder.assetsAddress.setVisibility(View.GONE);
        }
        holder.assetsName.setText(mBean.getShort_name().toUpperCase());
        holder.assetsConten.setText(mBean.getFull_name());
        holder.assetsChoose.setChecked(false);
        holder.position=position;
        for(int i=0;i<choose.size();i++){
            if(choose.get(i).getShort_name().equals(mBean.getShort_name())) {
                if(!TextUtils.isEmpty(mBean.getContract())&&choose.get(i).getContract().equalsIgnoreCase(mBean.getContract())){
                    holder.assetsChoose.setChecked(true);
                }else if(TextUtils.isEmpty(mBean.getContract())){
                    holder.assetsChoose.setChecked(true);
                }
                break;
            }
        }
        convertView.setOnLongClickListener(v -> {
            if(mBean.getLevel()!=0){
                myAlertDialog.show();
            }
            return false;
        });

        
        convertView.setOnClickListener(view -> {
            String contract = mBean.getContract();
            if (!TextUtils.isEmpty(contract)) {
                AllUtils.copyText(contract);
            }
        });

        return convertView;
    }


    class ViewHolder {
        @BindView(R2.id.assets_logo)
        CircularImage assetsLogo;
        @BindView(R2.id.assets_name)
        TextView assetsName;
        @BindView(R2.id.assets_conten)
        TextView assetsConten;
        @BindView(R2.id.assets_address)
        TextView assetsAddress;
        @BindView(R2.id.assets_choose)
        CheckBox assetsChoose;
        int position;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            assetsChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AssertBean bean=list.get(position);
                    if(assetsChoose.isChecked()){
                        choose.add(bean);
                        bean.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                        walletDBUtil.addAssets(bean);
                    }else {
                        for(int i=0;i<choose.size();i++){
                            if(choose.get(i).getShort_name().equals(list.get(position).getShort_name())) {
                                choose.remove(i);
                                break;
                            }
                        }
                        walletDBUtil.delAssetsWallet(SettingPrefUtil.getWalletAddress(context),bean.getShort_name());
                    }

                }
            });
            myAlertDialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    walletDBUtil.delAssets(list.get(position));
                    list.remove(position);
                    notifyDataSetChanged();
                    myAlertDialog.dismiss();
                }

                @Override
                public void No() {
                    myAlertDialog.dismiss();
                }
            });
        }
    }
}

