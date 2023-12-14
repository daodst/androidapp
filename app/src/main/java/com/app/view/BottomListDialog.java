

package com.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.R;
import com.app.pojo.BottomListItemBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.utils.GlideUtil;



public class BottomListDialog {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String mTitle;
    private List<BottomListItemBean> mItems;
    private Dialog mContentDialog;
    private View mContentView;

    private AdapterView.OnItemClickListener mItemClickListener;
    private ListView mListview;
    private TextView mTitleView;
    private BottomListDialogAdapter mAdapter;


    public BottomListDialog(Context context, String title, List<BottomListItemBean> items) {
        this.mContext = context;
        this.mTitle = title;
        this.mItems = items;
        this.mContentDialog = new Dialog(context);
        mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mAdapter = new BottomListDialogAdapter(items);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mItemClickListener = listener;
        setListener();
    }

    public void show() {

        View layout = mLayoutInflater.inflate(R.layout.bottom_list_dialog, null);

        mListview = (ListView) layout.findViewById(R.id.listview);
        mListview.setAdapter(mAdapter);
        
        mTitleView = (TextView) layout.findViewById(R.id.title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentDialog.setContentView(layout, layoutParams);
        Window win = mContentDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mContentDialog.show();
        layout.findViewById(R.id.close).setOnClickListener(v -> {
            mContentDialog.dismiss();
        });
        setListener();
    }

    private void setListener() {
        if (null != mListview) {
            mListview.setOnItemClickListener(mItemClickListener);
        }
    }

    public void dismiss() {
        mContentDialog.dismiss();
    }

    public class BottomListDialogAdapter extends BaseAdapter {

        List<BottomListItemBean> mList;

        public BottomListDialogAdapter(List<BottomListItemBean> items) {
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(
                        R.layout.bottom_list_dialog_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BottomListItemBean itemBean = mList.get(position);
            if (null != itemBean) {
                if (!TextUtils.isEmpty(itemBean.img)) {
                    GlideUtil.showImg(mContext, itemBean.img, holder.mImg);
                } else if (itemBean.iconId > 0) {
                    GlideUtil.showImg(mContext, itemBean.iconId, holder.mImg);
                } else {
                    holder.mImg.setVisibility(View.GONE);
                }
                holder.mTitle.setText(itemBean.title);
            }
            return convertView;
        }


    }
    public static class ViewHolder {
        @BindView(R.id.img)
        ImageView mImg;
        @BindView(R.id.title)
        TextView mTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
