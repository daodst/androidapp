

package com.wallet.ctc.ui.blockchain.managewallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;



public class NewManageWalletListAdapter extends BaseAdapter {
    private List<WalletEntity> list = new ArrayList<WalletEntity>();
    private Context context;

    public NewManageWalletListAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletEntity> list) {
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
                    R.layout.item_newmanage_wallet2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletEntity mEntity = list.get(position);
        holder.walletName.setText(mEntity.getName());
        int type = mEntity.getType();
        if (type == WalletUtil.DM_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_dm);
        } else if (type == WalletUtil.ETH_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_eth);
        } else if (type == WalletUtil.BTC_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_btc);
        } else if (type == WalletUtil.EOS_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_eos);
        } else if (type == WalletUtil.MCC_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_mcc);
        }else if (type == WalletUtil.OTHER_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_other);
        }else if (type == WalletUtil.XRP_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.xrp_logo);
        }else if (type == WalletUtil.TRX_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.trx_logo);
        }else if (type == WalletUtil.ETF_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.etf_logo);
        }else if (type == WalletUtil.DMF_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.hb_dmf_logo);
        }else if (type == WalletUtil.DMF_BA_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.bian_dmf_logo);
        }else if (type == WalletUtil.HT_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.huobi_logo);
        }else if (type == WalletUtil.BNB_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.bnb_logo);
        }

        return convertView;
    }

    ChangeDef changeDef;

    public void changeDef(ChangeDef changeDef) {
        this.changeDef = changeDef;
    }

    public interface ChangeDef {
        void change(String address, int postion);
    }



    class ViewHolder {
        @BindView(R2.id.wallet_type_logo)
        CircularImage walletTypeLogo;
        @BindView(R2.id.wallet_name)
        TextView walletName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

