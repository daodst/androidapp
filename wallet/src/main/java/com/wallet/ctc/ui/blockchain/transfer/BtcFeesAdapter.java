

package com.wallet.ctc.ui.blockchain.transfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class BtcFeesAdapter extends BaseAdapter {
    private List<String> list = new ArrayList<String>();
    private int choose;
    private Context context;

    public BtcFeesAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<String> list) {
        this.list = list;
    }
    public void bindData(int position) {
        this.choose=position;
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
                    R.layout.item_btcfees, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String title=list.get(position)+" sat/b";
        if(position==0){
            title="fastestFee: "+title;
        }else if(position==1){
            title="halfHourFee: "+title;
        }else if(position==2){
            title="hourFee: "+title;
        }
        holder.feesTitle.setText(title);
        if(choose==position) {
            holder.choosedImg.setVisibility(View.VISIBLE);
        }else {
            holder.choosedImg.setVisibility(View.GONE);
        }
        return convertView;
    }
    class ViewHolder {
        @BindView(R2.id.fees_title)
        TextView feesTitle;
        @BindView(R2.id.choosed_img)
        ImageView choosedImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

