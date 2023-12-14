

package common.app.my;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import common.app.BuildConfig;
import common.app.R;
import common.app.base.base.PermissionListener;
import common.app.base.model.http.HttpMethods;
import common.app.im.utils.Pic;
import common.app.mall.BaseActivity;
import common.app.my.localalbum.utils.FilterImageView;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.FileUtils;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;


public class Web extends BaseActivity {

    private static final String TAG = "Web";

    private FilterImageView mBackBtn;
    private FilterImageView mCloseBtn;
    private TextView mRightBtn;
    private FilterImageView mRefreshBtn;
    private TextView mTitleTv;

    private Intent intent;
    private WebView mWebView;
    private String from = "";
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private String device = "app";
    private String lang;
    private String url = "";
    private String html = "";

    private MyProgressDialog mLoadingDialog;

    public static void startWebActivity(Context mContext, String url, String mTitle, Map<String, String> parames) {
        Intent intent = new Intent(mContext, Web.class);
        intent.putExtra("url", url);
        intent.putExtra("title", mTitle);
        if (null != parames) {
            for (Map.Entry<String, String> entry : parames.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        mContext.startActivity(intent);
    }

    public static void startWebActivity(Context mContext, String url, String mTitle, String rightUrl, String rightText, Map<String, String> parames) {
        Intent intent = new Intent(mContext, Web.class);
        intent.putExtra("url", url);
        intent.putExtra("title", mTitle);
        intent.putExtra("rightUrl", rightUrl);
        intent.putExtra("rightText", rightText);
        if (null != parames) {
            for (Map.Entry<String, String> entry : parames.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        mContext.startActivity(intent);
    }


    public static Intent getIntentWebActivity(Context mContext, String url, String mTitle, Map<String, String> parames) {
        Intent intent = new Intent(mContext, Web.class);
        intent.putExtra("url", url);
        intent.putExtra("title", mTitle);
        if (null != parames) {
            for (Map.Entry<String, String> entry : parames.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_web);
    }

    @Override
    protected void initView() {
        super.initView();
        lang = LanguageUtil.getNowLocalStr(this);
        mWebView = (WebView) findViewById(R.id.web);
        mBackBtn = (FilterImageView) findViewById(R.id.btn_titlebar_left);
        mCloseBtn = (FilterImageView) findViewById(R.id.btn_close);
        mRightBtn = (TextView) findViewById(R.id.btn_titlebar_rights);
        mRefreshBtn = (FilterImageView) findViewById(R.id.btn_titlebar_right);
        mTitleTv = (TextView) findViewById(R.id.tv_titlebar_name);
        mLoadingDialog = new MyProgressDialog(this, "");
        initSetting();

    }

    
    private String mTitle;
    private String rightHtml;
    private String rightText;

    @Override
    protected void initData() {
        super.initData();
        intent = getIntent();
        mTitle = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        html = intent.getStringExtra("html");
        from = intent.getStringExtra("from");
        rightHtml = intent.getStringExtra("rightUrl");
        rightText = intent.getStringExtra("rightText");

        mTitleTv.setText(mTitle);
        if (!TextUtils.isEmpty(rightText)) {
            mRightBtn.setText(rightText);
            mRightBtn.setVisibility(View.VISIBLE);
            mRefreshBtn.setVisibility(View.GONE);
        } else {
            mRefreshBtn.setVisibility(View.VISIBLE);
        }


        if (null == from) {
            from = "";
        }
        if (!TextUtils.isEmpty(url) && (url.startsWith(BuildConfig.HOST))) {
            if (!url.contains("?")) {
                if (url.contains("device")) {
                    url = url + "&lang=" + lang;
                } else {
                    url = url + "?device=" + device + "&" + HttpMethods.KEY_LANGUAGE + "=" + lang;
                }
            } else {
                if (url.contains("device")) {
                    url = url + "&lang=" + lang;
                } else {
                    url = url + "&device=" + device + "&" + HttpMethods.KEY_LANGUAGE + "=" + lang;
                }
            }
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, url);
            }

        }

        if (mTitle != null) {
            mTitleTv.setText(mTitle);
        }


        mBackBtn.setOnClickListener(view -> {
            if (null == mWebView || !mWebView.canGoBack()) {
                finish();
            }
        });

        mRightBtn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(rightText)) {
                startWebActivity(Web.this, rightHtml, rightText, null);
            }
        });


        mRefreshBtn.setOnClickListener(view -> {
            if (null != mWebView) {
                mWebView.reload();
            }
        });

        mCloseBtn.setOnClickListener(view -> {
            finish();
        });

        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        } else {
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        }

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        
        mWebView.setOnLongClickListener(view -> {
            int type = mWebView.getHitTestResult().getType();
            if (type == WebView.HitTestResult.IMAGE_TYPE) {
                String imgurl = mWebView.getHitTestResult().getExtra();

                final String[] items = {getString(R.string.common_save_img)};
                AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(Web.this);
                listDialog.setTitle("");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            
                            saveImage(imgurl, new SaveImageCallback() {
                                @Override
                                public void onFinish(String savePath) {
                                    if (!TextUtils.isEmpty(savePath)) {
                                        showResult(getString(R.string.person_qr_save_success) + savePath);
                                    }
                                }
                            });
                        }
                    }
                });
                listDialog.show();
            }
            return false;
        });

    }

    private void initSetting() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setUseWideViewPort(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(false);
        
        mWebView.addJavascriptInterface(new AndroidtoJs(this, from), AndroidtoJs.METHOD_NAME);
        mWebView.setWebChromeClient(mWebChromeClient);
    }

    private void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mLoadingDialog && !mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }
            }
        });
    }

    private void dismisLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    
    public static interface SaveImageCallback {
        void onFinish(String savePath);
    }

    private void saveImage(String imgUrl, SaveImageCallback callback) {
        
        requestRuntimePermisssions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                showLoading();
                FileUtils.saveWebImage(Web.this, imgUrl, (success, filePath, fileName) -> {
                    dismisLoading();
                    if (success) {
                        try {
                            MediaStore.Images.Media.insertImage(getContentResolver(), filePath, fileName, null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            try {
                                String applicationId = getPackageName();
                                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), applicationId + ".fileProvider", new File(filePath));
                                intent.setData(contentUri);
                            } catch (Exception e) {
                                Uri uri2 = Uri.fromFile(new File(filePath));
                                intent.setData(uri2);
                            }
                        } else {
                            Uri uri2 = Uri.fromFile(new File(filePath));
                            intent.setData(uri2);
                        }
                        sendBroadcast(intent);
                    } else {
                        showResult(getString(R.string.common_save_faile));
                    }
                    if (null != callback) {
                        callback.onFinish(filePath);
                    }
                });
            }

            @Override
            public void onDenied(List<String> deniedList) {
            }
        });
    }
    


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        
        
        
        if (null != mWebView) {
            mWebView.clearFormData();
            mWebView.clearMatches();
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.loadUrl("about:blank");
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getCookieInfo(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getCookieInfo(url);
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            
            super.onPageFinished(view, url);
            getCookieInfo(url);
        }

    };

    private void getCookieInfo(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(url);
        Log.d("WebCookie", "getCookieInfo()url=" + url + "\n" + cookies);
    }

    private void setCookieInfo(String url, String cookies) {
        Log.d("WebCookie", "setCookieInfo()url=" + url + ", " + cookies);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookies);
        CookieSyncManager.getInstance().sync();
    }


    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            
        }
        

        
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            
            pickPic();
        }

        
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            
            pickPic();
        }

        
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            
            pickPic();
        }

        
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            
            pickPic();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mTitle == null) {
                mTitleTv.setText(title);
            }
        }
    };

    private void pickPic() {
        requestRuntimePermisssions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                Pic.from(Web.this).getMatisseLocal(FILE_CHOOSER_RESULT_CODE, 1);
            }

            @Override
            public void onDenied(List<String> deniedList) {
                
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                List<Uri> uriList = Matisse.obtainResult(data);
                if (uriList.size() < 1) {
                    return;
                }

                Uri selectedImage = uriList.get(0);
                if (uploadMessageAboveL != null) {
                    Uri[] results = new Uri[]{selectedImage};
                    uploadMessageAboveL.onReceiveValue(results);
                    uploadMessageAboveL = null;
                } else if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(selectedImage);
                    uploadMessage = null;
                }
            } else {
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                } else if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }

                uploadMessageAboveL.onReceiveValue(results);
                uploadMessageAboveL = null;
            } else {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;

            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
