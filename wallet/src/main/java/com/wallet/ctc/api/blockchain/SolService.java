

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.SolBaseBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface SolService {
    
    @POST("/")
    Observable<SolBaseBean> getBalance(@Body RequestBody body);
    
    @POST("/")
    Observable<SolBaseBean> getRecentBlockhash(@Body RequestBody body);
    
    @POST("/")
    Observable<SolBaseBean> getEtcCount(@Body RequestBody body);
    
    @POST("/")
    Observable<SolBaseBean> toTrans(@Body RequestBody body);
    
    @POST("/")
    Observable<SolBaseBean> getBlockNumber(@Body RequestBody body);
}
