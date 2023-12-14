

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.ZECInfoBean;
import com.wallet.ctc.model.blockchain.ZecBalanceBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class ZecApi {
    private ZecService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public ZecApi() {
        Retrofit retrofit = HttpMethods.getInstance().getZecRetrofit();
        mService = retrofit.create(ZecService.class);
    }
    public Observable<ZecBalanceBean> getZecBalance(String addr) {
        return mService.getZecBalance(addr).subscribeOn(Schedulers.io());
    }
    public Observable<BchEstimateFeeBean> getZECestimateFee(String params) {
        
        return mService.getZECestimateFee().subscribeOn(Schedulers.io());
    }
    public Observable<List<BchTxIdBean>> getZECTxId(String params) {
        
        return mService.getZECTxId(params).subscribeOn(Schedulers.io());
    }
    public Observable<BchTransResultBean> trans(String sign) {
        
        return mService.trans(sign).subscribeOn(Schedulers.io());
    }

    public Observable<ZECInfoBean> getZecInfo() {
        
        return mService.getInfo().subscribeOn(Schedulers.io());
    }





}
