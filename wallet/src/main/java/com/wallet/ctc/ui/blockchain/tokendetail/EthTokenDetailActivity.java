

package com.wallet.ctc.ui.blockchain.tokendetail;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.EthTokenDetailBean;
import com.wallet.ctc.util.GlideUtil;

import java.util.List;
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



public class EthTokenDetailActivity extends BaseActivity {
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
    @BindView(R2.id.website_name)
    TextView websiteName;
    @BindView(R2.id.website)
    TextView website;
    @BindView(R2.id.email_name)
    TextView emailName;
    @BindView(R2.id.email)
    TextView email;
    @BindView(R2.id.whitepaper_name)
    TextView whitepaperName;
    @BindView(R2.id.whitepaper)
    TextView whitepaper;
    @BindView(R2.id.yingwen_name)
    TextView yingwenName;
    @BindView(R2.id.yingwen)
    TextView yingwen;
    @BindView(R2.id.zhongwen_name)
    TextView zhongwenName;
    @BindView(R2.id.zhongwen)
    TextView zhongwen;
    @BindView(R2.id.blog_name)
    TextView blogName;
    @BindView(R2.id.blog)
    TextView blog;
    @BindView(R2.id.twitter_name)
    TextView twitterName;
    @BindView(R2.id.twitter)
    TextView twitter;
    @BindView(R2.id.telegram_name)
    TextView telegramName;
    @BindView(R2.id.telegram)
    TextView telegram;
    @BindView(R2.id.github_name)
    TextView githubName;
    @BindView(R2.id.github)
    TextView github;
    @BindView(R2.id.facebook_name)
    TextView facebookName;
    @BindView(R2.id.facebook)
    TextView facebook;
    @BindView(R2.id.reddit_name)
    TextView redditName;
    @BindView(R2.id.reddit)
    TextView reddit;
    @BindView(R2.id.slack_name)
    TextView slackName;
    @BindView(R2.id.slack)
    TextView slack;
    @BindView(R2.id.medium_name)
    TextView mediumName;
    @BindView(R2.id.medium)
    TextView medium;
    @BindView(R2.id.eth_name)
    TextView ethName;
    @BindView(R2.id.eth)
    TextView eth;
    @BindView(R2.id.usd_name)
    TextView usdName;
    @BindView(R2.id.usd)
    TextView usd;
    @BindView(R2.id.btc_name)
    TextView btcName;
    @BindView(R2.id.btc)
    TextView btc;
    private String token;
    private String address;
    private MeApi mApi = new MeApi();
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private String logo;
    private int wallettype;

    @Override
    public int initContentView() {
        Eyes.setTranslucent(this);
        token = getIntent().getStringExtra("tokenName");
        logo = getIntent().getStringExtra("logo");
        address = getIntent().getStringExtra("address");
        wallettype=getIntent().getIntExtra("wallettype", WalletUtil.ETH_COIN);
        return R.layout.activity_ethtoken_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        findViewById(R.id.topRelativeLayout).setPadding(0, Eyes.getStatusBarHeight(this), 0, 0);
        GlideUtil.showImg(this, logo, imgHeadLogo);

    }

    @Override
    public void initData() {
        getTokenDetail();
    }

    private void getTokenDetail() {
        mLoadingDialog.show();
        Map<String, Object> params2 = new TreeMap();
        params2.put("name", address);
        mApi.seachToken(params2,wallettype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            List<EthTokenDetailBean> list = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<EthTokenDetailBean>>() {
                            }.getType());
                            if (null != list && list.size() > 0) {
                                EthTokenDetailBean mBean = list.get(0);
                                tokenName.setText(mBean.getName());
                                tokenSuoxie.setText(mBean.getSymbol());
                                creatTokenTime.setText(mBean.getPublished_on());
                                if(!TextUtils.isEmpty(mBean.getWebsite())){
                                   websiteName.setVisibility(View.VISIBLE);
                                   website.setText(mBean.getWebsite());
                                   website.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getEmail())){
                                    emailName.setVisibility(View.VISIBLE);
                                    email.setText(mBean.getEmail());
                                    email.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getWhitepaper())){
                                    whitepaperName.setVisibility(View.VISIBLE);
                                    whitepaper.setText(mBean.getWhitepaper());
                                    whitepaper.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getOverview().getEn())){
                                    yingwenName.setVisibility(View.VISIBLE);
                                    yingwen.setText(mBean.getOverview().getEn());
                                    yingwen.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getOverview().getZh())){
                                    zhongwenName.setVisibility(View.VISIBLE);
                                    zhongwen.setText(mBean.getOverview().getZh());
                                    zhongwen.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getBlog())){
                                    blogName.setVisibility(View.VISIBLE);
                                    blog.setText(mBean.getLink().getBlog());
                                    blog.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getTwitter())){
                                    twitterName.setVisibility(View.VISIBLE);
                                    twitter.setText(mBean.getLink().getTwitter());
                                    twitter.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getTelegram())){
                                    telegramName.setVisibility(View.VISIBLE);
                                    telegram.setText(mBean.getLink().getTelegram());
                                    telegram.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getGithub())){
                                    githubName.setVisibility(View.VISIBLE);
                                    github.setText(mBean.getLink().getGithub());
                                    github.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getFacebook())){
                                    facebookName.setVisibility(View.VISIBLE);
                                    facebook.setText(mBean.getLink().getFacebook());
                                    facebook.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getReddit())){
                                    redditName.setVisibility(View.VISIBLE);
                                    reddit.setText(mBean.getLink().getReddit());
                                    reddit.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getSlack())){
                                    slackName.setVisibility(View.VISIBLE);
                                    slack.setText(mBean.getLink().getSlack());
                                    slack.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getLink().getMedium())){
                                    mediumName.setVisibility(View.VISIBLE);
                                    medium.setText(mBean.getLink().getMedium());
                                    medium.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getInitial_price().getETH())){
                                    ethName.setVisibility(View.VISIBLE);
                                    eth.setText(mBean.getInitial_price().getETH());
                                    eth.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getInitial_price().getUSD())){
                                    usdName.setVisibility(View.VISIBLE);
                                    usd.setText(mBean.getInitial_price().getUSD());
                                    usd.setVisibility(View.VISIBLE);
                                }
                                if(!TextUtils.isEmpty(mBean.getInitial_price().getBTC())){
                                    btcName.setVisibility(View.VISIBLE);
                                    btc.setText(mBean.getInitial_price().getBTC());
                                    btc.setVisibility(View.VISIBLE);
                                }
                            }
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
