

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BchBalanceBean;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class BchApi {
    private BchService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public BchApi() {
        Retrofit retrofit = HttpMethods.getInstance().getBchRetrofit();
        mService = retrofit.create(BchService.class);
    }
    public Observable<BchBalanceBean> getBchBalance(String addr) {
        return mService.getBchBalance(addr).subscribeOn(Schedulers.io());
    }

    public Observable<BchEstimateFeeBean> getBCHestimateFee(String params) {
     
        return mService.getBCHestimateFee().subscribeOn(Schedulers.io());
    }

    public Observable<List<BchTxIdBean>> getBchTxId(String params) {
     
        return mService.getBchTxId(params, params, true).subscribeOn(Schedulers.io());
    }

    public Observable<BchTransResultBean> trans(String sign) {
        return mService.trans(sign).subscribeOn(Schedulers.io());
    }
}
