

package com.wallet.ctc.ui.blockchain.addquotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class AddQuotesAdapter extends BaseAdapter {
    private List<String> list = new ArrayList<String>();
    private Context context;

    public AddQuotesAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<String> list) {
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
                    R.layout.item_add_quotes, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.assetsType.setVisibility(View.GONE);
        if(position==0||position==5||position==9){
            holder.assetsType.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.assets_type)
        TextView assetsType;
        @BindView(R2.id.assets_name)
        TextView assetsName;
        @BindView(R2.id.assets_choose)
        CheckBox assetsChoose;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

