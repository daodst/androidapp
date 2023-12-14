

package common.app.utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> computation_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
    }


    public static <T> ObservableTransformer<T, T> computation_computation() {
        return upstream ->
                upstream.subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation());
    }

}
