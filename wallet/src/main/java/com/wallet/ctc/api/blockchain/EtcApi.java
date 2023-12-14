

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.EtcBalanceBean;

import java.util.HashMap;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class EtcApi {
    private EtcService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public EtcApi() {
        Retrofit retrofit = HttpMethods.getInstance().getmEtcRetrofit();
        mService = retrofit.create(EtcService.class);
    }
    public Observable<EtcBalanceBean> getBalance(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.getBalance(requestBody).subscribeOn(Schedulers.io());
    }
    public Observable<EtcBalanceBean> getEtcGasPrice(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.getEtcGasPrice(requestBody).subscribeOn(Schedulers.io());
    }
    public Observable<EtcBalanceBean> getEtcCount(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.getEtcCount(requestBody).subscribeOn(Schedulers.io());
    }   public Observable<EtcBalanceBean> toTrans(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.toTrans(requestBody).subscribeOn(Schedulers.io());
    } public Observable<EtcBalanceBean> getBlockNumber(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.getBlockNumber(requestBody).subscribeOn(Schedulers.io());
    }
}
