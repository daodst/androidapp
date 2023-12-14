

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
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransactionNewEthRecordBean;
import com.wallet.ctc.model.blockchain.WeekAssertBackEntity;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.EthTokenDetailActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionEthRecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransferEthActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferEthDetailActivity;
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
import io.reactivex.android.schedulers.AndroidSchedulers;



public class AssetsEthDetailActivity extends BaseActivity {

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
    private TransactionEthRecordAdapter mAdapter;
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
    private List<TransactionNewEthRecordBean> mData = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();
    private List<WeekAssertBackEntity> trandBeans = new ArrayList<>();
    private int walletType;
    private String allAddress;

    @Override
    public int initContentView() {
        return R.layout.activity_ethassetsdetail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        WalletEntity walletEntity=walletDBUtil.getWalletInfo();
        allAddress=walletEntity.getAllAddress().toLowerCase();
        walletType=walletEntity.getType();
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
        if((!type.toUpperCase().equals("ETH")&&walletType== WalletUtil.ETH_COIN)
                ||(!type.toLowerCase().equals(getString(R.string.default_dmf_hb))&&walletType== WalletUtil.DMF_COIN)
                ||(!type.toLowerCase().equals(getString(R.string.default_dmf_ba))&&walletType== WalletUtil.DMF_BA_COIN)
                ||(!type.toLowerCase().equals(getString(R.string.default_etf))&&walletType== WalletUtil.ETF_COIN)
                ||(!type.toLowerCase().equals("bnb")&&walletType== WalletUtil.BNB_COIN)
                ||(!type.toLowerCase().equals("ht")&&walletType== WalletUtil.HT_COIN)){
            imgAction.setVisibility(View.VISIBLE);
        }
        mAdapter = new TransactionEthRecordAdapter(this,type);
        mAdapter.bindAddress(walletDBUtil.getWalletInfo().getAllAddress());
        listview.setAdapter(mAdapter);
        
        try {
            String data = mAcache.getAsString("transaction" + type + walletDBUtil.getWalletInfo().getAllAddress());
            mData = gson.fromJson(data, new TypeToken<Map<String, String>>() {
            }.getType());
            mAdapter.bindData(mData);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsEthDetailActivity.this, TransferEthDetailActivity.class);
                mData.get(position).setCoin_name(type);
                intent.putExtra("detail", mData.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dmGetMpchartData();
        loadHistory();
    }

    @Override
    public void initData() {

    }

    private void dmGetMpchartData() {

        Map<String, Object> params = new TreeMap<>();
        params.put("addr", allAddress);
        params.put("con_addr", tokenType);
        mApi.getWeekTransfer(params,walletType).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            List<String> list=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<String>>() {
                            }.getType());
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
                intent = new Intent(this, TransferEthActivity.class);
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
            CollectMoneyActivity.startCollectMoneyActivity(this,walletType,tokenType,decimal);
        } else {
        }
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap();
        if(!TextUtils.isEmpty(tokenType)){
            params.put("con_addr", tokenType);
        }
        params.put("page", 0);
        params.put("addr", walletDBUtil.getWalletInfo().getAllAddress());
        mApi.getTransctionList(params,walletType).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {

                            mData=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<TransactionNewEthRecordBean>>() {
                            }.getType());
                            mAcache.put("transaction" + type +walletDBUtil.getWalletInfo().getAllAddress(), gson.toJson(baseEntity.getData()));
                            if(null==mData||mData.size()<1){
                                nodata.setVisibility(View.VISIBLE);
                            }else {
                                nodata.setVisibility(View.GONE);
                            }
                            mAdapter.bindData(mData);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }
}
