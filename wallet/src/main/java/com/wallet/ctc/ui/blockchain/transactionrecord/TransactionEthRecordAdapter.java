

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.NewWalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.TransactionNewEthRecordBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.ui.blockchain.transfer.ChangeTransferEthGasPriceActivity;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TransactionEthRecordAdapter extends BaseAdapter {
    private List<TransactionNewEthRecordBean> list = new ArrayList<TransactionNewEthRecordBean>();
    private String myWalletAddress = "";
    private Context context;
    String type="";

    public TransactionEthRecordAdapter(Context context) {
        this.context = context;
    }
    public TransactionEthRecordAdapter(Context context,String type) {
        this.context = context;
        this.type=type;
    }

    public void bindData(List<TransactionNewEthRecordBean> list) {
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
        TransactionNewEthRecordBean mBean = list.get(position);
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
        if (mBean.getToAllAddress().toLowerCase().equals(myWalletAddress.toLowerCase())) {
            holder.walletName.setText(mBean.getFrom());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanru);
            holder.ether.setText("+" + mBean.getValueDecimal() + type);
            holder.ether.setTextColor(0xff5289E5);
        } else {
            holder.walletName.setText(mBean.getTo());
            holder.assetsLogo.setImageResource(R.mipmap.zhuanchu);
            holder.ether.setText("-" + mBean.getValueDecimal() + type);
            holder.ether.setTextColor(0xffFF0000);
        }
        holder.orderState.setVisibility(View.GONE);
        holder.pendLin.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mBean.Status)) {
            holder.assetsLogo.setImageResource(R.mipmap.chuangjian_token);
            holder.ether.setTextColor(0xff7C69C0);
            holder.orderState.setText("pending");
            holder.orderState.setVisibility(View.VISIBLE);
            holder.pendLin.setVisibility(View.VISIBLE);
        }if ("0".equals(mBean.Status)) {
            holder.orderState.setText("fail");
            holder.orderState.setVisibility(View.VISIBLE);
        }
        holder.noticeJieshou.setText(AllUtils.getTimeFormatText(mBean.getTimestamp()+""));

        holder.chehui.setOnClickListener(v -> {
            goChange(mBean,"0",0);
        });
        holder.jiasu.setOnClickListener(v -> {
            BigDecimal amount=new BigDecimal(mBean.getValueInt()).divide(new BigDecimal(Math.pow(10, mBean.getTokenDecimal())));
            goChange(mBean,amount.toPlainString(),1);
        });

        return convertView;
    }

    private void goChange(TransactionNewEthRecordBean mBean,String amount,int from){
        String fee= new NewWalletTransctionUtil(context).changeEthGasPrice(new BigDecimal(mBean.getGas()),new BigDecimal(mBean.getGasPrice()));
        TransferBean data = new TransferBean(mBean.getToAllAddress(), mBean.getFromAllAddress(),amount,fee, WalletUtil.ETH_COIN, type, fee);
        data.setRemark(mBean.getRemarks());
        data.setDecimal(mBean.getTokenDecimal());
        data.setGascount(new BigDecimal(mBean.getGas()).intValue());
        data.setGasprice(mBean.getGasPrice());
        data.setNonce(mBean.getNonce());
        Intent intent = new Intent(context, ChangeTransferEthGasPriceActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("from", from);
        ((Activity)context).startActivity(intent);
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
        @BindView(R2.id.jiasu)
        TextView jiasu;
        @BindView(R2.id.chehui)
        TextView chehui;
        @BindView(R2.id.pend_lin)
        View pendLin;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
