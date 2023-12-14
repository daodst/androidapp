

package com.wallet.ctc.ui.blockchain.managewallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.WalletLogoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class WalletTypeAdapter extends BaseAdapter {
    private List<WalletLogoBean> list = new ArrayList<WalletLogoBean>();
    private Context context;

    public WalletTypeAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletLogoBean> list) {
        this.list = list;
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
                    R.layout.item_add_wallet_type, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletLogoBean mEntity = list.get(position);
        holder.walletTypeLogo.setImageResource(mEntity.getLogo());
        holder.llqName.setText(mEntity.getWalletName());
        holder.choosedImg.setImageResource(R.mipmap.choose_import_wallet);
        return convertView;
    }


    class ViewHolder {
        @BindView(R2.id.wallet_type_logo)
        ImageView walletTypeLogo;
        @BindView(R2.id.llq_name)
        TextView llqName;
        @BindView(R2.id.choosed_img)
        ImageView choosedImg;
        @BindView(R2.id.llq_lin)
        LinearLayout llqLin;
        @BindView(R2.id.llq_mcc_view)
        View llqMccView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

