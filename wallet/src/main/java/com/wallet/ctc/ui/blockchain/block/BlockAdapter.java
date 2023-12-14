

package com.wallet.ctc.ui.blockchain.block;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class BlockAdapter extends BaseAdapter {
    private List<BlockDetailBean> list = new ArrayList<BlockDetailBean>();
    private Context context;

    public BlockAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<BlockDetailBean> list) {
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
                    R.layout.item_block_detail, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BlockDetailBean blockDetailBean=list.get(position);
        holder.tokenName.setText(blockDetailBean.getToken());
        holder.key.setText(blockDetailBean.getKey());
        holder.value.setText(blockDetailBean.getWriteValue());
        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.token_name)
        TextView tokenName;
        @BindView(R2.id.key)
        TextView key;
        @BindView(R2.id.value)
        TextView value;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

