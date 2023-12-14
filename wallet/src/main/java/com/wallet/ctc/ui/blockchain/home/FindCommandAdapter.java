

package com.wallet.ctc.ui.blockchain.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.model.blockchain.CommandEntity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.view.CircularImage;


public class FindCommandAdapter extends BaseAdapter {
    private List<CommandEntity> list;
    private Context context;
    private LayoutInflater inflater;

    public FindCommandAdapter(Context context, List<CommandEntity> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
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
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_find_command, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommandEntity mBean = list.get(position);
        GlideUtil.showImg(context, mBean.getLogo(), holder.avatar);
        holder.nickname.setText(mBean.getNickname());
        holder.dianzan.setImageResource(R.mipmap.wallet_find_nogood);
        holder.zanNum.setTextColor(0xff666666);
        if (mBean.getIs_ilike() == 1) {
            holder.dianzan.setImageResource(R.mipmap.wallet_find_good);
            holder.zanNum.setTextColor(0xff4A76D6);
        }
            holder.dianzan.setOnClickListener(v -> {
                onClick.Onclick(position, 1);
            });
            holder.zanNum.setOnClickListener(v -> {
                onClick.Onclick(position, 1);
            });
        holder.zanNum.setText("" + mBean.getLike_num());
        if (mBean.getReply_num() > 0) {
            holder.commandReply.setBackgroundResource(R.drawable.lin_f6f7fa_bg);
            holder.commandReply.setText(mBean.getReply_num() + context.getString(R.string.reply));
        } else {
            holder.commandReply.setBackgroundResource(R.color.white);
            holder.commandReply.setText(context.getString(R.string.reply));
        }
        holder.commandReply.setOnClickListener(v -> {
            onClick.Onclick(position, 2);
        });
        holder.commandDel.setVisibility(View.GONE);
        if (mBean.getIs_i() == 1) {
            holder.commandDel.setVisibility(View.VISIBLE);
        }
        holder.commandDel.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.delete_this_comment));
            builder.setCancelable(true);
            builder.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClick.Onclick(position, 3);
                }
            });
            builder.setNegativeButton(context.getString(R.string.cancel), null);
            builder.create().show();

        });
        holder.wTime.setText(AllUtils.getTimeFormatText(mBean.getW_time() + ""));
        if(null==mBean.getTo_content()|| TextUtils.isEmpty(mBean.getTo_content())) {
            holder.contentView.setText(mBean.getContent());
        }else {
            ForegroundColorSpan redSpan = new ForegroundColorSpan(0xff647BAC);
            SpannableStringBuilder builder = new SpannableStringBuilder(mBean.getContent()+" 
            int start=mBean.getContent().length()+5;
            int end=start+mBean.getTo_nickname().length();
            builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentView.setText(builder);
        }
        holder.view2.setVisibility(View.GONE);
        holder.view.setVisibility(View.GONE);
        if(position>=list.size()-1){
            holder.view.setVisibility(View.VISIBLE);
            if(list.size()<6){
                holder.view2.setVisibility(View.VISIBLE);
            }
        }else {
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R2.id.avatar)
        CircularImage avatar;
        @BindView(R2.id.nickname)
        TextView nickname;
        @BindView(R2.id.dianzan)
        ImageView dianzan;
        @BindView(R2.id.zan_num)
        TextView zanNum;
        @BindView(R2.id.content_view)
        TextView contentView;
        @BindView(R2.id.w_time)
        TextView wTime;
        @BindView(R2.id.command_reply)
        TextView commandReply;
        @BindView(R2.id.command_del)
        TextView commandDel;
        @BindView(R2.id.xian_view)
        View view;

        @BindView(R2.id.view2)
        View view2;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private OnClick onClick;

    public void SetOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    
    public interface OnClick {
        
        void Onclick(int position, int type);
    }
}
