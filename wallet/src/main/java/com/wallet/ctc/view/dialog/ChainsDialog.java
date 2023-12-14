

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.wallet.ctc.R;

import java.util.ArrayList;
import java.util.List;



public class ChainsDialog {

    private OnClick mOnClick;
    private Dialog mDialog;
    private ListView listView;
    private List<String> mList=new ArrayList();

    public void setOnClick(OnClick onClick) {
        this.mOnClick = onClick;
    }

    public ChainsDialog(Context context, List<String> list) {
        mList=list;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_depositi, null);
        listView = (ListView) view.findViewById(R.id.listview);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);
        QuickAdapter adapter = new QuickAdapter<String>(context, R.layout.item_depositi) {
            @Override
            protected void convert(BaseAdapterHelper helper, final String listBean) {
                helper.setText(R.id.item_day, listBean);
            }
        };
        adapter.replaceAll(mList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnClick.onConfirm(mList.get(position)+"",mList.get(position)+"");
                dismiss();
            }
        });
    }
    public void show() {
        if (null != mDialog) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    public interface OnClick {
        void onConfirm(String name, String id);
    }
}
