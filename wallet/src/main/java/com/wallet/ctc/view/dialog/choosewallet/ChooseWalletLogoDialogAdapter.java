

package com.wallet.ctc.view.dialog.choosewallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.WalletLogoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.view.RoundImageView;



public class ChooseWalletLogoDialogAdapter extends BaseAdapter {
    private List<WalletLogoBean> list = new ArrayList<>();
    private Context context;

    public ChooseWalletLogoDialogAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletLogoBean> list) {
        this.list = list;
        notifyDataSetChanged();
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
                    R.layout.item_wallet_logo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletLogoBean mEntity = list.get(position);
        holder.walletLogo.setImageResource(mEntity.getLogo());
        if(mEntity.getChoose()==1){
            holder.walletLogoChoose.setVisibility(View.VISIBLE);
            holder.itemLayout.setBackgroundResource(R.drawable.left_bottom_white_c14);
        }else {
            holder.walletLogoChoose.setVisibility(View.GONE);
            holder.itemLayout.setBackgroundResource(R.drawable.tranlate_drawable);
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.wallet_logo)
        RoundImageView walletLogo;
        @BindView(R2.id.wallet_logo_choose)
        View walletLogoChoose;
        @BindView(R2.id.itemLayout)
        RelativeLayout itemLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

