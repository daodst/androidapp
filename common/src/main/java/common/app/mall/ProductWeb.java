

package common.app.mall;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import common.app.R;
import common.app.ui.view.TitleBarView;



public class ProductWeb extends BaseActivity implements View.OnClickListener {
    private TitleBarView titleBarView;
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_productweb);
    }
    @Override
    protected void initView() {
        super.initView();
        titleBarView = (TitleBarView) findViewById(R.id.title_bar);
        webview = (WebView) findViewById(R.id.showdiarys);
    }
    @Override
    protected void initData() {
        super.initData();
        final Bundle bundle = getIntent().getExtras();
        final String diarysText = bundle.getString("diary");
        Log.i("++++++++",diarysText+"--");
        titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true); 
        settings.setAllowFileAccess(true); 
        settings.setBuiltInZoomControls(true); 
        settings.setSupportZoom(true); 
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSavePassword(false);
        settings.setSaveFormData(false);
        webview.loadDataWithBaseURL(null,diarysText, "text/html", "utf-8", null);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
