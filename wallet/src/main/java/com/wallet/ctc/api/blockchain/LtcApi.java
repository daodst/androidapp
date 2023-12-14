

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.LtcBalanceBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class LtcApi {
    private LtcService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public LtcApi() {
        Retrofit retrofit = HttpMethods.getInstance().getmLtcRetrofit();
        mService = retrofit.create(LtcService.class);
    }
    public Observable<LtcBalanceBean> getLtcBalance(String addr) {
        return mService.getLtcBalance(addr).subscribeOn(Schedulers.io());
    }
    public Observable<BchEstimateFeeBean> getLTCestimateFee(String params) {
        
        return mService.getLTCestimateFee().subscribeOn(Schedulers.io());
    } public Observable<List<BchTxIdBean>> getLTCTxId(String params) {
        
        return mService.getLTCTxId(params).subscribeOn(Schedulers.io());
    }public Observable<BchTransResultBean> trans(String sign) {
        
        return mService.trans(sign).subscribeOn(Schedulers.io());
    }
}
