

package com.wallet.ctc.ui.blockchain.setnode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.SettingNodeEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SettingNodeAdapter extends BaseAdapter {
    private List<SettingNodeEntity> list = new ArrayList<SettingNodeEntity>();
    private Context context;

    public SettingNodeAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<SettingNodeEntity> list) {
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
                    R.layout.item_settingnode, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SettingNodeEntity mBean=list.get(position);
        holder.nodeName.setText(mBean.getNodeName());
        holder.nodeXieyi.setText(mBean.getNodeUrl());

        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.node_name)
        TextView nodeName;
        @BindView(R2.id.node_xieyi)
        TextView nodeXieyi;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

