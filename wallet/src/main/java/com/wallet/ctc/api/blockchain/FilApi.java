

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.FilChainHeadBean;
import com.wallet.ctc.model.blockchain.FilGasMsgBean;
import com.wallet.ctc.model.blockchain.FilTransResultBean;

import java.util.HashMap;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class FilApi {
    private FilService mService;
    private RequestHelper mHelp = new RequestHelper();
    private Map<String, String> heard = new HashMap<>();

    public FilApi() {
        Retrofit retrofit = HttpMethods.getInstance().getFilRetrofit();
        mService = retrofit.create(FilService.class);
    }

    public Observable<FilBalanceBean> getBalance(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getBalance(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<FilBalanceBean> getFilGasPrice(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getFilGasPrice(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<FilTransResultBean> toTrans(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.toTrans(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<FilChainHeadBean> getChainHead(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getChainHead(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<FilBalanceBean> getNonce(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getNonce(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<FilGasMsgBean> getGasEstimateMessageGas(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getGasEstimateMessageGas(requestBody).subscribeOn(Schedulers.io());
    }
}
