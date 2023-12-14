

package common.app.base.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import common.app.R;



public class WalltAddrAdapter extends BaseAdapter{
    private List<String> list;
    private Context context;
    public WalltAddrAdapter(Context context,List<String> list) {
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHodler viewHodler;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.dialog_addr,null);
            viewHodler=new ViewHodler();
            viewHodler.tv=(TextView) view.findViewById(R.id.tvAddr);
            view.setTag(viewHodler);
        }else {
            viewHodler= (ViewHodler) view.getTag();
        }
        viewHodler.tv.setText(list.get(i));
        return view;
    }
    class  ViewHodler{
        TextView tv;
    }
}
