

package com.wallet.ctc.ui.blockchain.block;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
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



public class BlockDetailActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.current_block)
    TextView currentBlock;
    @BindView(R2.id.edit_qukuai)
    EditText editQukuai;
    @BindView(R2.id.creat_time)
    TextView creatTime;
    @BindView(R2.id.content_view)
    PullableListView contentView;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    private BlockAdapter mAdapter;
    private BlockChainApi mApi = new BlockChainApi();
    private Gson gson = new Gson();
    private int qusum=1;
    private int type=0;

    @Override
    public int initContentView() {
        return R.layout.activity_block_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type=getIntent().getIntExtra("type",0);
        mAdapter = new BlockAdapter(this);
        tvTitle.setText(R.string.block_details);
        contentView.setAdapter(mAdapter);
        qusum=getIntent().getIntExtra("qusum",1);
        currentBlock.setText(qusum+"");
        refreshView.releaseNotPull();
    }

    @Override
    public void initData() {
        mLoadingDialog.show();
        LogUtil.d(""+qusum);
        Map<String, Object> params2 = new TreeMap();
        params2.put("args", qusum+"");
        mApi.getBlockDetail(params2,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            String data=gson.toJson(baseEntity.getData());
                            if(null==data||data.length()<4){
                                mAdapter.bindData(new ArrayList<>());
                                mAdapter.notifyDataSetChanged();
                                return;
                            }
                            BaseBlockDetailBean baseBlockDetailBean = gson.fromJson(gson.toJson(baseEntity.getData()), BaseBlockDetailBean.class);
                            mAdapter.bindData(baseBlockDetailBean.getLedgers());
                            mAdapter.notifyDataSetChanged();
                            creatTime.setText(baseBlockDetailBean.getTimestamp());
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                    }
                });
    }

    @OnClick({R2.id.tv_back, R2.id.seach, R2.id.shangyikuai, R2.id.submitBtn})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.seach) {
            String key = editQukuai.getText().toString();
            if (TextUtils.isEmpty(key)) {
                return;
            }
            qusum = Integer.parseInt(key);
            if (qusum < 1) {
                qusum = 1;
                editQukuai.setText("1");
            }
            initData();

        } else if (i == R.id.shangyikuai) {
            if (qusum > 1) {
                qusum--;
                editQukuai.setText(qusum + "");
                initData();
            } else {
                editQukuai.setText(qusum + "");
                ToastUtil.showToast(getString(R.string.nomore_kuai));
            }

        } else if (i == R.id.submitBtn) {
            int sum = Integer.parseInt(currentBlock.getText().toString());
            if (qusum <= sum - 1) {
                qusum++;
                editQukuai.setText(qusum + "");
                initData();
            } else {
                editQukuai.setText(qusum + "");
                ToastUtil.showToast(getString(R.string.nomore_kuai));
            }

        }
    }
}
