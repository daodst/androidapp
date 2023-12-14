

package com.wallet.ctc.ui.blockchain.income;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.IncomeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class IncomeAdapter extends BaseAdapter {
    private List<IncomeBean> list = new ArrayList<IncomeBean>();
    private Context context;

    public IncomeAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<IncomeBean> list) {
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
                    R.layout.item_shouyi, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        IncomeBean mEntity = list.get(position);
        holder.shouyi.setText(mEntity.getDraw_earnings());
        holder.shouyiTime.setText(mEntity.getDraw_time());
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.shouyi)
        TextView shouyi;
        @BindView(R2.id.shouyi_time)
        TextView shouyiTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
