

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
import com.wallet.ctc.model.blockchain.SgbTransHistory;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionSgbRecordAdapter extends BaseAdapter {
    private List<SgbTransHistory> list = new ArrayList<SgbTransHistory>();
    private String myWalletAddress = "";
    private Context context;

    public TransactionSgbRecordAdapter(Context context) {
        this.context = context;
    }


    public void bindData(List<SgbTransHistory> list) {
        this.list = list;
    }

    public void bindAddress(String myWalletAddress) {
        this.myWalletAddress = myWalletAddress;
    }

    @Override
    public int getCount() {
        if (null == list) {
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
        SgbTransHistory mBean = list.get(position);
        holder.timeTxt.setVisibility(View.GONE);
        if (position == 0) {
            holder.timeTxt.setText(AllUtils.getTimeNYR(mBean.getBlock_timestamp()+""));
            holder.timeTxt.setVisibility(View.VISIBLE);
        } else {
            String day = AllUtils.getTimeNYR(mBean.getBlock_timestamp()+"" );
            String yesDay = AllUtils.getTimeNYR(list.get(position - 1).getBlock_timestamp()+"");
            if (!day.equals(yesDay)) {
                holder.timeTxt.setText(day);
                holder.timeTxt.setVisibility(View.VISIBLE);
            }
        }
        if (mBean.getSuccess()) {
            holder.orderState.setTextColor(0xff5EB10D);
        } else {
            holder.orderState.setTextColor(0xffFF0000);
        }
        if(mBean.getSuccess()){
            holder.orderState.setText("success");
        }else {
            holder.orderState.setText("fail");
        }
        if (mBean.getFrom().equalsIgnoreCase(myWalletAddress)) {
            holder.walletName.setText(mBean.getTo());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + mBean.getAmount() + "SGB");
            holder.ether.setTextColor(0xffFF0000);
        } else {
            holder.walletName.setText(mBean.getFrom());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + mBean.getAmount() + "SGB");
            holder.ether.setTextColor(0xff5289E5);
        }
        holder.noticeJieshou.setText(AllUtils.getTimeNYR(mBean.getBlock_timestamp()+""));

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
        @BindView(R2.id.order_state)
        TextView orderState;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
