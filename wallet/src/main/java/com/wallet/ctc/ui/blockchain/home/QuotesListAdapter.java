

package com.wallet.ctc.ui.blockchain.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.NewQuotesBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class QuotesListAdapter extends BaseAdapter {
    private List<NewQuotesBean> list = new ArrayList<NewQuotesBean>();
    private Context context;
    private String type= BuildConfig.CURRENCY_SYMBOL;

    public QuotesListAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<NewQuotesBean> list) {
        this.list = list;
    }
    public void bindType(String list) {
        this.type = list;
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
                    R.layout.item_quotes, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NewQuotesBean mBean=list.get(position);
        holder.moneyName.setText(mBean.getName());
        holder.moneyTypename.setText("/"+mBean.getBname());
        holder.moneyType.setText(mBean.getExchange_name());
        holder.price.setText(mBean.getClose().toPlainString());
        if(type.equals(BuildConfig.CURRENCY_SYMBOL)) {
            holder.price2.setText(type + "  " + mBean.getCprice());
        }else {
            holder.price2.setText(type + "  " + mBean.getUprice());
        }
        holder.quotesChange.setText(mBean.getChg()+"%");
        if(mBean.getChg().startsWith("-")){
            holder.quotesChange.setBackgroundResource(R.drawable.lin_red_bg);
        }else {
            holder.quotesChange.setBackgroundResource(R.drawable.lin_green_bg);
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.money_type)
        TextView moneyType;
        @BindView(R2.id.money_name)
        TextView moneyName;
        @BindView(R2.id.price)
        TextView price;
        @BindView(R2.id.price2)
        TextView price2;
        @BindView(R2.id.quotes_change)
        TextView quotesChange;
        @BindView(R2.id.money_type_name)
        TextView moneyTypename;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

