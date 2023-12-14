package common.app.base.activity;

import static common.app.base.base.FragmentContract.CLASS;
import static common.app.base.base.FragmentContract.DATA;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

import common.app.R;
import common.app.base.BaseActivity;
import common.app.utils.ActivityContainer;
import common.app.utils.LogUtil;



public class NewContentActivity extends BaseActivity {
    private static final String TAG = "NewContentActivity";
    protected static final String PARAM_DATA = "PARAM_DATA";

    private Fragment mBaseFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityContainer.getInstance().getList().add(this);
        setContentView(R.layout.activity_content);
        String clazz = getIntent().getStringExtra(CLASS);
        if (null != clazz) {
            mBaseFragment = set(clazz, getIntent());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mBaseFragment, mBaseFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    private Fragment set(String clazz, Intent intent) {
        return setSeFragment(clazz, intent.getSerializableExtra(DATA));
    }

    public Fragment setSeFragment(@Nullable String clazz, @Nullable Serializable value) {
        Fragment fragment = getBaseFragment(clazz);
        fragment.setArguments(get4SPBundle(value));
        return fragment;
    }

    private Fragment getBaseFragment(String clazz) {
        try {
            
            return (Fragment) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            LogUtil.i(TAG, Log.getStackTraceString(e));
            throw new RuntimeException(e);
        }
    }

    public final Bundle get4SPBundle(@Nullable Serializable value) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_DATA, value);
        return bundle;
    }
}
