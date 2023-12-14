

package com.app.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.R;

import common.app.mall.BaseActivity;
import common.app.model.net.okhttps.BusinessResponse;
import common.app.model.net.okhttps.Common;
import common.app.model.net.okhttps.OkHttps;
import common.app.ui.view.MyProgressDialog;
import common.app.ui.view.TitleBarView;




public class AgreementWeb extends BaseActivity {
    private TitleBarView titleBarView;
    private Intent intent;
    private WebView webview;
    private MyProgressDialog Progress;

    public static final String TYPE_PRIVATE = "private";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";

    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_YINLIU = "yinliu";
    public static final String TYPE_RECHARGE = "recharge";
    public static final String TYPE_OPERATE = "operate";
    public static final String TYPE_SUPPLY = "supply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_agreement_web);
    }

    @Override
    protected void initView() {
        super.initView();
        titleBarView = (TitleBarView) findViewById(R.id.title_bar);
        webview = (WebView) findViewById(R.id.web);
    }

    private String type = "";

    @Override
    protected void initData() {
        super.initData();
        intent = getIntent();
        String mTitle = intent.getStringExtra(KEY_TITLE);
        if (!TextUtils.isEmpty(mTitle)) {
            titleBarView.setText(mTitle);
        }
        type = intent.getStringExtra(KEY_TYPE);
        if (TextUtils.isEmpty(type)) {
            showResult("");
            finish();
            return;
        }
        titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
            }
        });
        Progress = new MyProgressDialog(this, this.getResources().getString(R.string.hold_on));
        WebSettings settings = webview.getSettings();

        settings.setJavaScriptEnabled(true); 
        settings.setAllowFileAccess(true); 
        settings.setBuiltInZoomControls(true); 
        settings.setSupportZoom(true); 
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                
                if (100 == newProgress) {
                    Progress.dismiss();
                } else {
                    Progress.show();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });
        getXieYi();
    }

    
    private void getXieYi() {
        OkHttps httpclient = new OkHttps(this);
        httpclient.addResponseListener(new BusinessResponse() {
            @Override
            public void OnMessageResponse(int id, String jo) {
                if (!TextUtils.isEmpty(jo)) {
                    webview.loadDataWithBaseURL(null, jo, "text/html", "utf-8", null);
                }
            }
        });
        String[] a = new String[]{"type"};
        String[] b = new String[]{type};
        httpclient.httppost(Common.GETXIEYIURL, httpclient.getCanshuPaixu(a, b), true, 1);
    }
}
