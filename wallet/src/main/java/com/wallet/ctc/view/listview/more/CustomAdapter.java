

package com.wallet.ctc.view.listview.more;



import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class CustomAdapter {
    private String TAG = CustomAdapter.class.getSimpleName();
    private View myView;
    private ViewGroup myViewGroup;
    private CustomListView myCustomListView;
    private AdapterView.OnItemClickListener listener;
    private AdapterView.OnItemLongClickListener longListener;

    public CustomAdapter() {
    }

    public int getCount() {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0L;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private final void getAllViewAddSexangle() {
        this.myCustomListView.removeAllViews();

        for(int i = 0; i < this.getCount(); ++i) {
            View viewItem = this.getView(i, this.myView, this.myViewGroup);
            this.myCustomListView.addView(viewItem, i);
        }

    }

    public void notifyDataSetChanged() {
        CustomListView.setAddChildType(true);
        this.notifyCustomListView(this.myCustomListView);
    }

    public void notifyCustomListView(CustomListView formateList) {
        this.myCustomListView = formateList;
        this.myCustomListView.removeAllViews();
        this.getAllViewAddSexangle();
        this.setOnItemClickListener(this.listener);
        this.setOnItemLongClickListener(this.longListener);
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener listener) {
        this.listener = listener;

        for(int i = 0; i < this.myCustomListView.getChildCount(); ++i) {
            View view = this.myCustomListView.getChildAt(i);
            final int s = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(CustomAdapter.this.TAG, "Item : " + s);
                    listener.onItemClick((AdapterView)null, v, s, (long)CustomAdapter.this.getCount());
                }
            });
        }

    }

    public void setOnItemLongClickListener(final AdapterView.OnItemLongClickListener listener) {
        this.longListener = listener;

        for(int i = 0; i < this.myCustomListView.getChildCount(); ++i) {
            View view = this.myCustomListView.getChildAt(i);
            final int s = i;
            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick((AdapterView)null, v, s, (long)CustomAdapter.this.getCount());
                    return true;
                }
            });
        }

    }
}
