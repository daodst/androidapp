

package com.wallet.ctc.view.choosemorepic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.ui.me.about.LocalFile;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;
import java.util.Map;




public class LocalAlbumAdapter extends BaseAdapter {
    private Map<String, List<LocalFile>> folders;
    private Context context;
    private List<String> folderNames;

    public LocalAlbumAdapter(Context context, Map<String, List<LocalFile>> folders, List<String> folderNames) {
        this.folders = folders;
        this.context = context;
        this.folderNames = folderNames;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_albumfoler, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = folderNames.get(i);
        List<LocalFile> files = folders.get(name);
        viewHolder.textView.setText(name + "(" + files.size() + ")");
        if (files.size() > 0) {
            GlideUtil.showImgSD(context, files.get(0).getThumbnailUri(), viewHolder.imageView);

        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
