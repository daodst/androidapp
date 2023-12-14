

package common.app.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;


public interface IBase {

    
    public interface IUiBase {
        
        void showAlertDialog(String content, View.OnClickListener okBtnListener, View.OnClickListener cancelBtnListener);

        
        void dismissDialog();

        
        void showToast(String content);

        
        void showLoadingDialog(String content);

        
        void dismissLoadingDialog();
    }


    public interface IVmBase<VM extends BaseViewModel> {

        
        void initParam();

        
        void initView(@Nullable View view);

        
        void initData();

        
        <T> T getViewDataBinding();


        
        void initViewModel();

        
        void registViewModelUiControl();

        
        public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls);

        public <T extends ViewModel> T createViewModel(@NonNull Fragment fragment, Class<T> cls);

        public <T extends ViewModel> T createViewModel(@NonNull DialogFragment fragment, Class<T> cls);

        
        VM getViewModel();
    }
}
