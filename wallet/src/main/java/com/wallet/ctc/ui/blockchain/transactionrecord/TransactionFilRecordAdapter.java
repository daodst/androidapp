

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionFilRecordAdapter extends BaseAdapter {
    private List<FilTransRecordBean.DocsBean> list = new ArrayList<FilTransRecordBean.DocsBean>();
    private String myWalletAddress = "";
    private Context context;
    String type = "";
    private int dec;


    public TransactionFilRecordAdapter(Context context, String type, int dec) {
        this.context = context;
        this.type = type;
        this.dec = dec;

    }


    public void bindData(List<FilTransRecordBean.DocsBean> list) {
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
        FilTransRecordBean.DocsBean mBean = list.get(position);
        holder.timeTxt.setVisibility(View.GONE);
        if (position == 0) {
            holder.timeTxt.setText(AllUtils.getTimeJavaNYR(mBean.getDate() * 1000));
            holder.timeTxt.setVisibility(View.VISIBLE);
        } else {
            String day = AllUtils.getTimeJavaNYR(mBean.getDate() * 1000);
            String yesDay = AllUtils.getTimeJavaNYR(list.get(position - 1).getDate() * 1000);
            if (!day.equals(yesDay)) {
                holder.timeTxt.setText(day);
                holder.timeTxt.setVisibility(View.VISIBLE);
            }
        }
        if ("completed".equals(mBean.getStatus())) {
            holder.orderState.setTextColor(0xff5EB10D);
        } else {
            holder.orderState.setTextColor(0xffFF0000);
        }
        holder.orderState.setText(mBean.getStatus());

        int decimal = mBean.getMetadata().getDecimals();
        if (decimal == 0) {
            
            decimal = dec;
        }
        BigDecimal amount = new BigDecimal("0");

        try {
            amount = new BigDecimal(mBean.getMetadata().getValue()).divide(new BigDecimal(Math.pow(10, decimal)), 6, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String symbol = mBean.getMetadata().getSymbol();
        if (symbol == null) {
            symbol = "";
        } else {
            symbol = symbol.toUpperCase();
        }
        String showAmount = amount.stripTrailingZeros().toPlainString() + " " + symbol;

        if ("outgoing".equals(mBean.getDirection())) {
            if ("token_transfer".equals(mBean.getType()) && null != mBean.getMetadata() && !TextUtils.isEmpty(mBean.getMetadata().getTo())) {
                
                holder.walletName.setText(mBean.getMetadata().getTo());
            } else {
                holder.walletName.setText(mBean.getTo());
            }
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + showAmount);
            holder.ether.setTextColor(ContextCompat.getColor(context, R.color.default_tip_color));
        } else if ("incoming".equals(mBean.getDirection())) {
            holder.walletName.setText(mBean.getFrom());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + showAmount);
            holder.ether.setTextColor(ContextCompat.getColor(context, R.color.default_theme_color));
        } else if ("yourself".equals(mBean.getDirection())) {
            holder.walletName.setText(mBean.getFrom());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText(showAmount);
            holder.ether.setTextColor(ContextCompat.getColor(context, R.color.default_theme_color));
        }
        if (!mBean.getType().equals("transfer") && !mBean.getType().equals("token_transfer")) {
            holder.assetsLogo.setImageResource(R.mipmap.heyue);
        }

        holder.noticeJieshou.setText(AllUtils.getTimeFormatJavaText(mBean.getDate() * 1000));

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
