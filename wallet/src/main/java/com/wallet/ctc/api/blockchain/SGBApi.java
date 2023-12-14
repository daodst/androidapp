

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BaseSgbTransHistory;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class SGBApi {
    private SgbService mService;
    public SGBApi() {
        Retrofit retrofit = HttpMethods.getInstance().getmSgbRetrofit();
        mService = retrofit.create(SgbService.class);
    }
    public Observable<BaseSgbTransHistory> getTransHistory(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url= BuildConfig.HOST_SGB+"api/scan/transfers";
        return mService.getTransHistory(url,requestBody).subscribeOn(Schedulers.io());
    }


}
