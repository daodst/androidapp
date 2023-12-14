

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;



public class ChooseWalletListAdapter extends BaseAdapter {
    private List<WalletEntity> list = new ArrayList<WalletEntity>();
    private String address;
    private Context context;

    public ChooseWalletListAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletEntity> list,String address) {
        this.list = list;
        this.address=address;
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
                    R.layout.item_home_walletlist, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletEntity mEntity=list.get(position);
        holder.walletNameMenu.setText(mEntity.getName());
        holder.bodyBg.setBackgroundColor(0xffffffff);
        if(mEntity.getAllAddress().equals(address)){
            holder.bodyBg.setBackgroundColor(0xfff4f4f4);
        }
        GlideUtil.showImg(context, mEntity.getTrueLogo(), holder.walletLogo);
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.wallet_logo)
        CircularImage walletLogo;
        @BindView(R2.id.wallet_name_menu)
        TextView walletNameMenu;
        @BindView(R2.id.body_bg)
        LinearLayout bodyBg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

