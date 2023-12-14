

package common.app.base.model;

import android.content.Context;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import common.app.AppApplication;
import common.app.R;
import common.app.base.model.http.bean.Result;
import common.app.utils.LogUtil;
import common.app.utils.NetWorkUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



public class DataRepositoryBase {

    private Context mContext = AppApplication.getInstance().getApplicationContext();
    private final String TAG = "DataRepositoryBase";



    protected <T> void toSubscribe(Observable<T> observable, Consumer consumer) {
        Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(TAG,"service throable exception ");

                
                Result<Throwable> errorResult = new Result<Throwable>();
                errorResult.setStatus(0);

                NetWorkUtils netWorkUtils = new NetWorkUtils();
                if (!netWorkUtils.isConnected()) {
                    errorResult.setInfo(mContext.getString(R.string.net_work_unconnected));
                } else {
                    if (throwable instanceof SocketTimeoutException) {
                        errorResult.setInfo(mContext.getString(R.string.error_net_timeout));
                    } else if (throwable instanceof ConnectException) {
                        errorResult.setInfo(mContext.getString(R.string.error_net_connect_ex));
                    } else {
                        errorResult.setInfo(mContext.getString(R.string.error_unknow));
                    }
                }
                consumer.accept(errorResult);

                
                if (null != throwable) {
                    uploadExceptionToServer(Thread.currentThread(),throwable);
                }
            }
        };

        Action onComplete = new Action() {
            @Override
            public void run() throws Exception {
            }
        };
        Consumer<Disposable> onSubcribe = new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                NetWorkUtils netWorkUtils = new NetWorkUtils();
                if (!netWorkUtils.isConnected()) {
                    errorConsumer.accept(null);
                    disposable.dispose();
                    return;
                }
            }
        };


        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer,errorConsumer,onComplete,onSubcribe);
    }

    protected void uploadExceptionToServer(Thread thread, Throwable ex) {
    }

    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

}
