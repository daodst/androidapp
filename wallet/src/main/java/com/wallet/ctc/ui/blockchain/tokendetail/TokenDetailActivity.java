

package com.wallet.ctc.ui.blockchain.tokendetail;

import android.os.Build;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.TokenDetailBean;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.my.view.CircularImage;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TokenDetailActivity extends BaseActivity {
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.token_name)
    TextView tokenName;
    @BindView(R2.id.token_suoxie)
    TextView tokenSuoxie;
    @BindView(R2.id.creat_token_time)
    TextView creatTokenTime;
    @BindView(R2.id.creat_token_zongliang)
    TextView creatTokenZongliang;
    @BindView(R2.id.creat_token_yuwa)
    TextView creatTokenYuwa;
    @BindView(R2.id.creat_token_kuanggong)
    TextView creatTokenKuanggong;
    @BindView(R2.id.creat_token_fabi)
    TextView creatTokenFabi;
    @BindView(R2.id.creat_token_pos)
    TextView creatTokenPos;
    @BindView(R2.id.creat_token_tongsuo)
    TextView creatTokenTongsuo;
    @BindView(R2.id.creat_token_guanwang)
    TextView creatTokenGuanwang;
    @BindView(R2.id.img_head_logo)
    CircularImage imgHeadLogo;
    private String token;
    private BlockChainApi mApi = new BlockChainApi();
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private String logo;
    private int type=0;

    @Override
    public int initContentView() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
        }
        token = getIntent().getStringExtra("tokenName");
        logo = getIntent().getStringExtra("logo");
        type=getIntent().getIntExtra("type",0);
        LogUtil.d(logo+"   ");
        return R.layout.activity_token_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        findViewById(R.id.topRelativeLayout).setPadding(0,Eyes.getStatusBarHeight(this),0,0);

    }

    @Override
    public void initData() {
        getTokenDetail();
    }

    private void getTokenDetail() {
        Map<String, Object> params = new TreeMap();
        params.put("cc", token);
        mApi.getTokenDetail(params,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            TokenDetailBean bean=gson.fromJson(gson.toJson(baseEntity.getData()),TokenDetailBean.class);
                            if(null==logo||logo.length()<5){
                                if(type== WalletUtil.DM_COIN){
                                    GlideUtil.showImg(TokenDetailActivity.this,R.mipmap.dm_logo, imgHeadLogo);
                                }else if(type== WalletUtil.MCC_COIN){
                                    GlideUtil.showImg(TokenDetailActivity.this,R.mipmap.mcc_logo, imgHeadLogo);
                                }else if(type== WalletUtil.OTHER_COIN){
                                    GlideUtil.showImg(TokenDetailActivity.this,R.mipmap.llq_other, imgHeadLogo);
                                }

                            }else {
                                GlideUtil.showImg(TokenDetailActivity.this, logo,imgHeadLogo);
                            }
                            tokenName.setText(bean.getName());
                            tokenSuoxie.setText(bean.getCc().toUpperCase());
                            creatTokenTime.setText(bean.getPublishTime());
                            creatTokenZongliang.setText(bean.getTotal());
                            creatTokenYuwa.setText(bean.getAward());
                            creatTokenPos.setText(bean.getMineral()+"%");
                            creatTokenGuanwang.setText(bean.getUrl());
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    @OnClick(R2.id.img_back)
    public void onViewClicked() {
        finish();
    }

}
