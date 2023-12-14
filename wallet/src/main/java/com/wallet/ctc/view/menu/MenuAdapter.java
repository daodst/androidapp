

package com.wallet.ctc.view.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.fragment.mall.model.AdvertEntity;



public class MenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<AdvertEntity> mData;
    private Intent intent;

    public MenuAdapter(Context mContext, List<AdvertEntity> list) {
        this.mData = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.menu_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GlideUtil.showImg(mContext, mData.get(position).getImage(), holder.menuLogo);
        holder.menuName.setText(mData.get(position).getName());
        AdvertEntity mEntity = mData.get(position);

        holder.menuBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.menu_logo)
        ImageView menuLogo;
        @BindView(R2.id.menu_name)
        TextView menuName;
        @BindView(R2.id.menu_body)
        LinearLayout menuBody;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
