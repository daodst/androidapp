

package common.app.base.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.R;
import common.app.R2;
import common.app.base.model.RadioListItemBean;



public class SimpleRadioListAdapter extends BaseAdapter {

    public List<RadioListItemBean> mDatas;
    public Context mContext;
    public LayoutInflater mInflater;
    private ItemClickListerner mItemClickListerner;
    public SimpleRadioListAdapter(Context context, List<RadioListItemBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public interface ItemClickListerner {
        public void onItemClick(View view, int position);
    }

    public void setItemClickListerner(ItemClickListerner listerner) {
        this.mItemClickListerner = listerner;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.simple_list_radio_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RadioListItemBean itemBean = mDatas.get(position);
        if (null != itemBean) {
            holder.mRadioBtn.setChecked(itemBean.checked);
            holder.mContent.setText(itemBean.content);
            
            
            holder.mRadioBtn.setOnClickListener(new SingleRadioItemClickListener(convertView, itemBean, position));
        }
        return convertView;
    }

    private void reSetRadioState() {
        if (null != mDatas && mDatas.size() > 0) {
            for (int i=0; i < mDatas.size(); i++) {
                mDatas.get(i).checked = false;
            }
        }
    }



    public class SingleRadioItemClickListener implements View.OnClickListener {
        private View mView;
        private RadioListItemBean mItemData;
        private int mPosition;

        public SingleRadioItemClickListener(View view, RadioListItemBean item, int position) {
            this.mItemData = item;
            this.mPosition = position;
            this.mView = view;
        }

        @Override
        public void onClick(View v) {
            if (!mItemData.checked) {
                for (int i=0; i < mDatas.size(); i++) {
                    mDatas.get(i).checked = false;
                }
                mItemData.checked = true;
                notifyDataSetChanged();
            }
            if (null != mItemClickListerner) {
                mItemClickListerner.onItemClick(mView, mPosition);
            }
        }
    }

    static class ViewHolder {
        @BindView(R2.id.radio_btn)
        RadioButton mRadioBtn;
        @BindView(R2.id.content)
        TextView mContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
