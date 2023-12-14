

package common.app.base.model.http.callback;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import common.app.AppApplication;
import common.app.R;
import common.app.base.BaseViewModel;
import common.app.base.model.http.bean.Request;
import common.app.base.model.http.bean.Result;
import common.app.base.model.http.exception.ExceptionHandle;
import common.app.base.model.http.exception.ResponseThrowable;
import common.app.utils.LogUtil;
import io.reactivex.observers.DisposableObserver;



public abstract class ApiNetResponse<T> extends DisposableObserver<Result<T>> {

    private final String TAG = "ApiNetResponse";

    
    private BaseViewModel mViewModel;
    private boolean mShowloading = false;

    
    private Request mRequestParmas;

    public void setRequestInfo(Request requestData) {
        mRequestParmas = requestData;
    }

    
    public Request getRequestInfo() {
        return mRequestParmas;
    }


    
    public ApiNetResponse(BaseViewModel viewModel) {
        if (null != viewModel) {
            this.mViewModel = viewModel;
        }
    }

    
    public ApiNetResponse(BaseViewModel viewModel, boolean showloading) {
        if (null != viewModel) {
            this.mViewModel = viewModel;
        }
        this.mShowloading = showloading;
    }


    
    public BaseViewModel getViewModel() {
        return mViewModel;
    }

    
    @Override
    public void onError(Throwable e) {
        processFaile(ExceptionHandle.handleException(e), null);
    }

    
    public void onDispose() {
        dismissLoadingDialog();
    }


    
    private ResponseThrowable getHandleException(String info, int statusCode) {
        return ExceptionHandle.handleException(new ResponseThrowable(new Exception(info), info, statusCode, false));
    }

    @Override
    public void onNext(Result<T> netResultData) {
        
        if (null == netResultData) {
            processFaile(getHandleException("http Data is null", 0), null);
            return;
        }

        
        if (netResultData.isDownload()) {
            
            if (netResultData.getStatus() == 1 || netResultData.getStatus() == 2) {
                
                String process = (String) netResultData.getData();
                onDownload(process, netResultData.getStatus());
                if (netResultData.getStatus() == 1) {
                    
                    processSuccess(netResultData.getData());
                }
            } else {
                onDownload(null, netResultData.getStatus());
                String info = TextUtils.isEmpty(netResultData.getInfo()) ? AppApplication.getContext().getResources().getString(R.string.common_save_faile) : netResultData.getInfo();
                processFaile(getHandleException(info, netResultData.getStatus()), null);
            }
            return;
        }
        
        if (netResultData.getStatus() != 1) {
            processFaile(getHandleException(netResultData.getInfo(), netResultData.getStatus()), netResultData.getData());
            return;
        }
        LogUtil.d("zzz",""+new Gson().toJson(netResultData));
        LogUtil.d("zzz",""+new Gson().toJson(netResultData.getData()));
        LogUtil.d("zzz",""+new Gson().toJson(netResultData.getMessage()));
        
        processSuccess(netResultData.getData());
    }


    @Override
    protected void onStart() {
        showLoadingDialog();
    }

    @Override
    public void onComplete() {
        dismissLoadingDialog();
    }


    
    public abstract void onSuccess(T data);

    
    private void processSuccess(T data) {
        dismissLoadingDialog();
        onSuccess(data);
    }


    
    private void processFaile(ResponseThrowable throwable, T data) {
        dismissLoadingDialog();
        String errroInfo = throwable.getErrorInfo();
        
        if (throwable.isNeedPush() && null != getRequestInfo()) {
            
            
            Request request = getRequestInfo();
            Context context = AppApplication.getInstance().getApplicationContext();
            if (getViewModel() != null) {
                context = getViewModel().getApplication();
            }
        }
        onFaile(errroInfo, data, throwable);
    }

    
    public void onFaile(String info, T data, Throwable throwable) {
        if (!TextUtils.isEmpty(info)) {
            showToast(info);
        }
    }

    
    public void onDownload(String process, int status) {
    }


    
    public void showLoadingDialog() {
        if (!mShowloading) {
            return;
        }
        if (null != mViewModel) {
            mViewModel.showLoadingDialog("");
        }
    }

    
    public void dismissLoadingDialog() {
        if (null != mViewModel) {
            mViewModel.dismissLoadingDialog();
        }
    }

    
    public void showToast(String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (mViewModel != null) {
            mViewModel.showToast(info);
        }
    }
}
