

package im.vector.app.features.call.phone.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import im.vector.app.R;


public class SMSelectPhoneAdapter extends RecyclerView.Adapter<SMSelectPhoneAdapter.ViewHolder> {

    List<SMPhoneNumberEntity> data;

    public void setData(List<SMPhoneNumberEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater from = LayoutInflater.from(context);
        View inflate = from.inflate(R.layout.sm_popup_select_phone_number_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SMPhoneNumberEntity entity = data.get(position);
        holder.tv_phone_number.setText(entity.phoneNumber);
        holder.iv_selected.setVisibility(entity.checked ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            mICall.call(entity);
        });
    }


    private ICall mICall;

    public void setICall(ICall ICall) {
        mICall = ICall;
    }

    public interface ICall {
        void call(SMPhoneNumberEntity entity);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_phone_number;
        ImageView iv_selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_phone_number = itemView.findViewById(R.id.tv_phone_number);
            iv_selected = itemView.findViewById(R.id.iv_selected);

        }
    }

}
