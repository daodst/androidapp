

package com.wallet.ctc.ui.blockchain.choosenode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.ChooseNodeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class ChooseNodeAdapter extends BaseAdapter {
    private List<ChooseNodeBean> list = new ArrayList<ChooseNodeBean>();
    private Context context;

    public ChooseNodeAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<ChooseNodeBean> list) {
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
                    R.layout.item_choosenode, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChooseNodeBean mBean=list.get(position);
        if(mBean.getType()==0) {
            holder.nodeName.setText(mBean.getName() + "   IP " + mBean.getSmallUrl());
        }else {
            holder.nodeName.setText( mBean.getName()+"     "+mBean.getDistrict() );
        }
        holder.nodeXieyi.setText(context.getString(R.string.data_transfer_protocol));
        int wangsu=mBean.getDelay();
        holder.nodeSudu.setText(context.getString(R.string.network_delay)+""+wangsu+"ms");
        if(wangsu<100) {
            holder.nodeK.setImageResource(R.mipmap.henkuai);
        }else if(wangsu<300) {
            holder.nodeK.setImageResource(R.mipmap.kuai);
        }else {
            holder.nodeK.setImageResource(R.mipmap.yiban);
        }
        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.node_name)
        TextView nodeName;
        @BindView(R2.id.node_xieyi)
        TextView nodeXieyi;
        @BindView(R2.id.node_sudu)
        TextView nodeSudu;
        @BindView(R2.id.node_k)
        ImageView nodeK;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

