

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.DotBalanceBean;
import com.wallet.ctc.model.blockchain.DotFeeBean;
import com.wallet.ctc.model.blockchain.DotMaterialBean;
import com.wallet.ctc.model.blockchain.DotTransResultBean;

import java.util.HashMap;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class DotApi {
    private DotService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public DotApi() {
        Retrofit retrofit = HttpMethods.getInstance().getDotRetrofit();
        mService = retrofit.create(DotService.class);
    }
    public Observable<DotBalanceBean> getDotBalance(String addr) {
        return mService.getDotBalance(addr).subscribeOn(Schedulers.io());
    }
    public Observable<DotFeeBean> getDotestimateFee(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.getDotestimateFee(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<DotMaterialBean> getDotMateria(String params) {
        return mService.getDotMaterial().subscribeOn(Schedulers.io());
    }

    public Observable<DotTransResultBean> trans(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.trans(requestBody).subscribeOn(Schedulers.io());
    }
}
