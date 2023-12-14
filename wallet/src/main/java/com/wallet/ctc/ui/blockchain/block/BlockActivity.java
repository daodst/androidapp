

package com.wallet.ctc.ui.blockchain.block;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class BlockActivity extends BaseActivity {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.div_view)
    View divView;
    @BindView(R2.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R2.id.block_sum)
    TextView blockSum;
    @BindView(R2.id.this_block_sum)
    TextView thisBlockSum;
    @BindView(R2.id.last_sync_time)
    TextView lastSyncTime;
    @BindView(R2.id.submitBtn)
    TextView submitBtn;

    private Intent intent;
    private BlockChainApi mApi=new BlockChainApi();
    private Gson gson=new Gson();
    private WalletEntity mWallet;
    private int type=0;

    @Override
    public int initContentView() {
        return R.layout.activity_block;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(R.string.block);
        type=getIntent().getIntExtra("type",0);
    }

    @Override
    public void initData() {
        Map<String, Object> params2 = new TreeMap();
        mApi.getBlockNum(params2,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            blockSum.setText(baseEntity.getData().toString());
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action, R2.id.submitBtn})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {
            intent = new Intent(this, BlockDetailActivity.class);
            startActivity(intent);

        } else if (i == R.id.submitBtn) {
            intent = new Intent(this, BlockDetailActivity.class);
            intent.putExtra("qusum", Integer.parseInt(blockSum.getText().toString()));
            intent.putExtra("type", type);
            startActivity(intent);

        } else {
        }
    }

    public void tongbu() {
        mLoadingDialog.show();
        Map<String, Object> params2 = new TreeMap();
        params2.put("begin","0");
        params2.put("end","3000");
        mApi.getBlockData(params2,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            Date d = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String dateNowStr = sdf.format(d);
                            lastSyncTime.setText(dateNowStr);
                            mAcache.put(mWallet.getAllAddress()+"time",dateNowStr);
                            LogUtil.d(""+gson.toJson(baseEntity.getData()));
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }
}
