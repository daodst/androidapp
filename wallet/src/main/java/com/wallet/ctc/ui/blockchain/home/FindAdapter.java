

package com.wallet.ctc.ui.blockchain.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.ArticleBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.view.RoundImageView;



public class FindAdapter extends BaseAdapter {
    private List<ArticleBean> list = new ArrayList<ArticleBean>();
    private Context context;

    public FindAdapter(Context context) {
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
                    R.layout.item_find, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ArticleBean mEntity = list.get(position);
        holder.findContent.setText(mEntity.getTitle());
        holder.findTime.setText(AllUtils.getTimeFormatText(new BigDecimal(mEntity.getW_time()).toPlainString()) + "  " + mEntity.getAuthor());
        holder.findLook.setText(mEntity.getLook_num() + context.getString(R.string.people));
        GlideUtil.showFindImg(context, mEntity.getCover(), holder.findLogo);
        holder.zanNum.setText(mEntity.getIntUp_num()+"");
        if(mEntity.getIs_updown()==1){
            holder.dianzan.setImageResource(R.mipmap.wallet_find_good_yellow);
        }else{
            holder.dianzan.setImageResource(R.mipmap.wallet_find_nogood);
        }
        holder.dianzanLin.setOnClickListener(v -> {
            click.onItemClick(1,position);
        });
        return convertView;
    }
    private ItemClick click;
    public void setClick(ItemClick click){
        this.click=click;
    }

    public interface ItemClick{
        void onItemClick(int type,int position);
    }
    class ViewHolder {
        @BindView(R2.id.find_content)
        TextView findContent;
        @BindView(R2.id.find_logo)
        RoundImageView findLogo;
        @BindView(R2.id.find_time)
        TextView findTime;
        @BindView(R2.id.find_look)
        TextView findLook;
        @BindView(R2.id.dianzan)
        ImageView dianzan;
        @BindView(R2.id.zan_num)
        TextView zanNum;
        @BindView(R2.id.dianzan_lin)
        LinearLayout dianzanLin;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

