

package com.wallet.ctc.ui.me.protocol;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class ProtocolActivity extends BaseActivity {
    @BindView(R2.id.baseweb_webview)
    WebView mWebView;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.submit)
    TextView submit;
    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    private ACache aCache;
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    public final String HTML_STYLE = "<style type=\"text/css\">\n" +
            "img,iframe,video {height:auto; max-width:100%; word-break:break-all;} \n</style>\n";

    @Override
    public int initContentView() {
        return R.layout.activity_protocol;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        aCache=ACache.get(this);
        tvTitle.setText(R.string.protocol);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        settings.setUseWideViewPort(false);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setDomStorageEnabled(true);
        
        settings.setLoadWithOverviewMode(true);
        
        mWebView.requestFocusFromTouch();
        
        
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                
                
                return true;
            }
        });
        String url=aCache.getAsString("protlcolurl");
        if(null!=url&&!TextUtils.isEmpty(url)){
            mWebView.loadDataWithBaseURL(Constants.BASE_URL, HTML_STYLE + url, "text/html", "UTF-8", null);
        }
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    submit.setEnabled(true);
                    submit.setBackgroundResource(R.drawable.protocol_btn_bg);
                } else {
                    submit.setEnabled(false);
                    submit.setBackgroundColor(0xffc1c1c1);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingPrefUtil.setgetAgreen(ProtocolActivity.this,true);
                Intent intent=new Intent(ProtocolActivity.this, BlockchainLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getUrl();

    }

    @Override
    public void initData() {

    }

    private void getUrl() {
        Map<String, Object> params2 = new TreeMap();
        params2.put("type", "service");
        mApi.getArticle(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if(baseEntity.getStatus()==1) {
                            String url = baseEntity.getData().toString();
                            aCache.put("protlcolurl", url);
                            mWebView.loadDataWithBaseURL(Constants.BASE_URL, HTML_STYLE + url, "text/html", "UTF-8", null);
                        }
                    }
                });
    }
}
