

package common.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class SimpleBaseAdapter<T, H extends SimpleBaseViewHolder> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;

    public SimpleBaseAdapter(Context context) {
        this.mContext = context;
        this.mDatas = new ArrayList<>();
    }

    
    public void addAllDatas(List<T> datas) {
        if (null != mDatas) {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void clearnAndAddAll(List<T> datas) {
        if (null != mDatas) {
            mDatas.clear();
        }
        if (null != datas) {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(getItemLayoutId(), parent, false);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }
        showItem(holder, getItem(position));
        return convertView;
    }

    
    protected abstract H createViewHolder(View convertView);

    
    public abstract int getItemLayoutId();

    
    public abstract void showItem(H holder, T data);

}
