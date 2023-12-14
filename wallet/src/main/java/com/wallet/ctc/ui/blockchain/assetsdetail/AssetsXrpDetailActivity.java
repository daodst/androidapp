

package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.TransactionXrpRecordBean;
import com.wallet.ctc.model.blockchain.WeekAssertBackEntity;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.EthTokenDetailActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionXrpRecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransferXrpActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferXrpDetailActivity;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.NetUtils;
import com.wallet.ctc.view.LineChartManager;

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
import common.app.ui.view.PullableScrollView;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class AssetsXrpDetailActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.line_chart1)
    LineChart lineChart1;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    LineChartManager lineChartManager1;
    @BindView(R2.id.listview)
    ListView listview;
    @BindView(R2.id.daib_num)
    TextView daibNum;
    @BindView(R2.id.daib_price)
    TextView daibPrice;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    @BindView(R2.id.scrollView)
    PullableScrollView scrollView;


    private TransactionXrpRecordAdapter mAdapter;
    private Intent intent;
    private String type = "";
    private String tokenType = "";
    private String gascount = "";
    private int decimal = 18;
    private String DbNum;
    private String DbPrice;
    private String Logo;
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    private List<TransactionXrpRecordBean> mData = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();
    private List<WeekAssertBackEntity> trandBeans = new ArrayList<>();

    private int page = 0;

    @Override
    public int initContentView() {
        return R.layout.activity_xrpassetsdetail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAcache = ACache.get(this);
        imgAction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.eth_token_detail));
        type = getIntent().getStringExtra("type");
        DbNum = getIntent().getStringExtra("DbNum");
        DbPrice = getIntent().getStringExtra("DbPrice");
        tokenType = getIntent().getStringExtra("address");
        gascount = getIntent().getStringExtra("gasCount");
        Logo = getIntent().getStringExtra("logo");
        decimal = getIntent().getIntExtra("decimal", 18);
        daibNum.setText(DbNum);
        daibPrice.setText("≈ ¥ " + DbPrice);
        if (DbPrice == null || DbPrice.equals("")) {
            DbPrice = "0";
        }
        lineChartManager1 = new LineChartManager(lineChart1);
        tvTitle.setText(type);
        if(!type.toUpperCase().equals("XRP")){
            imgAction.setVisibility(View.VISIBLE);
        }
        mAdapter = new TransactionXrpRecordAdapter(this,type);
        mAdapter.bindAddress(walletDBUtil.getWalletInfo().getAllAddress());
        listview.setAdapter(mAdapter);
        
        try {
            String data = mAcache.getAsString("transaction" + type + walletDBUtil.getWalletInfo().getAllAddress());
            if (!TextUtils.isEmpty(data)) {
                List<TransactionXrpRecordBean> datas = gson.fromJson(data, new TypeToken<List<TransactionXrpRecordBean>>() {}.getType());
                if (null != datas) {
                    mData.addAll(datas);
                }
            }
            mAdapter.bindData(mData);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsXrpDetailActivity.this, TransferXrpDetailActivity.class);
                mData.get(position).setCoin_name(type);
                intent.putExtra("detail", mData.get(position));
                startActivity(intent);
            }
        });

        
        pullView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 0;
                loadHistory();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                loadHistory();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        dmGetMpchartData();
        page = 0;
        loadHistory();
    }

    @Override
    public void initData() {

    }

    private void dmGetMpchartData() {
        if (!TextUtils.isEmpty(tokenType) && !TextUtils.isEmpty(type)) {
            
            getTokenWeekTransfer();
        } else {
            
            getMainWeekTransfer();
        }


    }

    
    private void getTokenWeekTransfer() {
        String allAddress = walletDBUtil.getWalletInfo().getAllAddress();
        Map<String, Object> params = new TreeMap<>();
        params.put("addr", allAddress);
        params.put("issuer", tokenType);
        params.put("currency", type);
        mApi.getXrpTokenWeekTransfer(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            Map<String,String> dataMap=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<Map<String, String>>() {
                            }.getType());
                            List<String> list = new ArrayList<>();
                            if (null != dataMap && dataMap.size() > 0) {
                                for (Map.Entry<String,String> entry : dataMap.entrySet()) {
                                    list.add(entry.getValue());
                                }
                            }
                            initMpChart(list);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    
    private void getMainWeekTransfer() {
        
        String allAddress = walletDBUtil.getWalletInfo().getAllAddress();
        Map<String, Object> params = new TreeMap<>();
        params.put("addr", allAddress);
        mApi.getXrpWeekTransfer(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            Map<String,String> dataMap=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<Map<String, String>>() {
                            }.getType());
                            List<String> list = new ArrayList<>();
                            if (null != dataMap && dataMap.size() > 0) {
                                for (Map.Entry<String,String> entry : dataMap.entrySet()) {
                                    list.add(entry.getValue());
                                }
                            }
                            initMpChart(list);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }




    private void initMpChart(List<String> list) {
        
        ArrayList<Float> xValues = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            xValues.add((float) i);
        }
        
        int size = list.size();
        if (size < 7) {
            for (int i=0; i < 7-size; i++) {
                list.add(0, "0");
            }
        } else if(size > 7){
            list = list.subList(size-7, size-1);
        }


        
        List<List<Float>> yValues = new ArrayList<>();
        List<Float> yValue = new ArrayList<>();
        for (int j = 0; j < xValues.size() - 1; j++) {
            yValue.add(0f);
        }

        
        List<Float> yy = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String data=list.get(i);
                if(TextUtils.isEmpty(data)){
                    data="0";
                }
                yy.add(Float.parseFloat(data));
            }
        }
        List<Float> yValue2 = new ArrayList<>();
        yValue2.addAll(yy);
        yValues.add(yValue2);
        
        List<Integer> colours = new ArrayList<>();
        colours.add(0xff7C69C0);
        
        List<String> names = new ArrayList<>();
        names.add(getString(R.string.numbers));
        lineChartManager1.showLineChart(xValues, yValues, names, colours);
        lineChartManager1.setDescription("");
    }

    @OnClick({R2.id.tv_back, R2.id.transfer, R2.id.collect_money, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            intent = new Intent(this, EthTokenDetailActivity.class);
            intent.putExtra("tokenName",type);
            intent.putExtra("logo", Logo);
            intent.putExtra("address",tokenType);
            startActivity(intent);

        } else if (i == R.id.transfer) {
            if(walletDBUtil.getWalletInfo().getLevel()==-1){
                ToastUtil.showToast(getString(R.string.wallet_type_error));
                return;
            }
            if (NetUtils.isNetworkConnected(this) || gascount.length() > 4) {
                intent = new Intent(this, TransferXrpActivity.class);
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
            CollectMoneyActivity.startCollectMoneyActivity(this, WalletUtil.XRP_COIN,tokenType,decimal);
        } else {
        }
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap();
        if (page > 0 && null != mData && mData.size() > 0) {
            String last = mData.get(mData.size()-1).getLedger_index();
            params.put("ledger_index", last);
        } else {
            
        }
        params.put("addr", walletDBUtil.getWalletInfo().getAllAddress());

        
        if (!TextUtils.isEmpty(tokenType)) {
            params.put("issuer", tokenType);
        }
        if (!TextUtils.isEmpty(type)) {
            params.put("currency", type.toLowerCase());
        }
        mApi.getXrpTransctionList(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            if (null != pullView) {
                                if (page == 0) {
                                    pullView.refreshFinish(PullToRefreshLayout.SUCCEED);
                                } else {
                                    pullView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                }

                            }

                            List<TransactionXrpRecordBean> datas = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<TransactionXrpRecordBean>>() {
                            }.getType());
                            if (page == 0) {
                                mData.clear();
                            } else {
                                if (null == datas || datas.size() == 0) {
                                    page--;
                                    ToastUtil.showToast(getString(R.string.nomore));
                                }
                            }
                            if (null != datas) {
                                mData.addAll(datas);
                            }
                            if (page == 0) {
                                mAcache.put("transaction" + type +walletDBUtil.getWalletInfo().getAllAddress(), gson.toJson(baseEntity.getData()));
                            }
                            if(null==mData){
                                nodata.setVisibility(View.VISIBLE);
                            }else {
                                nodata.setVisibility(View.GONE);
                            }
                            mAdapter.bindData(mData);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (null != pullView) {
                                if (page == 0) {
                                    pullView.refreshFinish(PullToRefreshLayout.FAIL);
                                } else {
                                    pullView.loadmoreFinish(PullToRefreshLayout.FAIL);
                                }

                            }
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }
}
