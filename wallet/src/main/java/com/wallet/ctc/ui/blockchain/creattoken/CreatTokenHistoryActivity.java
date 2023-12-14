

package com.wallet.ctc.ui.blockchain.creattoken;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.CreatEthEntity;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.CheckHashBean;
import com.wallet.ctc.ui.blockchain.tokendetail.EthTokenDetailActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.TokenDetailActivity;

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
import io.reactivex.android.schedulers.AndroidSchedulers;



public class CreatTokenHistoryActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.content_view)
    ListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    private CreatTokenHistoryAdapter mAdapter;
    private MeApi mApi = new MeApi();
    private List<CreatEthEntity> list = new ArrayList<>();
    private int page = 1;
    private int ref = 0;
    private Intent intent;
    private Gson gson = new Gson();
    @Override
    public int initContentView() {
        return R.layout.activity_notice;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.release_record));
        mAdapter = new CreatTokenHistoryAdapter(this);
        assetsList.setAdapter(mAdapter);
        pullView.releaseNotPull();
        pullView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                ref = 0;
                initData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ref = 1;
                initData();
            }
        });
        assetsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CreatEthEntity bean = list.get(position);
                if (bean.getType() == WalletUtil.DM_COIN || bean.getType() == WalletUtil.MCC_COIN || bean.getType() == WalletUtil.OTHER_COIN) {
                    intent = new Intent(CreatTokenHistoryActivity.this, TokenDetailActivity.class);
                    intent.putExtra("tokenName", bean.getShort_name());
                    intent.putExtra("logo", bean.getImg_path());
                    intent.putExtra("type", bean.getType());
                    startActivity(intent);
                } else if (bean.getType() == WalletUtil.ETH_COIN) {
                    if (bean.getStatu() == 1) {
                        intent = new Intent(CreatTokenHistoryActivity.this, EthTokenDetailActivity.class);
                        intent.putExtra("tokenName", bean.getShort_name().toUpperCase());
                        intent.putExtra("address", bean.getAddress());
                        intent.putExtra("logo", bean.getImg_path());
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(getString(R.string.currency_processing));
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        list = DBManager.getInstance(this).queryCreatHistory(WalletDBUtil.USER_ID);
        if(null==list||list.size()==0){
            nodata.setVisibility(View.VISIBLE);
            pullView.setVisibility(View.GONE);
            return;
        }
        mAdapter.bindData(list);
        nodata.setVisibility(View.GONE);
        pullView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getStatu()==0){
                getStatu(list.get(i));
            }
        }
    }

    @OnClick({R2.id.tv_back})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else {
        }
    }

    
    private void getStatu(CreatEthEntity creatEthEntity) {
        try {
            mLoadingDialog.show();
            Map<String, Object> params = new TreeMap();
            params.put("hash", creatEthEntity.getHexValue());
            mApi.getTranferHash(params,creatEthEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<BaseEntity>(CreatTokenHistoryActivity.this) {
                        @Override
                        public void onNexts(BaseEntity baseEntity) {
                            mLoadingDialog.dismiss();
                            if (baseEntity.getStatus() == 1 ) {
                                try{
                                    CheckHashBean mBean=gson.fromJson(gson.toJson(baseEntity.getData()),CheckHashBean.class);
                                    if(mBean.getStatus().equals("0x1")&&null!=mBean.getContractAddress()&& !TextUtils.isEmpty(mBean.getContractAddress())){
                                        creatEthEntity.setAddress(mBean.getContractAddress());
                                        creatEthEntity.setStatu(1);
                                        DBManager.getInstance(CreatTokenHistoryActivity.this).updateCreatHistory(creatEthEntity);
                                        walletDBUtil.delAssetsWalletAll(creatEthEntity.getShort_name());
                                        AssertBean assbean = new AssertBean("", creatEthEntity.getShort_name(), creatEthEntity.getFull_name(), creatEthEntity.getAddress(), "", creatEthEntity.getDecimal(), 1, 2);
                                        assbean.setWalletAddress("");
                                        walletDBUtil.addAssets(assbean);
                                        AssertBean assbean2 = new AssertBean("", creatEthEntity.getShort_name(), creatEthEntity.getFull_name(), creatEthEntity.getAddress(), "", creatEthEntity.getDecimal(), 1, 2);
                                        assbean2.setWalletAddress(creatEthEntity.getWalletAddress());
                                        walletDBUtil.addAssets(assbean2);
                                    }else if(mBean.getStatus().equals("0x2")){
                                        creatEthEntity.setStatu(2);
                                        DBManager.getInstance(CreatTokenHistoryActivity.this).updateCreatHistory(creatEthEntity);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }catch (Exception e){

                                }
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }
}
