

package common.app.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.app.ActivityRouter;
import common.app.R;
import common.app.RxBus;
import common.app.base.base.PermissionListener;
import common.app.base.model.http.bean.Result;
import common.app.base.them.Eyes;
import common.app.im.event.AccountError;
import common.app.ui.view.MyProgressDialog;
import common.app.ui.view.ToastView;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ToastView toast;
    private static final String TAG = "mall/BaseActivity";

    public static LinkedList<Activity> allMallActivitys = new LinkedList<Activity>();

    private CompositeDisposable mSubscriptions;
    protected InputMethodManager manager;
    public MyProgressDialog myProgressDialog;
    

    
    private Toast mToast;

    private InputMethodManager managerInput;

    
    protected boolean mNeedLogin = false;

    protected void setNeedLogin(boolean needLogin) {
        this.mNeedLogin = needLogin;
    }

    protected boolean isNeedLogin() {
        return false;
    }

    protected void goLogin() {
        
        ActivityRouter.startEmptyContentActivity(this, ActivityRouter.Lg4e.F_LoginFragment);
    }

    
    protected boolean isLogined() {

        return true;
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        LanguageUtil.initLanguage(this);
        myProgressDialog = new MyProgressDialog(this, "...");
        
        mNeedLogin = isNeedLogin();
        if (mNeedLogin) {
            

        }
        
        allMallActivitys.add(this);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        addSubscription(subscribeEvents());

        

        managerInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }




    public void setView(int layoutResID) {
        setContentView(layoutResID);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int intgetcolor = getColor(R.color.default_titlebar_bg_color);
                Eyes.setStatusBarLightMode(this, intgetcolor);
            } else {
                int intgetcolor = 0x30ffffff;
                Eyes.setStatusBarLightMode(this, intgetcolor);
            }
        }
        initView();
        initData();
    }

    public void setViewNoBg(int layoutResID) {
        setContentView(layoutResID);
        initView();
        initData();
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void addSubscription(Disposable disposable) {
        if (disposable == null) {
            return;
        }
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeDisposable();
        }
        mSubscriptions.add(disposable);
    }

    
    protected Disposable subscribeEvents() {
        return RxBus
                .getInstance()
                .toObservable()
                .compose(RxSchedulers.io_main())
                .subscribe(this::beSucceed, this::failure);
    }

    
    public void beSucceed(Object obj) {
        if (obj instanceof AccountError) {
            
            
            LogUtil.d(TAG, "errorCode=" + ((AccountError) obj).errorCode);
            this.finish();
        }
        succeed(obj);
    }

    
    public void succeed(Object obj) {

    }

    
    public void failure(Throwable throwable) {
        LogUtil.i(TAG, Log.getStackTraceString(throwable));
    }

    @Override
    protected void onDestroy() {
        
        super.onDestroy();
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
        allMallActivitys.remove(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void finishAll() {
        for (Activity activity : allMallActivitys) {
            activity.finish();
        }
        allMallActivitys.clear();
    }

    @SuppressLint("ShowToast")
    public void toast(String text) {
       
        showResult(text);
    }

    public void showResult(String info) {
        if (null == mToast) {
            mToast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(info);
        }
        mToast.show();
    }

    public void showErrorResult(Result result) {
        if (null != result && !TextUtils.isEmpty(result.getInfo())) {
            showResult(result.getInfo());
        } else {
            showResult(getString(R.string.error_unknow));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                managerInput.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

}
