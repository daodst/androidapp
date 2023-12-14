package com.wallet.ctc.ui.dapp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.DappHistoryEntity;
import com.wallet.ctc.nft.adapter.SkyAdapter;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.util.FastClickUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.app.base.BaseActivity;

public class DappSearchHistoryActivity extends BaseActivity<DappSearchHistoryViewModel> {
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.tv_search)
    TextView tvSearch;
    @BindView(R2.id.ll_search)
    LinearLayout llSearch;
    @BindView(R2.id.iv_delete)
    ImageView ivDelete;
    @BindView(R2.id.rv_history)
    RecyclerView rvHistory;
    private DappSearchHistoryAdapter adapter = null;

    private static final String KEY_URL = "url";
    private String mUrl;

    public static void intent(Context context, String url) {
        Intent intent = new Intent(context, DappSearchHistoryActivity.class);
        if (!TextUtils.isEmpty(url)) {
            intent.putExtra(KEY_URL, url);
        }
        context.startActivity(intent);
    }


    @Override
    public void initParam() {
        mUrl = getIntent().getStringExtra(KEY_URL);
    }


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_js_browse_history;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        adapter = new DappSearchHistoryAdapter();
        adapter.setClickListener(new SkyAdapter.OnAdapterItemClick<DappHistoryEntity>() {
            @Override
            public void onItemClick(DappHistoryEntity browseHistoryEntity, int position) {
                if (TextUtils.isEmpty(browseHistoryEntity.iconPath)) {
                    getViewModel().loadWebsiteInfo(browseHistoryEntity);
                }
                DappWebViewActivity.startDappWebViewActivity(DappSearchHistoryActivity.this, browseHistoryEntity.url);
            }
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvHistory.setAdapter(adapter);

    }

    @Override
    public void initData() {
        super.initData();
        getViewModel().getBrowseHistoryListLiveData.observe(this, new Observer<List<DappHistoryEntity>>() {
            @Override
            public void onChanged(List<DappHistoryEntity> browseHistoryEntities) {
                adapter.setItems(browseHistoryEntities);
                adapter.notifyDataSetChanged();
            }
        });

        getViewModel().deleteBrowseHistoryLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                adapter.setItems(new ArrayList<>());
                adapter.notifyDataSetChanged();
            }
        });

        if (!TextUtils.isEmpty(mUrl)) {
            etSearch.setText(mUrl);
            
            search();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getHistoryList();
    }

    @OnClick({R2.id.iv_back, R2.id.tv_search, R2.id.iv_delete})
    public void onClick(View view) {
        if (FastClickUtils.isFastClick()) {
            return;
        }

        int viewId = view.getId();
        if (viewId == R.id.iv_back) {
            finish();
        } else if (viewId == R.id.tv_search) {
            search();
        } else if (viewId == R.id.iv_delete) {
            getViewModel().deleteHistory();
        }
    }

    private void search() {
        if (null != getCurrentFocus()) {
            ((InputMethodManager) etSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        String text = etSearch.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (!text.startsWith("http://") && !text.startsWith("https://")) {
            
            text = "http://" + text;
            
        }
        getViewModel().addHistoryList(text);
        DappWebViewActivity.startDappWebViewActivity(this, text);

    }
}
