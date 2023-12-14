

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.DogeBalanceBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class DogeApi {
    private DogeService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public DogeApi() {
        Retrofit retrofit = HttpMethods.getInstance().getDogeRetrofit();
        mService = retrofit.create(DogeService.class);
    }
    public Observable<DogeBalanceBean> getDogeBalance(String addr) {
        return mService.getDogeBalance(addr).subscribeOn(Schedulers.io());
    }
    public Observable<BchEstimateFeeBean> getDogeestimateFee(int block) {
        
        return mService.getGogeestimateFee(block).subscribeOn(Schedulers.io());
    }

    public Observable<List<BchTxIdBean>> getDogeTxId(String params) {
        
        return mService.getDogeTxId(params).subscribeOn(Schedulers.io());
    }

    public Observable<BchTransResultBean> trans(String sign) {
        
        return mService.trans(sign).subscribeOn(Schedulers.io());
    }
}
