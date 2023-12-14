

package com.wallet.ctc.ui.blockchain.transactionrecord;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferDetailActivity;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TransactionRecordFragment extends BaseFragment {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.content_view)
    PullableListView contentView;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    private TransactionRecordAdapter mAdapter;
    private List<TransactionRecordBean> list = new ArrayList<>();
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private BlockChainApi mApi = new BlockChainApi();
    private int page = 1;
    private int ref = 0;
    private WalletEntity mWallet;
    protected Unbinder mUnbinder;
    private String address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_record, container, false);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int intgetcolor = ContextCompat.getColor(getActivity(), R.color.default_titlebar_bg_color);
                Eyes.addStatusBar(getActivity(), (ViewGroup) view, intgetcolor);
            } else {
                int intgetcolor = 0x30ffffff;
                Eyes.addStatusBar(getActivity(), (ViewGroup) view, intgetcolor);
            }
        }
        mUnbinder = ButterKnife.bind(this, view);
        tvBack.setVisibility(View.GONE);
        initUiAndListener();
        return view;
    }

    public void initUiAndListener() {
        tvTitle.setText(getString(R.string.transaction_record));
        imgAction.setImageResource(R.mipmap.motrans);
        imgAction.setVisibility(View.VISIBLE);
        imgAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseWalletActivity.class);
                intent.putExtra("address", address);
                startActivityForResult(intent, 1000);
            }
        });
        mAdapter = new TransactionRecordAdapter(getActivity());
        mAdapter.bindData(list, 4);
        contentView.setAdapter(mAdapter);
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
                Intent intent = new Intent(getActivity(), TransferDetailActivity.class);
                intent.putExtra("detail", list.get(position));
                startActivity(intent);

            }
        });
    }

    @Override
    public void getData() {
        initData();
    }

    public void initData() {
        page = 1;
        if (null == context) {
            return;
        }
        mWallet = WalletDBUtil.getInstent(getActivity()).getWalletInfo();
        address = mWallet.getAllAddress();
        mAdapter.bindAddress(mWallet.getAllAddress());
        loadHistory();
    }

    private void loadHistory() {
        Map<String, Object> params = new TreeMap();
        params.put("method", "history");
        params.put("number", 50);
        params.put("page", page);
        params.put("acc", address);
        mApi.getTransList(params, 4).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(getActivity()) {
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
                            if (TextUtils.isEmpty(datas) || datas.equals("null")) {
                                return;
                            }
                            LogUtil.d(datas);
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
                            } else {
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
                        if (null == pullView) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("1111111");
        if (resultCode == RESULT_OK) {
            address = data.getStringExtra("address");
            LogUtil.d(address);
            page = 1;
            loadHistory();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
