

package com.wallet.ctc.ui.blockchain.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.MarketPriceBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.pojo.CurrencyBean;



public class TrustQuotesListAdapter extends BaseAdapter {
    private List<MarketPriceBean.TickersBean> list = new ArrayList<MarketPriceBean.TickersBean>();
    private Context context;
    private String type= BuildConfig.CURRENCY_UNIT;
    private String dcu=BuildConfig.CURRENCY_SYMBOL;

    public TrustQuotesListAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<MarketPriceBean.TickersBean> list) {
        this.list = list;
    }
    public void bindType(String list) {
        this.type = list;
        this.dcu=type;
        if (dcu.equals(common.app.BuildConfig.CURRENCY_UNIT)) {
            dcu = BuildConfig.CURRENCY_SYMBOL;
        } else {
            Gson gson = new Gson();
            CurrencyBean bean = gson.fromJson(dcu, CurrencyBean.class);
            if (null != bean) {
                dcu = bean.getCurrency_symbol();
            }
        }
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
                    R.layout.item_trust_quotes, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MarketPriceBean.TickersBean mBean=list.get(position);
        holder.moneyName.setText(mBean.getCoin());
        holder.moneyTypename.setText("/"+type);
        holder.moneyType.setText(mBean.getProvider());

        holder.price.setText(dcu + "  "+new BigDecimal(mBean.getPrice()).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString());
        holder.quotesChange.setText(new BigDecimal(mBean.getChange_24h()).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString()+"%");
        if(mBean.getChange_24h().startsWith("-")){
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

