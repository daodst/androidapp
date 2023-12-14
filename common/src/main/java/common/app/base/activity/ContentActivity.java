

package common.app.base.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import common.app.ActivityRouter;
import common.app.R;
import common.app.base.base.BaseActivity;
import common.app.base.base.BaseFragment;
import common.app.base.base.FragmentContract;
import common.app.im.event.AccountError;
import common.app.utils.SpUtil;


public class ContentActivity extends BaseActivity {

    private static final String TAG = "ContentActivity";
    private BaseFragment mBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_content);
        String clazz = getIntent().getStringExtra(CLASS);
        if (null != clazz) {
            mBaseFragment = set(clazz, getIntent());
        } else {
            if (SpUtil.isFirst(this)) {
                
                
            } else {
                
                
            }
        }
    }

    
    private BaseFragment set(String clazz, Intent intent) {

        switch (intent.getIntExtra(TYPE, 0)) {
            case FragmentContract.BA_SERIALIZABLE: {
                return setSeFragment(clazz, intent.getSerializableExtra(DATA));
            }
            case FragmentContract.NONO: {
                return setFragment(clazz);
            }
            case FragmentContract.BA_TPARCELABLE: {
                return setFragment4P(clazz, intent.getParcelableExtra(DATA));

            }
            case FragmentContract.BA_TPARCELABLELIST: {
                return setFragment4PList(clazz, intent.getParcelableArrayListExtra(DATA));

            }
            case FragmentContract.BA_TSTRING: {
                return setFragment4S(clazz, intent.getStringExtra(DATA));

            }
            case FragmentContract.BA_TSTRINGLIST: {
                return setFragment4SList(clazz, intent.getStringArrayListExtra(DATA));

            }
            default:
                break;
        }
        return null;

    }

    @Override
    public void succeed(Object obj) {
        if (obj instanceof AccountError && null != mBaseFragment
                && !(mBaseFragment.getClass().getName().equals(ActivityRouter.Lg4e.F_SplashFragment))) {
            
            
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mBaseFragment) {
            mBaseFragment.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBaseFragment.onActivityResult(requestCode, resultCode, data);
    }

}
