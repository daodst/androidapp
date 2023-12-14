

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.BchBalanceBean;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface BchService {

    @GET("api/v2/xpub/{xpubaddr}")
    Observable<BchBalanceBean> getBchBalance(@Path("xpubaddr") String xpubAddr);
    @GET("api/v2/estimatefee/2")
    Observable<BchEstimateFeeBean> getBCHestimateFee();


    @GET("api/v2/utxo/{addr}")
    Observable<List<BchTxIdBean>> getBchTxId(@Path("addr") String address, @Query("addr") String addr, @Query("confirmed") boolean confirmed);

    @GET("api/v2/sendtx/{sign}")
    Observable<BchTransResultBean> trans(@Path("sign") String sign);

    @POST("api/v2/sendtx")
    Observable<BchTransResultBean> transP(@Body RequestBody body);
}
