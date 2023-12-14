

package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.BaseTrxBean;
import com.wallet.ctc.model.blockchain.TrxTransferHistoryBean;
import com.wallet.ctc.model.blockchain.TrxTrc20TransferHistoryBean;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionTrxRecordAdapter;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionTrxTrc20RecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransferTrxActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferTrxDetailActivity;
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



public class AssetsTRXDetailActivity extends BaseActivity {


    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
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
    private TransactionTrxRecordAdapter mAdapter;
    private TransactionTrxTrc20RecordAdapter mTrc20Adapter;
    private Intent intent;
    private String type = "";
    private String tokenType = "";
    private String gascount = "";
    private int decimal = 18;
    private String DbPrice;
    private Gson gson = new Gson();
    private TrxApi mApi = new TrxApi();
    private List<TrxTransferHistoryBean> mData = new ArrayList<>();
    private List<TrxTrc20TransferHistoryBean> mTrc20Data = new ArrayList<>();
    private WalletDBUtil walletDBUtil;
    private String fingerprint = "";

    @Override
    public int initContentView() {
        return R.layout.activity_trxassetsdetail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        nodata=findViewById(R.id.nodata);
        walletDBUtil = WalletDBUtil.getInstent(this);
        mAcache = ACache.get(this);
        imgAction.setImageResource(R.mipmap.eth_token_detail);
        type = getIntent().getStringExtra("type");
        DbPrice = getIntent().getStringExtra("DbPrice");
        tokenType = getIntent().getStringExtra("address");
        gascount = getIntent().getStringExtra("gasCount");
        decimal = getIntent().getIntExtra("decimal", 18);
        if (DbPrice == null || DbPrice.equals("")) {
            DbPrice = "0";
        }
        if(!type.toUpperCase().equals("TRX")) {
            imgAction.setVisibility(View.VISIBLE);
        }else {
            imgAction.setVisibility(View.GONE);
        }
        tvTitle.setText(type);
        mAdapter = new TransactionTrxRecordAdapter(this, type,decimal);
        mAdapter.bindAddress(walletDBUtil.getWalletInfo().getAllAddress());
        mAdapter.bindData(mData);
        mTrc20Adapter=new TransactionTrxTrc20RecordAdapter(this,type,decimal);
        mTrc20Adapter.bindAddress(walletDBUtil.getWalletInfo().getAllAddress());
        mTrc20Adapter.bindData(mTrc20Data);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsTRXDetailActivity.this, TransferTrxDetailActivity.class);
                intent.putExtra("type", type);
                if(TextUtils.isEmpty(tokenType)) {
                    intent.putExtra("detail", gson.toJson(mData.get(position)));
                }else {
                    intent.putExtra("detail", gson.toJson(mTrc20Data.get(position)));
                }
                startActivity(intent);
            }
        });
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                fingerprint=null;
                loadData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                loadData();
            }
        });
        if (type.toUpperCase().equals("TRX")&&TextUtils.isEmpty(tokenType)) {
            listview.setAdapter(mAdapter);
        } else {
            listview.setAdapter(mTrc20Adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fingerprint=null;
        loadData();
    }
    private void loadData(){
        if (type.toUpperCase().equals("TRX")&&TextUtils.isEmpty(tokenType)) {
            loadHistory();
        } else {
            loadTrc20History();
        }
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
            intent = new Intent(this, BaseWebViewActivity.class);
            intent.putExtra("title", type.toUpperCase() + getString(R.string.token_detail2));
            intent.putExtra("url", "https://tronscan.io/#/token20/" + tokenType);
            startActivity(intent);
        } else if (i == R.id.transfer) {
            if(walletDBUtil.getWalletInfo().getLevel()==-1){
                ToastUtil.showToast(getString(R.string.wallet_type_error));
                return;
            }
            if (NetUtils.isNetworkConnected(this)) {
                intent = new Intent(this, TransferTrxActivity.class);
                intent.putExtra("amountStr", "0");
                intent.putExtra("tokenType", tokenType);
                intent.putExtra("toAddress", "");
                intent.putExtra("tokenName", type);
                intent.putExtra("gasCount", gascount);
                intent.putExtra("decimal", decimal);
                startActivity(intent);
            } else {
                ToastUtil.showToast(getString(R.string.connect_failuer_toast));
            }

        } else if (i == R.id.collect_money) {
            CollectMoneyActivity.startCollectMoneyActivity(this, WalletUtil.TRX_COIN,tokenType,decimal);
        } else {
        }
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap();
        params.put("limit", 20);
        if (!TextUtils.isEmpty(fingerprint)) {
            params.put("fingerprint", "");
        }
        params.put("search_internal", true);
        mApi.getTransactions(walletDBUtil.getWalletInfo().getAllAddress(), params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseTrxBean>(this) {
                    @Override
                    public void onNexts(BaseTrxBean baseEntity) {
                        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        if (baseEntity.isSuccess()) {
                            List<TrxTransferHistoryBean> historyBeans=gson.fromJson(gson.toJson(baseEntity.getData()),new TypeToken<List<TrxTransferHistoryBean>>(){}.getType());
                            if(TextUtils.isEmpty(fingerprint)){
                                mData.clear();
                            }
                            if(null==historyBeans||historyBeans.size()<1){
                                nodata.setVisibility(View.VISIBLE);
                                return;
                            }
                            nodata.setVisibility(View.GONE);
                            for(int i=0;i<historyBeans.size();i++){
                                if(null==historyBeans.get(i).getRaw_data()||null==historyBeans.get(i).getRaw_data().getContract()||!historyBeans.get(i).getRaw_data().getContract().get(0).getType().equals("TransferContract")){
                                    historyBeans.remove(i);
                                    i--;
                                }
                            }
                            mData.addAll(historyBeans);
                            if(!TextUtils.isEmpty(baseEntity.getMeta().getFingerprint())){
                                fingerprint=baseEntity.getMeta().getFingerprint();
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getError());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                        refreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                });
    }

    private void loadTrc20History() {
        Map<String, Object> params = new TreeMap();
        params.put("limit", 20);
        if (!TextUtils.isEmpty(fingerprint)) {
            params.put("fingerprint", fingerprint);
        }
        params.put("contract_address", tokenType);
        mApi.getTrc20Transactions(walletDBUtil.getWalletInfo().getAllAddress(), params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseTrxBean>(this) {
                    @Override
                    public void onNexts(BaseTrxBean baseEntity) {
                        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        if (baseEntity.isSuccess()) {
                            List<TrxTrc20TransferHistoryBean> historyBeans=gson.fromJson(gson.toJson(baseEntity.getData()),new TypeToken<List<TrxTrc20TransferHistoryBean>>(){}.getType());
                            if(TextUtils.isEmpty(fingerprint)){
                                mTrc20Data.clear();
                            }
                            mTrc20Data.addAll(historyBeans);
                            if(!TextUtils.isEmpty(baseEntity.getMeta().getFingerprint())){
                                fingerprint=baseEntity.getMeta().getFingerprint();
                            }
                            mTrc20Adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getError());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                        refreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                });
    }
}
