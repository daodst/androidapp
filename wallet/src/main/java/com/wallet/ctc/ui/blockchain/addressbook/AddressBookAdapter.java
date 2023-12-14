

package com.wallet.ctc.ui.blockchain.addressbook;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.AppHolder;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.AddressBookEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;
import common.app.utils.GlideUtil;



public class AddressBookAdapter extends BaseAdapter {
    private List<AddressBookEntity> list = new ArrayList<AddressBookEntity>();
    private Context context;

    public AddressBookAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<AddressBookEntity> list) {
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
                    R.layout.item_addressbook, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AddressBookEntity mBean=list.get(position);
        GlideUtil.showImg(context, AppHolder.getLogoByAddress(mBean.getAddress()), holder.assetsLogo);
        holder.assetsName.setText(mBean.getName());
        holder.assetsConten.setText(mBean.getAddress());
        if (TextUtils.isEmpty(mBean.getRemark())) {
            holder.remarks.setVisibility(View.GONE);
        } else {
            holder.remarks.setVisibility(View.VISIBLE);
        }
        holder.remarks.setText(mBean.getRemark());
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.assets_logo)
        CircularImage assetsLogo;
        @BindView(R2.id.assets_name)
        TextView assetsName;
        @BindView(R2.id.assets_conten)
        TextView assetsConten;
        @BindView(R2.id.remarks)
        TextView remarks;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

