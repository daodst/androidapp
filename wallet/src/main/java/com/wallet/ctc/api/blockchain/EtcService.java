

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.EtcBalanceBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface EtcService {
    
    @POST("/")
    Observable<EtcBalanceBean> getBalance(@Body RequestBody body);
    
    @POST("/")
    Observable<EtcBalanceBean> getEtcGasPrice(@Body RequestBody body);
    
    @POST("/")
    Observable<EtcBalanceBean> getEtcCount(@Body RequestBody body);
    
    @POST("/")
    Observable<EtcBalanceBean> toTrans(@Body RequestBody body);
    
    @POST("/")
    Observable<EtcBalanceBean> getBlockNumber(@Body RequestBody body);
}
