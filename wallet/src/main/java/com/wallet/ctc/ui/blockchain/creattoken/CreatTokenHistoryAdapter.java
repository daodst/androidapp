

package com.wallet.ctc.ui.blockchain.creattoken;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.CreatEthEntity;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class CreatTokenHistoryAdapter extends BaseAdapter {
    private List<CreatEthEntity> list = new ArrayList<CreatEthEntity>();
    private Context context;

    public CreatTokenHistoryAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<CreatEthEntity> list) {
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
                    R.layout.item_create_tken_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CreatEthEntity mEntity = list.get(position);
        holder.coinName.setText(mEntity.getShort_name().toUpperCase());
        holder.coinNum.setText(mEntity.getAssestNum());
        holder.coinTime.setText(AllUtils.getTimeYRSF(mEntity.getCreatTime()+""));
        if(mEntity.getType()== WalletUtil.DM_COIN){
            holder.mainCoin.setText("DM");
        }else if(mEntity.getType()== WalletUtil.MCC_COIN){
            holder.mainCoin.setText(context.getString(R.string.default_token_name).toUpperCase());
        }else if(mEntity.getType()== WalletUtil.OTHER_COIN){
            holder.mainCoin.setText(context.getString(R.string.default_other_token_name).toUpperCase());
        }else if(mEntity.getType()== WalletUtil.ETH_COIN){
            holder.mainCoin.setText("ETH");
        }else if(mEntity.getType()== WalletUtil.ETF_COIN){
            holder.mainCoin.setText(context.getString(R.string.default_etf).toUpperCase());
        }else if(mEntity.getType()== WalletUtil.DMF_COIN){
            holder.mainCoin.setText(context.getString(R.string.default_dmf_hb).toUpperCase());
        }else if(mEntity.getType()== WalletUtil.DMF_BA_COIN){
            holder.mainCoin.setText(context.getString(R.string.default_dmf_ba).toUpperCase());
        }else if(mEntity.getType()== WalletUtil.HT_COIN){
            holder.mainCoin.setText("HT");
        }else if(mEntity.getType()== WalletUtil.BNB_COIN){
            holder.mainCoin.setText("BNB");
        }
        if(mEntity.getStatu()==1){
            holder.coinStatu.setText(context.getString(R.string.success));
            holder.coinStatu.setBackgroundResource(R.drawable.lin_green_creattoken_statu_bg);
        }else if(mEntity.getStatu()==0){
            holder.coinStatu.setText(context.getString(R.string.processing));
            holder.coinStatu.setBackgroundResource(R.drawable.lin_red_creattoken_statu_bg);
        }else {
            holder.coinStatu.setText(context.getString(R.string.fail));
            holder.coinStatu.setBackgroundResource(R.drawable.lin_red_creattoken_statu_bg);
        }


        return convertView;
    }


    class ViewHolder {
        @BindView(R2.id.coin_name)
        TextView coinName;
        @BindView(R2.id.coin_num)
        TextView coinNum;
        @BindView(R2.id.coin_time)
        TextView coinTime;
        @BindView(R2.id.coin_statu)
        TextView coinStatu;
        @BindView(R2.id.main_coin)
        TextView mainCoin;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

