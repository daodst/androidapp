

package com.wallet.ctc.ui.blockchain.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.ArticleBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class HelpAdapter extends BaseAdapter {
    private List<ArticleBean> list = new ArrayList<ArticleBean>();
    private Context context;

    public HelpAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<ArticleBean> list) {
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
                    R.layout.item_help, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ArticleBean mEntity=list.get(position);
        holder.helpTitle.setText(mEntity.getTitle());
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.help_title)
        TextView helpTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

