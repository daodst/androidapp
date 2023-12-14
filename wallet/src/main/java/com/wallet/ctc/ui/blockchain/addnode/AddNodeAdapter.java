

package com.wallet.ctc.ui.blockchain.addnode;

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
import com.wallet.ctc.db.SettingNodeEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.utils.AllUtils;



public class AddNodeAdapter extends BaseAdapter {
    private List<SettingNodeEntity> list;
    private Context context;

    public AddNodeAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<SettingNodeEntity>();
    }

    public void bindData(List<SettingNodeEntity> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    
    public void updateTime(String url, long time) {
        if (null == list || list.size() == 0 || TextUtils.isEmpty(url)) {
            return;
        }
        for (int i=0; i<list.size(); i++) {
            SettingNodeEntity node = list.get(i);
            if (url.equals(node.getNodeUrl())) {
                node.setPingTime(time);
                break;
            }
        }
        notifyDataSetChanged();
    }

    
    public boolean hasAddThisNode(String url) {
        if (null == list || list.size() == 0 || TextUtils.isEmpty(url)) {
            return false;
        }
        String hostPort = AllUtils.urlDelDt(url);
        if (TextUtils.isEmpty(hostPort)) {
            return false;
        }
        for (int i=0; i<list.size(); i++) {
            SettingNodeEntity node = list.get(i);
            String nodeUrlPort = AllUtils.urlDelDt(node.getNodeUrl());
            if (hostPort.equals(nodeUrlPort)) {
                return true;
            }
        }
        return false;
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
                    R.layout.item_addnode, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SettingNodeEntity mBean = list.get(position);
        if(position==0){
            holder.addNodeTitle.setVisibility(View.VISIBLE);
        }else  if(mBean.getIsDef()!=list.get(position-1).getIsDef()){
            holder.addNodeTitle.setVisibility(View.VISIBLE);
        }else {
            holder.addNodeTitle.setVisibility(View.GONE);
        }
        if(mBean.getIsDef()==0){
            holder.addNodeTitle.setText(R.string.default_node_title);
        }else {
            holder.addNodeTitle.setText(context.getString(R.string.custom_node));
        }

        holder.nodeName.setText(mBean.getNodeUrl());
        if(mBean.isChoose()){
            holder.nodeXieyi.setVisibility(View.VISIBLE);
        }else {
            holder.nodeXieyi.setVisibility(View.INVISIBLE);
        }

        long pingTime = mBean.getPingTime();

        if (pingTime > 0 && pingTime < 500) {
            
            holder.useTime.setText(""+pingTime+"ms");
            holder.useTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(context, R.drawable.circle_green_c10), null);
        } else if(pingTime > 500) {
            holder.useTime.setText(""+pingTime+"ms");
            holder.useTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(context, R.drawable.circle_yellow_c10), null);
        } else if(pingTime < 0){
            holder.useTime.setText(R.string.ping_net_error_tip);
            holder.useTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(context, R.drawable.circle_red_c10), null);
        } else {
            holder.useTime.setText("- -");
            holder.useTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }

        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.add_node_title)
        TextView addNodeTitle;
        @BindView(R2.id.node_name)
        TextView nodeName;
        @BindView(R2.id.node_xieyi)
        ImageView nodeXieyi;
        @BindView(R2.id.use_time)
        TextView useTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

