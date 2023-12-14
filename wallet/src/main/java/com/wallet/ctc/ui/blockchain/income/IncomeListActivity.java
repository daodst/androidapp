

package com.wallet.ctc.ui.blockchain.income;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.BaseIncomeBean;
import com.wallet.ctc.model.blockchain.IncomeBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class IncomeListActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.content_view)
    PullableListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    private Gson gson = new Gson();
    private BlockChainApi mApi = new BlockChainApi();
    private IncomeAdapter mAdapter;
    private List<IncomeBean> list = new ArrayList<IncomeBean>();
    private int page = 1;
    private String token;
    private int type;

    @Override
    public int initContentView() {
        token = getIntent().getStringExtra("tokenName");
        type=getIntent().getIntExtra("type",0);
        return R.layout.activity_notice;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.creat_token_shouyi));
        mAdapter = new IncomeAdapter(this);
        mAdapter.bindData(list);
        assetsList.setAdapter(mAdapter);
        refreshView.releaseNotPull();

    }


    @Override
    public void initData() {
        loadNotice();
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {
        } else {
        }
    }

    private void loadNotice() {
        Map<String, Object> params = new TreeMap();
        params.put("page", page);
        params.put("token", token);
        params.put("acc", (walletDBUtil.getWalletInfo().getAllAddress()).toLowerCase());
        mApi.getAwardList(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) throws Exception {
                        if (baseEntity.getStatus() == 1) {
                            String data = gson.toJson(baseEntity.getData());
                            BaseIncomeBean baseIncomeBean=gson.fromJson(data,BaseIncomeBean.class);
                            if(page==1){
                                list.clear();
                            }
                            list.addAll(baseIncomeBean.getAwardlist());
                            if (null != list && list.size() > 0) {
                                mAdapter.bindData(list);
                                mAdapter.notifyDataSetChanged();
                                nodata.setVisibility(View.GONE);
                                refreshView.setVisibility(View.VISIBLE);
                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                refreshView.setVisibility(View.GONE);
                            }

                        } else {
                        }
                    }
                });
    }

}
