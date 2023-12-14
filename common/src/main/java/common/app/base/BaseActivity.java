

package common.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.R;
import common.app.RxBus;
import common.app.base.base.PermissionListener;
import common.app.im.event.AccountError;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.FloatViewHelper;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.AllUtils;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BaseActivity<VM extends BaseViewModel> extends RxAppCompatActivity implements IBase.IUiBase, IBase.IVmBase<VM> {


    private static final String TAG = "BaseActivity";

    
    protected Unbinder mUnbinder;
    private boolean forceIntercept = false;

    public boolean isForceIntercept() {
        return forceIntercept;
    }

    public void setForceIntercept(boolean forceIntercept) {
        this.forceIntercept = forceIntercept;
    }

    
    public int initContentView(Bundle savedInstanceState) {
        return -1;
    }


    public View initBindingView(Bundle savedInstanceState) {
        return null;
    }


    @Override
    public void initParam() {

    }

    @Override
    public void initView(@Nullable View view) {

    }

    @Override
    public void initData() {
    }

    

    @Override
    public <T> T getViewDataBinding() {
        
        return null;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtil.initLanguage(this);
        
        initParam();
        
        
        int layoutId = initContentView(savedInstanceState);

        if (-1 != layoutId) {
            
            setContentView(layoutId);
            mUnbinder = ButterKnife.bind(this);
        } else {
            View rootView = initBindingView(savedInstanceState);
            if (null != rootView) {
                setContentView(rootView);
            }
        }


        
        if (!isForceIntercept()) {
            initView(getWindow().getDecorView().findViewById(android.R.id.content));

            addSubscription();
            
            initData();
        }
    }


    protected void addSubscription() {
        addSubscription(subscribeEvents());
    }

    private CompositeDisposable mDisposable;

    protected final void addSubscription(Disposable subscription) {
        if (subscription == null) {
            return;
        }
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(subscription);
    }

    
    protected Disposable subscribeEvents() {
        return RxBus
                .getInstance()
                .toObservable()
                .compose(RxSchedulers.io_main())
                .subscribe(this::beSucceed, this::failure);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SpUtil.isDebug()) {
            FloatViewHelper.showFloatView(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!AllUtils.isAppOnForeground(this)) {
            FloatViewHelper.removeFloatView(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        if (null != mDisposable) {
            mDisposable.dispose();
        }
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }

    }

    

    
    @Override
    public void showToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }

    }

    
    public void showToast(@StringRes int strResId) {
        Toast.makeText(this, getString(strResId), Toast.LENGTH_SHORT).show();
    }

    protected MyProgressDialog mProgressDialog;

    
    @Override
    public void showLoadingDialog(String content) {
        if (null == mProgressDialog) {
            mProgressDialog = new MyProgressDialog(BaseActivity.this, "Loading");
        } else if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }

    public void showLoading() {
        showLoadingDialog("Loading...");
    }

    public void disLoading() {
        dismissLoadingDialog();
    }

    
    @Override
    public void dismissLoadingDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    protected MyAlertDialog mAlertDialog;

    
    @Override
    public void showAlertDialog(String content, View.OnClickListener okBtnListener, View.OnClickListener cancelBtnListener) {
        if (null != mAlertDialog ) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new MyAlertDialog(this, content);
        mAlertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                if (null != okBtnListener) {
                    okBtnListener.onClick(null);
                }
            }

            @Override
            public void No() {
                if (null != cancelBtnListener) {
                    cancelBtnListener.onClick(null);
                }
            }
        });
        mAlertDialog.show();
    }

    
    @Override
    public void dismissDialog() {
        if (null != mAlertDialog) {
            mAlertDialog.dismiss();
        }
    }
    


    
    protected VM viewModel;


    
    @Override
    public final void initViewModel() {
        if (viewModel == null) {
            Class modelClass = null;
            Type type = getClass().getGenericSuperclass();
            if (null != type && type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getActualTypeArguments() != null && parameterizedType.getActualTypeArguments().length > 0) {
                    
                    modelClass = (Class) parameterizedType.getActualTypeArguments()[0];
                } else {
                    Log.d(TAG, "has no Any T class");
                }
            } else {
                Log.d(TAG, "GenericSuperclass may Null or not ParameterizedType");
            }

            
            if (modelClass == null) {
                modelClass = BaseViewModel.class;
            }

            viewModel = (VM) createViewModel(this, modelClass);
            
            viewModel.injectLifecycleProvider(this, false);
            
            viewModel.setLifecyleOwner(this);

            
            registViewModelUiControl();

        }
    }

    
    @Override
    public void registViewModelUiControl() {
        
        viewModel.observe(viewModel.showDialogEvent, new Observer<DialogContainerEvent>() {
            @Override
            public void onChanged(@Nullable DialogContainerEvent dialogEvent) {
                if (null != dialogEvent) {
                    showAlertDialog(dialogEvent.content, dialogEvent.okBtnListener, dialogEvent.cancleBtnListener);
                }

            }
        });

        
        viewModel.observe(viewModel.dismissDialogEvent, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                dismissDialog();
            }
        });

        
        viewModel.observe(viewModel.showToastEvent, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String content) {
                showToast(content);
            }
        });

        
        viewModel.observe(viewModel.showLoadingEvent, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String content) {
                showLoadingDialog(content);
            }
        });

        
        viewModel.observe(viewModel.dismissLoadingEvent, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                dismissLoadingDialog();
            }
        });
    }

    
    @Override
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    
    @Override
    public <T extends ViewModel> T createViewModel(@NonNull Fragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);
    }

    @Override
    public <T extends ViewModel> T createViewModel(@NonNull DialogFragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);
    }

    
    @Override
    public VM getViewModel() {
        initViewModel();
        return viewModel;
    }
    

    public PermissionListener mListener;

    private static final int PERMISSIONREQUSTCODE = 1;

    public void requestRuntimePermisssions(String[] permissions, PermissionListener listener) {

        mListener = listener;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            
            if (null != mListener) {
                mListener.onGranted();
            }
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
            if (null != mListener) {
                mListener.onGranted();
            }
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
                    if (deniedList.isEmpty() && null != mListener) {
                        mListener.onGranted();
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

    protected boolean isLogined() {
        return true;
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

    protected boolean needAutoHide() {
        if (null != mKeyBoard) {
            return mKeyBoard.needAutoHide();
        }
        return true;
    }

    private boolean isSoftShowing() {
        
        int screenHeight = getWindow().getDecorView().getHeight();
        
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

}
