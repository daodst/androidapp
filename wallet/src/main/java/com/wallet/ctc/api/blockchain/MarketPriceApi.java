

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.MarketPriceBean;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class MarketPriceApi {
    private MarketPriceService mService;
    public MarketPriceApi() {
        Retrofit retrofit = HttpMethods.getInstance().getmMarketPriceRetrofit();
        mService = retrofit.create(MarketPriceService.class);
    }
    public Observable<MarketPriceBean> getPrice(String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"), params);
        return mService.getPrice(requestBody).subscribeOn(Schedulers.io());
    }
}
