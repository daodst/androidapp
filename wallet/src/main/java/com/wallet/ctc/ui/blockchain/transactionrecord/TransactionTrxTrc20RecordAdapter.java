

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.TrxTrc20TransferHistoryBean;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionTrxTrc20RecordAdapter extends BaseAdapter {
    private List<TrxTrc20TransferHistoryBean> list = new ArrayList<TrxTrc20TransferHistoryBean>();
    private String myWalletAddress = "";
    private Context context;
    String type="";
    BigDecimal decimal;

    public TransactionTrxTrc20RecordAdapter(Context context, String type, int dec) {
        this.context = context;
        this.type=type;
        decimal=new BigDecimal(Math.pow(10, dec));
    }

    public void bindData(List<TrxTrc20TransferHistoryBean> list) {
        this.list = list;
    }

    public void bindAddress(String myWalletAddress) {
        this.myWalletAddress = myWalletAddress;
    }

    @Override
    public int getCount() {
        if(null==list){
            return 0;
        }
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
                    R.layout.item_ethtransaction_record, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TrxTrc20TransferHistoryBean mBean = list.get(position);
        holder.timeTxt.setVisibility(View.GONE);
        if (position == 0) {
            holder.timeTxt.setText(AllUtils.getTimeJavaNYR(mBean.getBlock_timestamp()));
            holder.timeTxt.setVisibility(View.VISIBLE);
        } else {
            String day=AllUtils.getTimeJavaNYR(mBean.getBlock_timestamp());
            String yesDay=AllUtils.getTimeJavaNYR(list.get(position - 1).getBlock_timestamp());
            if (!day.equals(yesDay)) {
                holder.timeTxt.setText(day);
                holder.timeTxt.setVisibility(View.VISIBLE);
            }
        }
        BigDecimal amount=mBean.getValue().divide(decimal,2,BigDecimal.ROUND_HALF_UP);
        if (mBean.getTo().toLowerCase().equals(myWalletAddress.toLowerCase())) {
            holder.walletName.setText(mBean.getFrom());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + amount.toPlainString() + type);
            holder.ether.setTextColor(0xff5289E5);
        } else {
            holder.walletName.setText(mBean.getTo());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + amount.toPlainString()  + type);
            holder.ether.setTextColor(0xffFF0000);
        }
        holder.noticeJieshou.setText(AllUtils.getTimeFormatJavaText(mBean.getBlock_timestamp()));

        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.time_txt)
        TextView timeTxt;
        @BindView(R2.id.assets_logo)
        ImageView assetsLogo;
        @BindView(R2.id.wallet_name)
        TextView walletName;
        @BindView(R2.id.ether)
        TextView ether;
        @BindView(R2.id.kuanggong)
        TextView kuanggong;
        @BindView(R2.id.notice_jieshou)
        TextView noticeJieshou;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
