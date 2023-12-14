

package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.SGBApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.BaseSgbTransHistory;
import com.wallet.ctc.model.blockchain.SgbTransHistory;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionSgbRecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransferSgbActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferSgbDetailActivity;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class AssetsSgbDetailActivity extends BaseActivity {


    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.daib_num)
    TextView daib_num;
    @BindView(R2.id.daib_price)
    TextView daib_price;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.listview)
    PullableListView listview;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    @BindView(R2.id.transfer)
    LinearLayout transfer;
    @BindView(R2.id.collect_money)
    LinearLayout collectMoney;
    @BindView(R2.id.bottom_lin)
    LinearLayout bottomLin;
    private View nodata;
    private TransactionSgbRecordAdapter mAdapter;

    private Intent intent;
    private Gson gson = new Gson();
    private SGBApi mApi = new SGBApi();
    private List<SgbTransHistory> mData = new ArrayList<>();
    private WalletDBUtil walletDBUtil;
    private int page=0;

    @Override
    public int initContentView() {
        return R.layout.activity_filassetsdetail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        nodata = findViewById(R.id.nodata);
        walletDBUtil = WalletDBUtil.getInstent(this);
        mAcache = ACache.get(this);
        tvTitle.setText("SGB");
        mAdapter = new TransactionSgbRecordAdapter(this);
        mAdapter.bindAddress(walletDBUtil.getWalletInfo().getAllAddress());
        mAdapter.bindData(mData);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsSgbDetailActivity.this, TransferSgbDetailActivity.class);
                intent.putExtra("detail", gson.toJson(mData.get(position)));
                startActivity(intent);
            }
        });
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                loadData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                loadHistory();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        page=0;
        listview.setAdapter(mAdapter);
        loadHistory();
    }

    @Override
    public void initData() {

    }

    @OnClick({R2.id.tv_back, R2.id.transfer, R2.id.collect_money, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
        } else if (i == R.id.transfer) {
            if(walletDBUtil.getWalletInfo().getLevel()==-1){
                ToastUtil.showToast(getString(R.string.wallet_type_error));
                return;
            }
            if (NetUtils.isNetworkConnected(this)) {
                intent = new Intent(this, TransferSgbActivity.class);
                startActivity(intent);
            } else {
                ToastUtil.showToast(getString(R.string.connect_failuer_toast));
            }

        } else if (i == R.id.collect_money) {
            CollectMoneyActivity.startCollectMoneyActivity(this, WalletUtil.SGB_COIN,"",10);
        } else {
        }
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap<>();
        params.put("row", 50);
        params.put("page",page);
        params.put("address",walletDBUtil.getWalletInfo().getAllAddress());
        mApi.getTransHistory(new Gson().toJson(params), WalletUtil.SGB_COIN).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseSgbTransHistory>(this) {
                    @Override
                    public void onNexts(BaseSgbTransHistory baseEntity) {
                        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        if (baseEntity.getMessage().equalsIgnoreCase("success")) {
                            if(page==0){
                                mData.clear();
                            }
                            mData.addAll(baseEntity.getData().getTransfers());
                            page++;
                            if (null == mData || mData.size() < 1) {
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                nodata.setVisibility(View.GONE);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getMessage());
                            if (null == mData || mData.size() < 1) {
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                nodata.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null == mData || mData.size() < 1) {
                            nodata.setVisibility(View.VISIBLE);
                        } else {
                            nodata.setVisibility(View.GONE);
                        }
                        if(null!=refreshView) {
                            refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                            refreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                });
    }
}
