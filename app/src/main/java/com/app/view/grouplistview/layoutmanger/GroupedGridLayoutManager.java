

package com.app.view.grouplistview.layoutmanger;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.view.grouplistview.adapter.GroupedRecyclerViewAdapter;



public class GroupedGridLayoutManager extends GridLayoutManager {

    private GroupedRecyclerViewAdapter mAdapter;

    public GroupedGridLayoutManager(Context context, int spanCount,
                                    GroupedRecyclerViewAdapter adapter) {
        super(context, spanCount);
        mAdapter = adapter;
        setSpanSizeLookup();
    }

    private void setSpanSizeLookup() {
        super.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int count = getSpanCount();
                if (mAdapter != null) {
                    int type = mAdapter.judgeType(position);
                    
                    if (type == GroupedRecyclerViewAdapter.TYPE_CHILD) {
                        int groupPosition = mAdapter.getGroupPositionForPosition(position);
                        int childPosition =
                                mAdapter.getChildPositionForPosition(groupPosition, position);
                        return getChildSpanSize(groupPosition, childPosition);
                    }
                }

                return count;
            }
        });
    }

    
    public int getChildSpanSize(int groupPosition, int childPosition) {
        return 1;
    }

    @Override
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {

    }
}
