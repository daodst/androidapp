

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.view.listview.more.CustomAdapter;

import java.util.List;



public class ZhujiciCheckAdapter extends CustomAdapter {
    private List<String> list;
    private Context con;
    private LayoutInflater inflater;

    public ZhujiciCheckAdapter(Context context, List<String> list) {
        this.con = context;
        this.list = list;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();

            convertView = inflater.inflate(R.layout.adapter_check_item, null);

            vh.tv = (TextView) convertView.findViewById(R.id.adapter_text);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setTextColor(0xff666666);
        vh.tv.setBackgroundColor(0xffffffff);
        vh.tv.setText(list.get(position));
        return convertView;
    }

    public class ViewHolder{
        public TextView tv;
    }
}
