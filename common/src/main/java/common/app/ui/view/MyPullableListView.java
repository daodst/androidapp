

package common.app.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.List;

import common.app.R;
import common.app.mall.util.ToastUtil;
import common.app.ui.adapter.SimpleBaseAdapter;


public class MyPullableListView extends SimpleLinearLayout {

    private ViewHolder mViewHolder;
    private boolean showLoading = false;
    private int dividerHeight = 0;
    private Drawable divider;
    private int listTopPadding = 0;
    private int listBottomPadding = 0;
    public MyPullableListView(Context context) {
        super(context);
    }

    public MyPullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyPullableListView);
        this.showLoading = typedArray.getBoolean(R.styleable.MyPullableListView_showLoading, false);
        dividerHeight = typedArray.getDimensionPixelSize(
                R.styleable.MyPullableListView_itemDividerHeight, 0);
        divider = typedArray.getDrawable(R.styleable.MyPullableListView_itemDivider);
        listTopPadding = typedArray.getDimensionPixelSize(R.styleable.MyPullableListView_listPaddingTop, 0);
        listBottomPadding = typedArray.getDimensionPixelSize(R.styleable.MyPullableListView_listPaddingBottom, 0);
        typedArray.recycle();
        init();
    }

    @Override
    protected void initViews() {
        this.contentView = inflate(mContext, R.layout.pullable_list_view, this);
        mViewHolder = new ViewHolder(contentView);
    }

    private void init() {
        if (showLoading) {
            showLoading();
        } else {
            hideLoading();
        }
        if (null != divider && null != mViewHolder) {
            mViewHolder.mListView.setDivider(divider);
        }
        if (dividerHeight != 0 && null != mViewHolder) {
            mViewHolder.mListView.setDividerHeight(dividerHeight);
        }
        if ((listTopPadding != 0 || listBottomPadding != 0) && null != mViewHolder) {
            mViewHolder.mListView.setPadding(0, listTopPadding, 0, listBottomPadding);
            
            mViewHolder.mListView.setClipToPadding(false);
        }
    }


    public void showLoading() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mLoadingView.setVisibility(VISIBLE);
    }

    public void hideLoading() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mLoadingView.setVisibility(GONE);
    }

    public void showEmptyView() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        hideLoading();
        mViewHolder.mEmptyView.setVisibility(VISIBLE);
    }

    public void hideEmptyView() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mLoadingView.setVisibility(GONE);
        mViewHolder.mEmptyView.setVisibility(GONE);
    }

    public void setAdapter(ListAdapter adapter) {
        if (null == mViewHolder || null == mContext || null == adapter) {
            return;
        }
        mViewHolder.mListView.setAdapter(adapter);
    }

    
    public <T> void showDatas(List<T> datas, Integer nowPage, SimpleBaseAdapter adapter) {
        if (null == mViewHolder || null == mContext || mViewHolder.mListView == null || null == adapter) {
            return;
        }
        if (nowPage == 1) {
            adapter.clearnAndAddAll(datas);
        } else {
            if(null == datas || datas.size() == 0){
                ToastUtil.showToast(mContext.getString(R.string.nomore));
                nowPage--;
            }
            adapter.addAllDatas(datas);
        }
        if (adapter.getCount() == 0) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    
    public void setPullState(boolean success, int nowPage) {
        if (success) {
            
            if (nowPage == 1) {
                refreshSuccess();
            } else {
                loadMoreSuccess();
            }
        } else {
            
            if (nowPage == 1) {
                refreshFaile();
            } else {
                loadMoreFail();
            }
        }
    }

    public void refreshSuccess() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    public void refreshFaile() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.refreshFinish(PullToRefreshLayout.FAIL);
    }

    public void loadMoreSuccess() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    public void loadMoreFail() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
    }

    public void autoRefresh() {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.autoRefresh();
    }

    public void setCanPullDown(boolean canPullDown) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.setCanPullDown(canPullDown);
    }

    public void setCanPUllUp(boolean canPUllUp) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.setCanPullUp(canPUllUp);
    }


    public void addHeaderView(View headView) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.addHeaderView(headView);
    }

    public void addFooterView(View footView) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.addFooterView(footView);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mListView.setOnItemLongClickListener(listener);
    }

    public void setOnRefreshListener(PullToRefreshLayout.OnRefreshListener listener) {
        if (null == mViewHolder || null == mContext) {
            return;
        }
        mViewHolder.mRefreshView.setOnRefreshListener(listener);
    }

    static class ViewHolder {
        PullableListView mListView;
        PullToRefreshLayout mRefreshView;
        LinearLayout mEmptyView;
        LinearLayout mLoadingView;

        ViewHolder(View view) {
            mListView = view.findViewById(R.id.list_view);
            mRefreshView = view.findViewById(R.id.refresh_view);
            mEmptyView = view.findViewById(R.id.emptyInclude);
            mLoadingView = view.findViewById(R.id.loading_view);
        }
    }
}
