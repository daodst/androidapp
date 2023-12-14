

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.DotBalanceBean;
import com.wallet.ctc.model.blockchain.DotFeeBean;
import com.wallet.ctc.model.blockchain.DotMaterialBean;
import com.wallet.ctc.model.blockchain.DotTransResultBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface DotService {

    @GET("accounts/{addr}/balance-info")
    Observable<DotBalanceBean> getDotBalance(@Path("addr") String xpubAddr);

    @POST("transaction/fee-estimate")
    Observable<DotFeeBean> getDotestimateFee(@Body RequestBody request);

    @GET("transaction/material?noMeta=true")
    Observable<DotMaterialBean> getDotMaterial();

    @POST("transaction")
    Observable<DotTransResultBean> trans(@Body RequestBody request);



}
