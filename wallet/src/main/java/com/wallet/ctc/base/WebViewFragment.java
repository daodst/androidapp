

package com.wallet.ctc.base;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.util.LogUtil;

import common.app.base.them.Eyes;



public class WebViewFragment extends BaseFragment {
    private WebView mWebView;
    private String titleStr = "";
    private TextView btnLeft;
    private TextView tvTitle;
    private String url = "";
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private MeApi mApi = new MeApi();
    public final String HTML_STYLE = "<style type=\"text/css\">\n" +
            "img,iframe,video {height:auto; max-width:100%; word-break:break-all;} \n</style>\n";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_webview, container, false);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int intgetcolor = ContextCompat.getColor(getActivity(), R.color.default_titlebar_bg_color);
                Eyes.addStatusBar(getActivity(), (ViewGroup) view, intgetcolor);
            } else {
                int intgetcolor = 0x30ffffff;
                Eyes.addStatusBar(getActivity(), (ViewGroup) view, intgetcolor);
            }
        }
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        btnLeft = (TextView) view.findViewById(R.id.tv_back);
        mWebView = (WebView) view.findViewById(R.id.baseweb_webview);
        findViews();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void findViews() {

        btnLeft.setVisibility(View.GONE);
        tvTitle.setText(titleStr);
        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    mWebView.setVisibility(View.GONE);
                    mWebView.removeAllViews();
                    context.finish();
                }
            }
        });
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
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "lnkj");
        

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                
                
                view.loadUrl(url);
                return true;
            }
        });
        loadUrl();
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            
            String value = html;
            Message message = Message.obtain();
            message.obj = value;

        }
    }


    protected void initDatas() {
        if (!url.startsWith("http")) {
            LogUtil.d(HTML_STYLE + url);
            mWebView.loadDataWithBaseURL("", HTML_STYLE + url, "text/html", "UTF-8", null);
        } else {
            mWebView.loadUrl(url);
        }
    }

    private void loadUrl() {
        mWebView.loadUrl("http://xxx");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.setVisibility(View.GONE);
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    @Override
    public void onStop() {
        
        super.onStop();
        mWebView.clearCache(true);
    }
}
