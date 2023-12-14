

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.MarketPriceBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface MarketPriceService {
    @POST("v2/market/tickers")
    Observable<MarketPriceBean> getPrice(@Body RequestBody body);

}
