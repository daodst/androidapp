

package common.app.base.base;


import static common.app.base.base.FragmentContract.BA_SERIALIZABLE;
import static common.app.base.base.FragmentContract.BA_TPARCELABLE;
import static common.app.base.base.FragmentContract.BA_TPARCELABLELIST;
import static common.app.base.base.FragmentContract.BA_TSTRING;
import static common.app.base.base.FragmentContract.BA_TSTRINGLIST;
import static common.app.base.base.FragmentContract.NONO;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Unbinder;
import common.app.ActivityRouter;
import common.app.R;
import common.app.RxBus;
import common.app.base.activity.ContentActivity;
import common.app.base.base.BaseActivity.KeyBoard;
import common.app.im.base.Loading;
import common.app.im.ui.view.QQDialogLoading;
import common.app.ui.view.LinkTouchMovementMethod;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements FragmentContract.Contract4Fragment, Loading, KeyBoard {


    public static final String CLASS = FragmentContract.CLASS;
    public static final String DATA = FragmentContract.DATA;
    public static final String TYPE = FragmentContract.TYPE;
    private static final String TAG = "DATA";


    protected BaseActivity mActivity;

    private CompositeDisposable mDisposable;
    
    private Toast mToast;

    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
        LanguageUtil.initLanguage(  this.mActivity);

    }


    @Override
    public boolean needAutoHide() {
        return true;
    }


    /
    protected boolean mNeedLogin = false;

    protected void setNeedLogin(boolean needLogin) {
        this.mNeedLogin = needLogin;
    }

    protected boolean isNeedLogin() {
        return false;
    }

    protected void goLogin() {
        
        
        ActivityRouter.startEmptyContentActivity(getContext(), ActivityRouter.Lg4e.F_LoginFragment);
    }

    
    protected boolean isLogined() {
        return true;
    }
    

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mNeedLogin = isNeedLogin();
        if (mNeedLogin) {
            
        }
        

        mActivity.setKeyBoard(() -> {
            return needAutoHide();
        });
        getBundleData(false);
        initViews();
        initEvents();
        
        addSubscription(subscribeEvents());
    }

    protected Bundle mBundle = null;

    protected Object mParamData = null;
    protected boolean mIsArrayString = false;


    @CallSuper
    protected void getBundleData(boolean needCheck) {
        mBundle = this.getArguments();
        
        if (needCheck) {
            if (null == mBundle) {
                mActivity.finish();
                showMsg(R.string.param_check_error);
                return;
            }
            checkParam();
        } else if (null != mBundle) {
            checkParam();
        }
    }

    private void checkParam() {
        mParamData = mBundle.get(PARAM_DATA);
        boolean isLegalArray = mParamData instanceof ArrayList && 0 == ((ArrayList) mParamData).size();
        boolean isLegalObj = null == mParamData;
        if (isLegalArray || isLegalObj) {
            mActivity.finish();
        }
        boolean legalArray = mParamData instanceof ArrayList && 0 < ((ArrayList) mParamData).size();
        if (legalArray && ((ArrayList) mParamData).get(0) instanceof String) {
            mIsArrayString = true;
        }
    }

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


    
    public void succeed(Object obj) {

    }


    
    public void failure(Throwable throwable) {
        LogUtil.i(TAG, Log.getStackTraceString(throwable));
    }

    protected T mPresenter;

    public void setPresenter(T presenter) {
        this.mPresenter = presenter;
    }

    protected Unbinder mUnbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.clear();
        }
        if (null != mPresenter) {
            mPresenter.unsubscribe();
        }
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    protected int getLayoutRes() {
        return Integer.MIN_VALUE;
    }

    
    protected void initViews() {
    }


    
    protected void initEvents() {
    }

    /
    protected ArrayList<Parcelable> getArrayData(Parcelable... args) {
        return null;
    }

    @Override
    public final Bundle get4PListBundle(@Nullable ArrayList<? extends Parcelable> value) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PARAM_DATA, value);
        return bundle;
    }

    
    protected ArrayList<String> getArrayData(String... args) {

        return null;
    }

    @Override
    public final Bundle get4SListBundle(@Nullable ArrayList<String> value) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(PARAM_DATA, value);
        return bundle;
    }

    /

    @Nullable
    private final void targetFragment(String clsFragment, Bundle value, int type, boolean needResult) {
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        value.putInt(TYPE, type);
        value.putString(CLASS, clsFragment);
        intent.putExtras(value);
        if (needResult) {
            startActivityForResult(intent, Activity.RESULT_FIRST_USER);
        } else {
            startActivity(intent);
        }
    }
    /
    protected final Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mActivity, id);
    }


    
    protected final ColorStateList getColorList(@ColorRes int id) {
        return ColorStateList.valueOf(ContextCompat.getColor(mActivity, id));
    }

    
    protected final int getDimension(@DimenRes int id) {
        return (int) (getResources().getDimension(id) + 0.5f);
    }

    protected final void setChildFragement(@IdRes int containerViewId, BaseFragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    public void setTouchableSpan(TextView tv) {
        
        tv.setMovementMethod(new LinkTouchMovementMethod());
        
        tv.setHighlightColor(ContextCompat.getColor(mActivity, android.R.color.transparent));
    }


    
    protected boolean needAddToBackStack() {
        return false;
    }

    
    public int[] getAnimations() {
        return null;
    }

    
    public void onBackPressed() {

        mActivity.finish();
        
    }


    
    protected void setTabModel(TabLayout tabLayout) {
        if (tabLayout.getChildCount() > 4) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }

    protected final void clipboardText(String content) {
        
        
        ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(content);
        Toast.makeText(mActivity, getString(R.string.clip_over), Toast.LENGTH_SHORT).show();
    }


    protected final void setLeftDrawable(TextView tv, @DrawableRes int drawableID, int size) {
        Drawable drawable = ContextCompat.getDrawable(mActivity, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    public final void setLeftDrawable(TextView tv, @DrawableRes int drawableID) {
        int size = getSize(tv);
        Drawable drawable = ContextCompat.getDrawable(mActivity, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(drawable, null, null, null);
    }


    private final int getSize(TextView tv) {
        return (int) (tv.getTextSize() + 0.5f);
    }

    protected final void setRightDrawable(TextView tv, @DrawableRes int drawableID, int size) {
        Drawable drawable = ContextCompat.getDrawable(mActivity, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(null, null, drawable, null);
    }

    protected final void setRightDrawable(TextView tv, @DrawableRes int drawableID) {
        int size = getSize(tv);
        Drawable drawable = ContextCompat.getDrawable(mActivity, drawableID);
        drawable.setBounds(0, 0, size, size);
        tv.setCompoundDrawables(null, null, drawable, null);
    }


    
    protected final void finnishAnim() {
        mActivity.overridePendingTransition(R.anim.push_l2r_in, R.anim.push_l2r_out);
    }

    public final void showMsg(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public final void showMsg(int msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }


    public static final boolean isLoading = false;
    private QQDialogLoading mQQDialogLoading;

    @Override
    public void showLoading() {
        if (null != mQQDialogLoading) {
            mQQDialogLoading.showLoading();
        }
    }

    @Override
    public void setTitle(String title) {
        mQQDialogLoading = new QQDialogLoading(mActivity, title);
    }

    @Override
    public void hindeLoading() {
        if (null != mQQDialogLoading) {
            mQQDialogLoading.hindeLoading();
        }
    }

    public void showResult(String info) {
        if (TextUtils.isEmpty(info) || null == getActivity()) {
            return;
        }
        if (null == mToast) {
            mToast = Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(info);
        }
        mToast.show();
    }
    

    
    public final static Bundle setBundle(Parcelable paramData, String clazz) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA, paramData);
        bundle.putInt(TYPE, FragmentContract.BA_TPARCELABLE);
        bundle.putString(CLASS, clazz);
        return bundle;
    }


    public final static Bundle setArrayStrBundle(ArrayList<String> paramData, String clazz) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(DATA, paramData);
        bundle.putInt(TYPE, BA_TSTRINGLIST);
        bundle.putString(CLASS, clazz);
        return bundle;
    }


    public final static Bundle setArrayParBundle(ArrayList<? extends Parcelable> paramData, String clazz) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA, paramData);
        bundle.putInt(TYPE, BA_TPARCELABLELIST);
        bundle.putString(CLASS, clazz);
        return bundle;
    }

    public final static Bundle setBundle(String paramData, String clazz) {
        Bundle bundle = new Bundle();
        bundle.putString(DATA, paramData);
        bundle.putInt(TYPE, BA_TSTRING);
        bundle.putString(CLASS, clazz);
        return bundle;
    }


    public final static Bundle setEmptyBundle(String clazz) {
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, NONO);
        bundle.putString(CLASS, clazz);
        return bundle;
    }

    protected final static Intent getParIntent(Context context, Parcelable paramData, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(setBundle(paramData, clazz));
        return intent;
    }

    protected final static Intent getArrayStrIntent(Context context, ArrayList<String> paramData, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(setArrayStrBundle(paramData, clazz));
        return intent;
    }

    protected final static Intent getArrayParIntent(Context context, ArrayList<? extends Parcelable> paramData, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(setArrayParBundle(paramData, clazz));
        return intent;
    }

    protected final static Intent getStrIntent(Context context, String paramData, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(setBundle(paramData, clazz));
        return intent;
    }

    protected final static Intent getEmptyIntent(Context context, String clazz) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(setEmptyBundle(clazz));
        return intent;
    }
}
