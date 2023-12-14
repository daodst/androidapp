

package com.wallet.ctc.view.popwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.pojo.CurrencyBean;



public class CurrencyUnitAdapter extends BaseAdapter {

    private List<CurrencyBean> mList;
    private Context mContext;


    public CurrencyUnitAdapter(Context context, List<CurrencyBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.currencyunit_item, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(mList.get(position).getCurrency());
        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.title)
        TextView title;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
