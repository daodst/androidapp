

package common.app.ui.view.update.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import common.app.R;


public class UpdateAdapter extends BaseAdapter {

    private List<String> mData;

    public UpdateAdapter(List<String> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_list_update, null, false);
            holder.mContet = (TextView) convertView.findViewById(R.id.dialog_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mContet.setText(mData.get(position));
        return convertView;
    }

    public static class ViewHolder {
        TextView mContet;
    }
}
