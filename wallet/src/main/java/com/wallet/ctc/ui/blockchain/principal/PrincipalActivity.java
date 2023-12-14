

package com.wallet.ctc.ui.blockchain.principal;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.DiyaBean;
import com.wallet.ctc.view.listview.NoScrollListView;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class PrincipalActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.dailinshouyi)
    TextView dailinshouyi;
    @BindView(R2.id.zongbenjin)
    TextView zongbenjin;
    @BindView(R2.id.shengyubenjin)
    TextView shengyubenjin;
    @BindView(R2.id.benjinjilu_list)
    NoScrollListView benjinjiluList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.lingqubenjin)
    TextView lingqubenjin;
    private String token;
    private BlockChainApi mApi=new BlockChainApi();
    private Gson gson=new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private DiyaBean mData;
    private int type;

    @Override
    public int initContentView() {
        mData = (DiyaBean)getIntent().getSerializableExtra("data");
        type=getIntent().getIntExtra("type",0);
        return R.layout.activity_principal;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(R.string.creat_token_linbenjin);
        tvAction.setText(R.string.creat_token_shouyi);
        Drawable top = ContextCompat.getDrawable(this, R.mipmap.shouyijilu);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        tvAction.setCompoundDrawables(top, null, null, null);
        tvAction.setVisibility(View.VISIBLE);
        dailinshouyi.setText(mData.getDraw_principle().toPlainString());
        zongbenjin.setText(mData.getPrinciple().toPlainString());
        shengyubenjin.setText(mData.getLeft_principle().toPlainString());
    }

    @Override
    public void initData() {
        Map<String, Object> params = new TreeMap();
        params.put("id", mData.getId());
        mLoadingDialog.show();
        mApi.getAwardList(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            String datas = gson.toJson(baseEntity.getData());
                            if (TextUtils.isEmpty(datas) || datas.length() < 4 || datas.equals("null")) {
                                return;
                            }

                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadingDialog.dismiss();
                    }
                });
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action, R2.id.lingqubenjin})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {
        } else if (i == R.id.lingqubenjin) {
            ToastUtil.showToast("");

        }
    }

    private void getWithdraw(String id) {
        Map<String, Object> params = new TreeMap();
        params.put("id", id);
        mApi.getWithdraw(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {

                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }
}
