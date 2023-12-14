

package com.wallet.ctc.ui.me.setting;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;

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
import com.wallet.ctc.db.WalletEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SettingDefWalletAdapter extends BaseAdapter {
    private List<WalletEntity> list = new ArrayList<WalletEntity>();
    private Context context;
    private String address="";
    private int types;

    public SettingDefWalletAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<WalletEntity> list,String address,int type) {
        this.list = list;
        this.address=address.toLowerCase();
        this.types=type;
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
                    R.layout.item_settingdef_wallet, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WalletEntity mEntity = list.get(position);
        holder.walletName.setText(mEntity.getName());
        int type = mEntity.getType();
        if (type == DM_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_dm);
        } else if (type == ETH_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_eth);
        } else if (type == BTC_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_btc);
        } else if (type == EOS_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_eos);
        } else if (type == MCC_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_mcc);
        }else if (type == OTHER_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.llq_other);
        }else if (type == XRP_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.xrp_logo);
        }else if (type == TRX_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.trx_logo);
        }else if (type == ETF_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.etf_logo);
        }else if (type == DMF_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.hb_dmf_logo);
        }else if (type == DMF_BA_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.bian_dmf_logo);
        }else if (type == HT_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.huobi_logo);
        }else if (type == BNB_COIN) {
            holder.walletTypeLogo.setImageResource(R.mipmap.bnb_logo);
        }
        if(address.equals(mEntity.getAllAddress().toLowerCase())&&types==mEntity.getType()){
            holder.checkImg.setImageResource(R.mipmap.setting_wallet_def_choosed);
        }else {
            holder.checkImg.setImageResource(R.mipmap.setting_wallet_def_choose);
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
        ImageView walletTypeLogo;
        @BindView(R2.id.wallet_name)
        TextView walletName;
        @BindView(R2.id.check_img)
        ImageView checkImg;
        @BindView(R2.id.llq_lin)
        LinearLayout llqLin;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

