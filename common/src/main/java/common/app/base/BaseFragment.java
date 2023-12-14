

package common.app.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.trello.rxlifecycle2.components.support.RxFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.R;
import common.app.RxBus;
import common.app.base.activity.ContentActivity;
import common.app.base.base.PermissionListener;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BaseFragment<VM extends BaseViewModel> extends RxFragment implements IBase.IUiBase, IBase.IVmBase<VM> {

    private static final String TAG = "BaseFragment";
    protected Context mContext;
    protected View mView;
    
    protected Unbinder mUnbinder;

    
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return -1;
    }

    
    public View initBindingView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }
    
    protected boolean isLogined() {
        return true;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
    }

    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

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

    protected final Disposable subscribeEvents() {
        return RxBus
                .getInstance()
                .toObservable()
                .compose(RxSchedulers.io_main())
                .subscribe(this::succeed, this::failure);
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
    public <T> T getViewDataBinding() {
        
        return null;
    }

    
    protected boolean attachToRoot() {
        return false;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        
        int layoutId = initContentView(inflater, container, savedInstanceState);
        if (-1 != layoutId) {
            
            mView = inflater.inflate(layoutId, container, attachToRoot());
            mUnbinder = ButterKnife.bind(this, mView);
            return mView;
        } else {
            View rootView = initBindingView(inflater, container, savedInstanceState);
            if (null != rootView){
                mView = rootView;
                return mView;
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        addSubscription(subscribeEvents());
    }

    
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mUnbinder) {
            mUnbinder.unbind();
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
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), PERMISSIONREQUSTCODE);
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

    /

    
    @Override
    public void showToast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    protected MyProgressDialog mProgressDialog;

    
    @Override
    public void showLoadingDialog(String content) {
        if (null == mProgressDialog) {
            mProgressDialog = new MyProgressDialog(getActivity(), "Loading");
        } else if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
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
        if (null != mAlertDialog) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new MyAlertDialog(getActivity(), content);
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
    


    /
    protected void onLazyLoad(View view) {

    }

    
    protected void onFragmentVisible() {

    }

    
    protected void onFragmentHide() {
    }

    
    protected boolean isNowVisible() {
        if (!mPause && null != mView && mVisiblae) {
            return true;
        } else {
            return false;
        }
    }


    
    public void onParentFragmentVisible() {
        if (!mPause && null != mView && mVisiblae) {
            onFragmentVisible();
        }
    }

    
    public void onParentFragmentInVisible() {
        onFragmentHide();
    }


    /
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
            
            viewModel.injectLifecycleProvider(this, true);
            
            viewModel.setLifecyleOwner(this);

            
            registViewModelUiControl();

        }
    }

    
    @Override
    public void registViewModelUiControl() {
        viewModel.observe(viewModel.showDialogEvent, new Observer<DialogContainerEvent>() {
            @Override
            public void onChanged(@Nullable DialogContainerEvent dialogContainerEvent) {
                showAlertDialog(dialogContainerEvent.content, dialogContainerEvent.okBtnListener, dialogContainerEvent.cancleBtnListener);
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

    protected final static Intent getEmptyIntent(Context context, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(common.app.base.base.BaseFragment.setEmptyBundle(clazz));
        return intent;
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

    
    public final void initViewModelFix(Class<VM> clazz) {
        if (null == viewModel) {
            viewModel = new ViewModelProvider(this).get(clazz);
            
            viewModel.injectLifecycleProvider(this, true);
            
            viewModel.setLifecyleOwner(this);
            
            registViewModelUiControl();
        }
    }
}
