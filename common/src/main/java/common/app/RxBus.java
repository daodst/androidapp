

package common.app;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;




public class RxBus {

    private static final String TAG = "RxBus";

    private static volatile RxBus sInstance;

    public static RxBus getInstance() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    
    private PublishSubject<Object> mEventBus = PublishSubject.create();

    public void post(Object event) {
        mEventBus.onNext(event);
    }

    public Observable<Object> toObservable() {
        return mEventBus;
    }

    
    public static Observer defaultObserver() {

        return new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object value) {
                Log.d(TAG, "New event received: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "What is this? Please solve this as soon as possible!", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Duty off!!!");
            }
        };
    }
}
