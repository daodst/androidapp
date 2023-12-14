

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.view.listview.more.CustomAdapter;

import java.util.ArrayList;
import java.util.List;



public class ZhujiciAdapter extends CustomAdapter {
    private List<String> list;
    private List<String> choose=new ArrayList<>();
    private Context con;
    private LayoutInflater inflater;

    public ZhujiciAdapter(Context context, List<String> list,List<String> choose) {
        this.con = context;
        this.list = list;
        this.choose=choose;
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

            convertView = inflater.inflate(R.layout.adapter_sexangle_item_style, null);

            vh.tv = (TextView) convertView.findViewById(R.id.adapter_text);

            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        String name=list.get(position);
        vh.tv.setText(name);
        vh.tv.setTextColor(0xff666666);
        vh.tv.setBackgroundColor(0xffffffff);
        for(int i=0;i<choose.size();i++){
            if(choose.get(i).equals(name)){
                vh.tv.setTextColor(0xffffffff);
                vh.tv.setBackgroundColor(0xff4AB6E7);
                break;
            }
        }



        return convertView;
    }

    public class ViewHolder{
        public TextView tv;
    }
}
