

package common.app.base.base;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.Unbinder;
import common.app.R;


public class BaseDialogFragment<T extends BasePresenter> extends DialogFragment {


    
    protected void initViews() {
    }

    
    protected void initEvents() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getArgument();
        initViews();
        initEvents();
    }

    public void getArgument() {
    }

    protected View getLayout(LayoutInflater inflater, ViewGroup container) {

        return null;
    }

    
    protected void setDialogBackground() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    private static final float DIALOG_WIDTH_PROPORTION = 0.85f;

    protected void resizeDialogSize() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(size);
        window.setLayout((int) (size.x * DIALOG_WIDTH_PROPORTION), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static final String CLASS = "CLASS";
    public static final String DATA = "DATA";

    protected void targetFragment(Class<?> clsActivity, String clsFragment, Parcelable data) {
        Intent intent = new Intent(mActivity, clsActivity);
        intent.putExtra(CLASS, clsFragment);
        intent.putExtra(DATA, data);
        startActivity(intent);
    }

    protected void clipboardText(String content) {
        
        
        ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(content);
        Toast.makeText(mActivity, getString(R.string.clip_over), Toast.LENGTH_SHORT).show();
    }

    protected T mPresenter;

    public void setPresenter(T presenter) {
        this.mPresenter = presenter;
    }

    protected Unbinder mUnbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.unsubscribe();
        }
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    
    public void showMsg(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    
    public void showMsg(int msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Context getContext() {
        return mActivity;
    }
}

