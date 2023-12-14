

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.TransactionXrpRecordBean;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionXrpRecordAdapter extends BaseAdapter {
    private List<TransactionXrpRecordBean> list = new ArrayList<TransactionXrpRecordBean>();
    private String myWalletAddress = "";
    private Context context;
    String type = "";

    public TransactionXrpRecordAdapter(Context context) {
        this.context = context;
    }

    public TransactionXrpRecordAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public void bindData(List<TransactionXrpRecordBean> list) {
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
                    R.layout.item_xrptransaction_record, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        TransactionXrpRecordBean mBean = list.get(position);
        holder.timeTxt.setVisibility(View.GONE);
        if (position == 0) {
            holder.timeTxt.setText(mBean.getDay());
            holder.timeTxt.setVisibility(View.VISIBLE);
        } else {
            if (!mBean.getDay().equals(list.get(position - 1).getDay())) {
                holder.timeTxt.setText(mBean.getDay());
                holder.timeTxt.setVisibility(View.VISIBLE);
            }
        }
        if (mBean.getTo_account().toLowerCase().equals(myWalletAddress.toLowerCase())) {
            holder.walletName.setText(mBean.getFrom_account());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + mBean.getAmount() + type);
            holder.ether.setTextColor(0xff5289E5);
        } else {
            holder.walletName.setText(mBean.getTo_account());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + mBean.getAmount() + type);
            holder.ether.setTextColor(0xffFF0000);
        }

        if (!TextUtils.isEmpty(mBean.getFee())) {
            holder.kuanggong.setVisibility(View.VISIBLE);
            holder.kuanggong.setText(context.getString(R.string.xrp_transfee_title)+" "+mBean.getFee()+"XRP");
        } else {
            holder.kuanggong.setVisibility(View.GONE);
        }

        
        holder.orderState.setVisibility(View.GONE);

        String timeStr = AllUtils.getTimeByStrText(mBean.getTx_time()+"");
        if (TextUtils.isEmpty(timeStr)) {
            holder.noticeJieshou.setText("- -");
        } else {
            holder.noticeJieshou.setText(timeStr);
        }


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
