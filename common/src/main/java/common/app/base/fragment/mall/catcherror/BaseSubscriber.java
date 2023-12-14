

package common.app.base.fragment.mall.catcherror;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.app.utils.LogUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;



public class BaseSubscriber<T> implements Observer<T>{
    private static final String TAG = "BaseSubscriber";
    private Context mContext;
    private int mNetType;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private CompositeDisposable mDisposable;
    public BaseSubscriber(Context context) {
        mContext = context;
    }
    public BaseSubscriber(Context context, CompositeDisposable mDis) {
        mContext = context;
        mDisposable=mDis;
    }
    @Override
    public void onError(Throwable e) {
        if(null==mContext || (mContext instanceof Activity && ((Activity)mContext).isFinishing())){
            return;
        }
        ApiErrorHelper.handleCommonError(mContext, e);
    }

    @Override
    public void onNext(T t){
        try {
            if(null==mContext || (mContext instanceof Activity && ((Activity)mContext).isFinishing())){
                LogUtil.d(TAG,"");
                return;
            }
            onNexts(t);
        } catch (Exception e) {
            e.printStackTrace();
            

        }
    }

    public void onNexts(T t)  throws Exception{

    }

    @Override
    public void onSubscribe(Disposable d) {
        if(null!=mDisposable) {
            mDisposable.add(d);
        }
    }

    @Override
    public void onComplete() {

    }

}
