

package com.wallet.ctc.ui.blockchain.transactionrecord;

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
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosTransRecordsBean;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferDetailActivity;

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



public class TransactionRecordActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.content_view)
    PullableListView contentView;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    private TransactionRecordAdapter mAdapter;
    private List<TransactionRecordBean> list = new ArrayList<>();
    private Gson gson = new Gson();
    private BlockChainApi mApi = new BlockChainApi();
    private int page = 1;
    private int ref = 0;
    private WalletEntity mWallet;
    private String address;
    private int type=0;
    private String tokenName;

    public String getWalletAddress() {
        if (null == mWallet) {
            return "";
        }
        if (type == WalletUtil.MCC_COIN) {
            return mWallet.getAllAddress2();
        } else {
            return mWallet.getAllAddress();
        }
    }

    @Override
    public int initContentView() {
        return R.layout.activity_transaction_record;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type=getIntent().getIntExtra("type",0);
        tokenName=getIntent().getStringExtra("tokenName");
        tvTitle.setText(getString(R.string.transaction_record));
        imgAction.setVisibility(View.GONE);
        mAdapter = new TransactionRecordAdapter(this);
        mAdapter.bindData(list,type);
        contentView.setAdapter(mAdapter);
        mWallet = walletDBUtil.getWalletInfo();
        mAdapter.bindAddress(getWalletAddress());
        pullView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                ref = 0;
                loadHistory();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ref = 1;
                loadHistory();
            }
        });
        contentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TransactionRecordActivity.this, TransferDetailActivity.class);
                intent.putExtra("detail", list.get(position));
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        address=getWalletAddress();
        loadHistory();
    }


    private void loadMccHistory() {
        mApi.getEvmosHistory(page, address, tokenName).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosTransRecordsBean>(this) {
                    @Override
                    public void onNexts(EvmosTransRecordsBean baseEntity) {
                        if (ref == 0) {
                            pullView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        } else {
                            pullView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (baseEntity.isSuccess()) {
                            if (page == 1) {
                                list.clear();
                            }
                            List<TransactionRecordBean> data = new ArrayList<>();
                            if (baseEntity.data != null && baseEntity.data.transaction != null) {
                                data = baseEntity.data.transaction;
                            }
                            if (data == null || data.size() < 1) {
                                if (page > 1) {
                                    ToastUtil.showToast(getString(R.string.nomore));
                                }
                            } else {
                                page++;
                            }
                            list.addAll(data);
                            if (list == null || list.size() == 0) {
                                nodata.setVisibility(View.VISIBLE);
                                pullView.setVisibility(View.GONE);
                            }else {
                                nodata.setVisibility(View.GONE);
                                pullView.setVisibility(View.VISIBLE);
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

    private void loadHistory() {
        if (type == WalletUtil.MCC_COIN) {
            loadMccHistory();
            return;
        }
        Map<String, Object> params = new TreeMap();
        params.put("method", "history");
        params.put("page", page);
        params.put("acc", address);
        params.put("number","10");
        mApi.getTransList(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (ref == 0) {
                            pullView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        } else {
                            pullView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (baseEntity.getStatus() == 1) {
                            if (page == 1) {
                                list.clear();
                            }
                            String datas = gson.toJson(baseEntity.getData());
                            if (TextUtils.isEmpty(datas) ||datas.equals("null")) {
                                return;
                            }
                            List<TransactionRecordBean> data = gson.fromJson(datas, new TypeToken<List<TransactionRecordBean>>() {
                            }.getType());
                            if (data == null || data.size() < 1) {
                                if (page > 1) {
                                    ToastUtil.showToast(getString(R.string.nomore));
                                }
                            } else {
                                page++;
                            }
                            list.addAll(data);
                            if (list == null || list.size() == 0) {
                                nodata.setVisibility(View.VISIBLE);
                                pullView.setVisibility(View.GONE);
                            }else {
                                nodata.setVisibility(View.GONE);
                                pullView.setVisibility(View.VISIBLE);
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

    @OnClick({R2.id.tv_back, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            Intent intent = new Intent(TransactionRecordActivity.this, ChooseWalletActivity.class);
            intent.putExtra("address", address);
            startActivityForResult(intent, 1000);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&null!=data){
            address=data.getStringExtra("address");
            page=1;
            loadHistory();
        }
    }
}
