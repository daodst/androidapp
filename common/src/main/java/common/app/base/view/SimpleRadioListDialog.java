

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
import common.app.base.model.RadioListItemBean;
import common.app.base.view.adapter.SimpleRadioListAdapter;



public class SimpleRadioListDialog {

    private Context mContext;
    private String mTitle;
    private List<RadioListItemBean> mItems;
    private Dialog mContentDialog;
    private View mContentView;
    private LayoutInflater mLayoutInflater;
    private AdapterView.OnItemClickListener mItemClickListener;
    private ListView mListview;
    private TextView mTitleView;

    public SimpleRadioListDialog(Context context, String title, List<RadioListItemBean> items) {
        this.mContext = context;
        this.mTitle = title;
        this.mItems = items;
        this.mContentDialog = new Dialog(context);
        mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mItemClickListener = listener;
        setListener();
    }

    public void show() {
        if (null == mContentView) {
            mContentView = mLayoutInflater.inflate(R.layout.simple_radio_list_dialog,null);
        }
        mListview = (ListView) mContentView.findViewById(R.id.listview);
        mTitleView = (TextView) mContentView.findViewById(R.id.title);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        }
        if (null != mItems && mItems.size() > 0) {
            SimpleRadioListAdapter adapter = new SimpleRadioListAdapter(mContext, mItems);
            adapter.setItemClickListerner((view, position) -> {
                if (null != mItemClickListener) {
                    mItemClickListener.onItemClick(null, view, position, position);
                }
            });
            mListview.setAdapter(adapter);
        }
        setListener();
        mContentDialog.setContentView(mContentView);
        mContentDialog.setCanceledOnTouchOutside(true);
        mContentDialog.show();
    }

    private void setListener(){
        if (null != mListview) {
            mListview.setOnItemClickListener(mItemClickListener);
        }
    }

    public void dismiss() {
        mContentDialog.dismiss();
    }
}
