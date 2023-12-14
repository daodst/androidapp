

package im.vector.app.features.call.phone.logs.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.matrix.android.sdk.internal.database.model.ChatPhoneLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import im.vector.app.R;
import im.wallet.router.util.Consumer;


public class SMDialHistoryAdapter extends RecyclerView.Adapter<SMDialHistoryAdapter.ViewHolder> {

    private List<ChatPhoneLog> mChatPhoneLogs;

    public void setChatPhoneLogs(List<ChatPhoneLog> chatPhoneLogs) {
        mChatPhoneLogs = chatPhoneLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        View view = from.inflate(R.layout.sm_activity_dial_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return null == mChatPhoneLogs ? 0 : mChatPhoneLogs.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatPhoneLog log = mChatPhoneLogs.get(position);
        Context context = holder.itemView.getContext();
        holder.phoneTextView.setText(log.getPhone());
        holder.tv_times.setText(times(log.getTime()));
        holder.remark.setText(log.getBestName());

        
        holder.remark.setVisibility(TextUtils.isEmpty(log.getBestName()) ? View.GONE : View.VISIBLE);
        
        if (log.getIncome() == 1) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.mipmap.sm_icon_call_history_phone);
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
            if (log.getStatus() == 2) {
                
                holder.remark.setTextColor(ContextCompat.getColor(context, R.color.default_error_color));
                holder.phoneTextView.setTextColor(ContextCompat.getColor(context, R.color.default_error_color));
            } else {
                holder.remark.setTextColor(ContextCompat.getColor(context, R.color.default_hint_text_color));
                holder.phoneTextView.setTextColor(ContextCompat.getColor(context, R.color.default_hint_text_color));
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (null != mConsumer) {
                mConsumer.accept(log);
            }
        });
    }


    public Consumer<ChatPhoneLog> mConsumer;

    public void setConsumer(Consumer<ChatPhoneLog> consumer) {
        mConsumer = consumer;
    }

    public static String times(Long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yy/MM/dd HH:mm");
        String times = sdr.format(new Date(time));
        return times;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView phoneTextView;
        TextView remark;
        TextView tv_times;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_phone);
            phoneTextView = itemView.findViewById(R.id.tv_phone_number);
            remark = itemView.findViewById(R.id.tv_remark_name);
            tv_times = itemView.findViewById(R.id.tv_times);
        }
    }
}
