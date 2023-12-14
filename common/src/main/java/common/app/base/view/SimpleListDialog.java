

package common.app.base.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import common.app.R;



public class SimpleListDialog {

    private Context mContext;
    private String mTitle;
    private List<String> mItems;
    private Dialog mContentDialog;
    private View mContentView;
    private LayoutInflater mLayoutInflater;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private ListView mListview;
    private TextView mTitleView;
    private WalltAddrAdapter addrAdapter;

    public SimpleListDialog(Context context, String title, List<String> items) {
        this.mContext = context;
        this.mTitle = title;
        this.mItems = items;
        this.mContentDialog = new Dialog(context);
        mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mLayoutInflater = LayoutInflater.from(context);
        addrAdapter = new WalltAddrAdapter(context, mItems);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mItemClickListener = listener;
        setListener();
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        setListener();
    }

    public void show() {
        if (null == mContentView) {
            mContentView = mLayoutInflater.inflate(R.layout.simple_list_dialog, null);
        }
        mListview = (ListView) mContentView.findViewById(R.id.listview);
        mTitleView = (TextView) mContentView.findViewById(R.id.title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        } else {
            mTitleView.setVisibility(View.GONE);
        }
        if (null != mItems && mItems.size() > 0) {
            mListview.setAdapter(addrAdapter);
        }
        setListener();
        mContentDialog.setContentView(mContentView);
        mContentDialog.setCanceledOnTouchOutside(true);
        mContentDialog.show();
    }

    private void setListener() {
        if (null != mListview) {
            mListview.setOnItemClickListener(mItemClickListener);
            mListview.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    public void dismiss() {
        mContentDialog.dismiss();
    }
}
