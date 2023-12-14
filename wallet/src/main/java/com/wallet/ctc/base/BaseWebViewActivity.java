

package com.wallet.ctc.base;

import android.annotation.SuppressLint;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.model.me.HelpDetailBean;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.LogUtil;

import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class BaseWebViewActivity extends BaseActivity {
    private WebView mWebView;
    private String key;
    private String titleStr = "";
    private TextView btnLeft;
    private TextView tvTitle;
    ImageView fujian;
    private String url = "";
    private String id;
    private int type=0;
    private String sysName;
    private Gson gson=new GsonBuilder()
                    .disableHtmlEscaping() 
                    .create();
    private MeApi mApi=new MeApi();
    public final String HTML_STYLE = "<style type=\"text/css\">\n" +
            "img,iframe,video {height:auto; max-width:100%; word-break:break-all;} \n</style>\n";
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    int startTime;
    private ACache aCache;

    @Override
    public int initContentView() {
        return R.layout.activity_webview;
    }

    @Override
    public void initUiAndListener() {
        type=getIntent().getIntExtra("type",0);
        url=getIntent().getStringExtra("url");
        titleStr=getIntent().getStringExtra("title");
        aCache=ACache.get(this);
        if(type==1){
            sysName=getIntent().getStringExtra("sysName");
        }
        if(null==url){
            url="";
        }
        findViews();
    }

    @Override
    public void initData() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void findViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnLeft = (TextView) findViewById(R.id.tv_back);
        tvTitle.setText(titleStr);
        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    mWebView.setVisibility(View.GONE);
                    mWebView.removeAllViews();
                    finish();
                }
            }
        });
        mWebView = (WebView) findViewById(R.id.baseweb_webview);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(false);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        String data=mAcache.getAsString(url);
        
        settings.setDatabaseEnabled( true );
        String dbPath = getApplicationContext().getDir( "database" , MODE_PRIVATE).getPath();
        settings.setDatabasePath(dbPath);
        if(null==data||TextUtils.isEmpty(data)){
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);  
        }else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  
        }
        settings.setAppCacheMaxSize(50*1024*1024);   
        settings.setAppCacheEnabled(true);
        String appCaceDir = this .getApplicationContext().getDir( "cache" , MODE_PRIVATE).getPath();
        settings.setAppCachePath(appCaceDir);
        
        mWebView.requestFocusFromTouch();
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                
                
                view.loadUrl(url);
                return true;
            }
        });
        if(type==1){
            String url=aCache.getAsString(sysName);
            if(!TextUtils.isEmpty(url)) {
                if(!url.startsWith("http")) {
                    mWebView.loadDataWithBaseURL("", HTML_STYLE + url, "text/html", "UTF-8", null);
                }else {
                    mWebView.loadUrl(url);
                }
            }
            getUrl();
        }else if(type==2){
            getDetail(url);
        }else {
            initDatas();
        }
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
        if(!url.startsWith("http")) {
            LogUtil.d(HTML_STYLE+url);
            mWebView.loadDataWithBaseURL("", HTML_STYLE + url, "text/html", "UTF-8", null);
        }else {
            mWebView.loadUrl(url);
        }
    }

    private void loadUrl(){

    }

    @Override
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack(); 
            return true;
        } else {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.setVisibility(View.GONE);
        mWebView.removeAllViews();
        mWebView.destroy();
    }
    private void getUrl(){
        Map<String, Object> params2 = new TreeMap();
        params2.put("type", sysName);
        mLoadingDialog.show();
        mApi.getArticle(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        mLoadingDialog.dismiss();
                        if (baseEntity.getStatus() == 1) {
                            url=baseEntity.getData()+"";
                            if(TextUtils.isEmpty(url)){
                                return;
                            }
                            aCache.put(sysName,url);
                            initDatas();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void getDetail(String id){
        Map<String, Object> params2 = new TreeMap();
        params2.put("id", id);
        mApi.getHelpInfo(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            HelpDetailBean helpDetailBean=gson.fromJson(gson.toJson(baseEntity.getData()),HelpDetailBean.class);
                            if(!TextUtils.isEmpty(helpDetailBean.getContent())){
                                url=helpDetailBean.getContent();
                            }else if(!TextUtils.isEmpty(helpDetailBean.getUrl())){
                                url=helpDetailBean.getUrl();
                            }
                            initDatas();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    private void getArticleDetail(String id){
        Map<String, Object> params2 = new TreeMap();
        params2.put("id", id);
        mApi.getArticleInfo(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            HelpDetailBean helpDetailBean=gson.fromJson(gson.toJson(baseEntity.getData()),HelpDetailBean.class);
                            if(!TextUtils.isEmpty(helpDetailBean.getContent())){
                                url=helpDetailBean.getContent();
                            }else if(!TextUtils.isEmpty(helpDetailBean.getUrl())){
                                url=helpDetailBean.getUrl();
                            }
                            initDatas();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        
        super.onStop();
        mWebView.clearCache(false);
    }
}
