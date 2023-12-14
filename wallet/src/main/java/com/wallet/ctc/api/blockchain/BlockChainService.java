

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.EvmosTransRecordsBean;
import com.wallet.ctc.model.blockchain.QuotesBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;



public interface BlockChainService {

    
    @FormUrlEncoded
    @POST("index.php")
    Observable<List<QuotesBean>> getQuotesList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("index.php")
    Observable<List<QuotesBean>> getSeachQuotesList(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST("peer")
    Observable<BaseEntity> getNode(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("tklist")
    Observable<BaseEntity> getTkList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("tkinfo")
    Observable<BaseEntity> getTkDetail(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("bal")
    Observable<BaseEntity> getBanlance(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST("trade")
    Observable<BaseEntity> getTransList(@FieldMap Map<String, Object> map);

    
    @POST()
    Observable<EvmosTransRecordsBean> getEvmosHistory(@Url String url, @Body RequestBody body);

    
    @FormUrlEncoded
    @POST("ivk")
    Observable<BaseEntity> getCreatTrand(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("token")
    Observable<BaseEntity> creatAssest(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("crtTx")
    Observable<BaseEntity> creatTxid(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("block")
    Observable<BaseEntity> getBlock(@FieldMap Map<String, Object> map);


    

    @FormUrlEncoded
    @POST("qry")
    Observable<BaseEntity> getBlockDetail(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("awardpreview")
    Observable<BaseEntity> getDiyaDetail(@FieldMap Map<String, Object> map);


    

    @FormUrlEncoded
    @POST("withdrawlist")
    Observable<BaseEntity> getWithdrawList(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("tokenawddetail")
    Observable<BaseEntity> getAwardList(@FieldMap Map<String, Object> map);


    

    @FormUrlEncoded
    @POST("minner")
    Observable<BaseEntity> getMinner(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("award")
    Observable<BaseEntity> getAward(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("withdraw")
    Observable<BaseEntity> getWithdraw(@FieldMap Map<String, Object> map);


    

    @FormUrlEncoded
    @POST("tkinfo")
    Observable<BaseEntity> getTokenDetail(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST("trend")
    Observable<BaseEntity> getTrend(@FieldMap Map<String, Object> map);
    

    @POST
    Observable<Object> checkUrl(@Url String url, @Body RequestBody data);

}
