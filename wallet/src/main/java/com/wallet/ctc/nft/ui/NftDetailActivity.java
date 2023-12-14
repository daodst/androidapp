

package com.wallet.ctc.nft.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.nft.adapter.NftAssetsAdapter;
import com.wallet.ctc.nft.adapter.SkyAdapter;
import com.wallet.ctc.view.TitleBarView;

import java.util.List;

import butterknife.BindView;
import common.app.base.BaseActivity;
import common.app.my.view.CircularImage;

public class NftDetailActivity extends BaseActivity<NftDetailBiz> {
    @BindView(R2.id.title_bar)
    TitleBarView titleBar;
    @BindView(R2.id.iv_logo)
    CircularImage ivLogo;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_address)
    TextView tvAddress;
    @BindView(R2.id.rv_assets)
    RecyclerView rvAssets;
    @BindView(R2.id.srl)
    SmartRefreshLayout srl;
    @BindView(R2.id.iv_empty)
    ImageView ivEmpty;
    private NftAssetsAdapter adapter = null;
    private Gson gson = new Gson();

    
    private NftBean nftBean = null;

    public static void intent(Context context, NftBean data) {
        Intent in = new Intent(context, NftDetailActivity.class);
        in.putExtra("data", data);
        context.startActivity(in);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_nft_detail;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        nftBean = (NftBean) getIntent().getSerializableExtra("data");
        rvAssets.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new NftAssetsAdapter();
        adapter.setClickListener(new SkyAdapter.OnAdapterItemClick<NftBean>() {
            @Override
            public void onItemClick(NftBean data, int position) {
                NftAssetsDetailActivity.intent(NftDetailActivity.this, data);
            }
        });
        rvAssets.setAdapter(adapter);
        getViewModel().initData(nftBean);
        showInfo();
        register();
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getViewModel().getNftAssets();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
       
            srl.autoRefresh();
      
    }

    private void register() {
        getViewModel().getNftAssetsLiveData.observe(this, new Observer<List<NftBean>>() {
            @Override
            public void onChanged(List<NftBean> nftBeans) {
                finishRefresh();
                if (null == nftBeans || nftBeans.size() == 0) {
                    showEmpty();
                } else {
                    showContent();
                }
                adapter.setItems(nftBeans);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showEmpty() {
        rvAssets.setVisibility(View.GONE);
        ivEmpty.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        rvAssets.setVisibility(View.VISIBLE);
        ivEmpty.setVisibility(View.GONE);
    }

    private void showInfo() {
        tvName.setText(nftBean.name);
        tvAddress.setText(nftBean.token_address);
    }

    private void finishRefresh() {
      
            srl.finishRefresh();
       
    }
}
