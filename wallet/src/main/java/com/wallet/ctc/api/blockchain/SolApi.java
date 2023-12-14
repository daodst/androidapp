

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.SolBaseBean;

import java.util.HashMap;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class SolApi {
    private SolService mService;
    private RequestHelper mHelp = new RequestHelper();
    private Map<String, String> heard = new HashMap<>();

    public SolApi() {
        Retrofit retrofit = HttpMethods.getInstance().getmSolRetrofit();
        mService = retrofit.create(SolService.class);
    }

    public Observable<SolBaseBean> getBalance(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getBalance(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<SolBaseBean> getRecentBlockhash(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getRecentBlockhash(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<SolBaseBean> getEtcCount(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getEtcCount(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<SolBaseBean> toTrans(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.toTrans(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<SolBaseBean> getBlockNumber(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getBlockNumber(requestBody).subscribeOn(Schedulers.io());
    }
}
