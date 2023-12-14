

package com.wallet.ctc.ui.me.about;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wallet.ctc.R;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;



public class BugSubmitImgAdapter extends BaseAdapter {

    private Context mContext;
    private RemoveListener mRemoveListener;
    private View.OnClickListener mAddListener;
    private List<LocalFile> checkedItems;
    private int mMaxCount = 4;

    public interface RemoveListener {
        public void onRemove(int position);
    }

    public BugSubmitImgAdapter(Context context, RemoveListener removeListener, View.OnClickListener addListener, int max, List<LocalFile> checkedItems) {
        this.mContext = context;
        this.mRemoveListener = removeListener;
        this.mAddListener = addListener;
        this.mMaxCount = max;
        this.checkedItems = checkedItems;
    }

    @Override
    public int getCount() {
        return checkedItems.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bug_submit_img_item,null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image_iv);
            holder.removeBtn = (ImageButton) convertView.findViewById(R.id.remove_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.removeBtn.setOnClickListener(v->{
            if (null != mRemoveListener) {
                mRemoveListener.onRemove(position);
            }
        });
        if (position == checkedItems.size()) {
            holder.image.setImageResource(R.mipmap.skill_img_add);
            holder.image.setOnClickListener(mAddListener);
            holder.removeBtn.setVisibility(View.GONE);
            if (position == mMaxCount) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            holder.removeBtn.setVisibility(View.VISIBLE);
            holder.image.setOnClickListener(null);
            if(checkedItems.get(position).ishttp()){
                GlideUtil.showImg(mContext,checkedItems.get(position).getOriginalUri(),holder.image,null);
            }else{
                holder.image.setImageBitmap(checkedItems.get(position).getBitmap());
            }
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public ImageButton removeBtn;
    }
}
