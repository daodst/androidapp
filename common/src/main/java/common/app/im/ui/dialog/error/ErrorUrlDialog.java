

package common.app.im.ui.dialog.error;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.R;
import common.app.R2;
import common.app.base.base.BaseDialogFragment;
import common.app.utils.AllUtils;



public class ErrorUrlDialog extends BaseDialogFragment {

    @BindView(R2.id.active_img)
    WebView mWebView;
    @BindView(R2.id.close_dialog)
    ImageView closeDialog;
    private static final String PARAM_TITLE = "PARAM_TITLE";
    private static final String PARAM_MESSAGE = "PARAM_MESSAGE";
    private static final String PARAM_CANCELABLE = "PARAM_CANCELABLE";
    private static final String PARAM_ERROR_CODE = "PARAM_ERROR_CODE";


    private String mTitle;
    private String mMesage;
    private int mErrorCode;

    private IErrorClick mIErrorClick;

    public void setIErrorClick(IErrorClick IErrorClick) {
        mIErrorClick = IErrorClick;
    }

    private boolean mCancelable;


    public static Bundle getBundle(String title, String message, boolean cancelable, int errorCode) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_TITLE, title);
        bundle.putString(PARAM_MESSAGE, message);
        bundle.putBoolean(PARAM_CANCELABLE, cancelable);
        bundle.putInt(PARAM_ERROR_CODE, errorCode);
        return bundle;
    }


    private void iniData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mTitle = bundle.getString(PARAM_TITLE);
            mMesage = bundle.getString(PARAM_MESSAGE);
            mErrorCode = bundle.getInt(PARAM_ERROR_CODE);
            this.mCancelable = bundle.getBoolean(PARAM_CANCELABLE);
        }
        mTitle = null == mTitle ? "" : mTitle;
        mMesage = null == mMesage ? "" : mMesage;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        int width= AllUtils.getDisplayMetricsWidth(getActivity().getWindowManager())- AllUtils.dip2px(getContext(),60);
        int height=(int)(width*1.3+ AllUtils.dip2px(getContext(),40));
        getDialog().getWindow().setLayout(width,height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error_url, container, false);
        iniData();
        this.setCancelable(this.mCancelable);
        mUnbinder = ButterKnife.bind(this, view);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void initEvents() {
        closeDialog.setOnClickListener(v -> {
            
            if (null != mIErrorClick) {
                mIErrorClick.onClik(mErrorCode);
            }
        });
    }

    @Override
    protected void initViews() {

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
        
        settings.setDatabaseEnabled( true );

        settings.setAppCacheMaxSize(50*1024*1024);   
        settings.setAppCacheEnabled(true);
        
        mWebView.requestFocusFromTouch();
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                
                
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl("https://www.baidu.com");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface IErrorClick {
        
        void onClik(int errorCode);
    }
}
