

package common.app.base.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;
import common.app.ActivityRouter;
import common.app.R;
import common.app.RxBus;
import common.app.base.them.Eyes;
import common.app.utils.ActivityContainer;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;



public abstract class BaseActivity extends RxAppCompatActivity implements FragmentContract.Contract4Activity {

    public static final String CLASS = FragmentContract.CLASS;
    public static final String DATA = FragmentContract.DATA;
    public static final String TYPE = FragmentContract.TYPE;
    private static final String TAG = "BaseActivity";

    public BaseFragment mBaseFragment;

    protected Unbinder unbinder;

    private CompositeDisposable mSubscriptions;

    
    



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    
    
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

    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityContainer.getInstance().getList().add(this);
        LanguageUtil.initLanguage(this);
        super.onCreate(savedInstanceState);
        if (needState() && Build.VERSION.SDK_INT !=Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int intgetcolor = getColor(R.color.default_titlebar_bg_color);
                Eyes.setStatusBarLightMode(this, intgetcolor);
            } else {
                int intgetcolor = 0x30ffffff;
                Eyes.setStatusBarLightMode(getActivity(), intgetcolor);
            }
        }
        
        mNeedLogin = isNeedLogin();
        if (mNeedLogin) {
            
        }
        
        addSubscription(subscribeEvents());
        
        
        initData();
        initEvent();

    }

    public void setView(int layoutResID) {
        setContentView(layoutResID);
    }

    protected boolean needState() {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)
        {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    
    protected void initData() {
    }

    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            
            View v = getCurrentFocus();
            
            if (isShouldHideInput(v, ev) && needAutoHide() && isSoftShowing()) {
                LogUtil.i(TAG, isShouldHideInput(v, ev) + "");
                hideSoftInput(v.getWindowToken());
            }
            
        }
        return super.dispatchTouchEvent(ev);
    }



    public interface HideView {
        void hiedeView();
    }

    private boolean isSoftShowing() {
        
        int screenHeight = getWindow().getDecorView().getHeight();
        
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    protected boolean needAutoHide() {
        if (null != mKeyBoard) {
            return mKeyBoard.needAutoHide();
        }
        return true;
    }


    private KeyBoard mKeyBoard;

    public KeyBoard getKeyBoard() {
        return mKeyBoard;
    }

    public void setKeyBoard(KeyBoard keyBoard) {
        mKeyBoard = keyBoard;
    }

    public interface KeyBoard {
        boolean needAutoHide();
    }

    
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (canBeFocus(v)) {
            return false;
        }
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                
                return false;
            } else {
                return true;
            }
        }
        
        return false;
    }


    
    public boolean canBeFocus(View v) {
        return false;
    }

    
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    
    protected void initEvent() {
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
        ActivityContainer.getInstance().getList().remove(this);
    }


    @Override
    public void replaceFragment(@Nullable BaseFragment fragment) {
        replaceFragment(fragment);
    }

    @Override
    public void replaceFragment4S(@Nullable BaseFragment fragment, @Nullable String value) {
        fragment.setArguments(fragment.get4SBundle(value));
        replaceFragment(fragment);
    }

    @Override
    public void replaceFragment4P(@Nullable BaseFragment fragment, @Nullable Parcelable value) {
        fragment.setArguments(fragment.get4PBundle(value));
        replaceFragment(fragment);
    }

    @Override
    public void replaceFragment4PList(@Nullable BaseFragment fragment, @Nullable ArrayList<? extends Parcelable> value) {
        fragment.setArguments(fragment.get4PListBundle(value));
        replaceFragment(fragment);
    }

    @Override
    public void replaceFragment4SList(@Nullable BaseFragment fragment, @Nullable ArrayList<String> value) {
        fragment.setArguments(fragment.get4SListBundle(value));
        replaceFragment(fragment);
    }

    


    @Override
    public BaseFragment setFragment(@Nullable String clazz) {
        BaseFragment fragment = getBaseFragment(clazz);
        setFragment(fragment);
        return fragment;
    }

    public BaseFragment setSeFragment(@Nullable String clazz, @Nullable Serializable value) {
        BaseFragment fragment = getBaseFragment(clazz);
        fragment.setArguments(fragment.get4SPBundle(value));
        setFragment(fragment);
        return fragment;
    }

    @Override
    public BaseFragment setFragment4P(@Nullable String clazz, @Nullable Parcelable value) {
        BaseFragment fragment = getBaseFragment(clazz);
        fragment.setArguments(fragment.get4PBundle(value));
        setFragment(fragment);
        return fragment;
    }

    @Override
    public BaseFragment setFragment4S(@Nullable String clazz, @Nullable String value) {
        BaseFragment fragment = getBaseFragment(clazz);
        fragment.setArguments(fragment.get4SBundle(value));
        setFragment(fragment);
        return fragment;
    }

    @Override
    public BaseFragment setFragment4PList(@Nullable String clazz, @Nullable ArrayList<? extends Parcelable> value) {
        BaseFragment fragment = getBaseFragment(clazz);
        fragment.setArguments(fragment.get4PListBundle(value));
        setFragment(fragment);
        return fragment;
    }

    @Override
    public BaseFragment setFragment4SList(@Nullable String clazz, @Nullable ArrayList<String> value) {
        BaseFragment fragment = getBaseFragment(clazz);
        fragment.setArguments(fragment.get4SListBundle(value));
        setFragment(fragment);
        return fragment;
    }

    private BaseFragment getBaseFragment(String clazz) {
        try {
            
            return (BaseFragment) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            LogUtil.i(TAG, Log.getStackTraceString(e));
            throw new RuntimeException(e);
        }

    }

    
    private void setFragment(BaseFragment fragment) {
        replaceFragment(fragment, fragment.needAddToBackStack(), null);
    }

    
    private void replaceFragment(BaseFragment fragment, Boolean allowBack, int[] anim) {

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (null != anim) {
                transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
            }
            transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            if (allowBack) {
                transaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            transaction.commitAllowingStateLoss();

        }
    }

    private void replace(BaseFragment fragment) {

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            int[] anim = fragment.getAnimations();
            if (null != anim) {
                transaction.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
            }
            transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
            if (fragment.needAddToBackStack()) {
                transaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            transaction.commitAllowingStateLoss();

        }
    }


    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {

            moveTaskToBack(true);
        }
    }


    


    private PermissionListener mListener;

    private static final int PERMISSIONREQUSTCODE = 1;

    public void requestRuntimePermisssions(String[] permissions, PermissionListener listener) {

        mListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            
            mListener.onGranted();
            return;
        }
        
        List<String> permissionList = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), PERMISSIONREQUSTCODE);
        } else {
            mListener.onGranted();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONREQUSTCODE: {
                
                if (grantResults != null && grantResults.length > 0) {
                    List<String> deniedList = new ArrayList<String>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedList.add(permissions[i]);
                        }
                    }
                    if (deniedList.isEmpty()) {
                        if (null != mListener) {
                            mListener.onGranted();
                        }

                    } else {
                        if (null != mListener) {
                            mListener.onDenied(deniedList);
                        }
                        
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.permission_denied_alert_ok, okListener)
                .setNegativeButton(R.string.permission_denied_alert_cancel, cancelListener)
                .create()
                .show();

    }

    

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showMsg(int msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public Activity getActivity() {
        return this;
    }


}
