

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.AccountResourceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBean;
import com.wallet.ctc.model.blockchain.QuotesBean;
import com.wallet.ctc.model.blockchain.TrxBlockHeardBean;
import com.wallet.ctc.model.blockchain.TrxSeachBean;
import com.wallet.ctc.model.blockchain.TrxSeachDeatilBean;
import com.wallet.ctc.model.blockchain.TrxTransactionResultBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;



public interface TrxService {

    
    @FormUrlEncoded
    @POST("wallet/getaccount")
    Observable<List<QuotesBean>> getAccount(@FieldMap Map<String, Object> map, @HeaderMap Map<String,String> data);

    
    @GET("https://apilist.tronscan.org/api/account")
    Observable<BaseTrxBanlanceBean> getAccounts(@QueryMap Map<String, Object> map);

    @POST("https://apilist.tronscan.org/api/search-main")
    Observable<List<TrxSeachBean>> seachCoin(@Body RequestBody body);

    @GET("https://apilist.tronscan.org/api/contract")
    Observable<TrxSeachDeatilBean> getCoinDetail(@QueryMap Map<String, Object> map);

    
    @GET("v1/accounts/{p}/transactions")
    Observable<BaseTrxBean> getTransactions(@Path("p") String address, @QueryMap Map<String,Object> map, @HeaderMap Map<String,String> data);
    
    @GET("v1/accounts/{p}/transactions/trc20")
    Observable<BaseTrxBean> getTrc20Transactions(@Path("p") String address,@QueryMap Map<String,Object> map,@HeaderMap Map<String,String> data);

    
    @GET("wallet/getnowblock")
    Observable<TrxBlockHeardBean> getNowBlock(@HeaderMap Map<String,String> data);
    
    @Headers("Content-Type: application/json")
    @POST("wallet/broadcasttransaction")
    Observable<TrxTransactionResultBean> sendTransaction(@Body RequestBody body, @HeaderMap Map<String,String> data);

    
    @FormUrlEncoded
    @POST("wallet/getaccountnet")
    Observable<List<QuotesBean>> getAccountNet(@FieldMap Map<String, Object> map);


    
    @Headers("Content-Type: application/json")
    @POST("wallet/getaccountresource")
    Observable<AccountResourceBean> getAccountResource(@Body RequestBody body, @HeaderMap Map<String,String> data);

    
    @FormUrlEncoded
    @POST("wallet/createtransaction")
    Observable<BaseEntity> createTransaction(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST("wallet/transactions")
    Observable<BaseEntity> getTransactionsList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("wallet/assets")
    Observable<BaseEntity> getAssetsList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("wallet/broadcasttransaction")
    Observable<BaseEntity> broadcastTransaction(@FieldMap Map<String, Object> map);



}
