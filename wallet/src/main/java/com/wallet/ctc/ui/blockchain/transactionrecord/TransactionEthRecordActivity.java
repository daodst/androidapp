

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransactionNewEthRecordBean;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferEthDetailActivity;

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



public class TransactionEthRecordActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.content_view)
    PullableListView contentView;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    private TransactionEthRecordAdapter mAdapter;
    private List<TransactionNewEthRecordBean> list = new ArrayList<>();
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    private int page=0;
    private int ref=0;
    private WalletEntity mWallet;
    private String type = "";
    private String tokenType = "";
    @Override
    public int initContentView() {
        return R.layout.activity_transaction_record;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type = getIntent().getStringExtra("type");
        tokenType = getIntent().getStringExtra("address");
        tvTitle.setText(getString(R.string.transaction_record));
        mAdapter = new TransactionEthRecordAdapter(this,type);
        mAdapter.bindData(list);
        contentView.setAdapter(mAdapter);
        mWallet=walletDBUtil.getWalletInfo();
        mAdapter.bindAddress(mWallet.getAllAddress());
        pullView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page=0;
                ref=0;
                loadHistory();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ref=1;
                loadHistory();
            }
        });
        contentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(TransactionEthRecordActivity.this, TransferEthDetailActivity.class);
                list.get(position).setCoin_name(type);
                intent.putExtra("detail",list.get(position));
                startActivity(intent);

            }
        });
    }

    @Override
    public void initData() {
        loadHistory();
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap();
        params.put("page", page);
        if(!type.toLowerCase().equals("eth")){
            params.put("con_addr", tokenType);
        }
        params.put("addr",mWallet.getAllAddress());
        mApi.getTransctionList(params,mWallet.getType()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (ref == 0) {
                            pullView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        } else {
                            pullView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (baseEntity.getStatus() == 1) {
                            if (page == 0) {
                                list.clear();
                            }
                            List<TransactionNewEthRecordBean> data = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<TransactionNewEthRecordBean>>() {
                            }.getType());
                            if (data == null || data.size() < 1) {
                                if (page > 1) {
                                    ToastUtil.showToast(getString(R.string.nomore));
                                }
                            } else {
                                page++;
                            }
                            list.addAll(data);
                            if(data.size()>0){
                                pullView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }else {
                                pullView.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null==pullView){
                            return;
                        }
                        if (ref == 0) {
                            pullView.refreshFinish(PullToRefreshLayout.FAIL);
                        } else {
                            pullView.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                });
    }

    @OnClick(R2.id.tv_back)
    public void onViewClicked() {
        finish();
    }

}
