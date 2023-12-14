

package com.wallet.ctc.api.me;

import com.wallet.ctc.base.BaseEntity;

import java.util.List;
import java.util.Map;

import common.app.base.model.http.bean.Result;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;



public interface MeService {
    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_ARTICLE_LIST)
    Observable<BaseEntity> getArticleList(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_HELP_LIST)
    Observable<BaseEntity> getHelpList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_POST_ARTICLE_LIST)
    Observable<BaseEntity> getPostArticleList(@FieldMap Map<String, Object> map);

    
    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_POST_GROUP_ARTICLE_LIST)
    Observable<BaseEntity> getPostGroupArticleList(@FieldMap Map<String, Object> map);
    

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_POST_ARTICLE)
    Observable<BaseEntity> addPostArticle(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.EDIT_POST_ARTICLE)
    Observable<BaseEntity> editPostArticle(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST(MeHttpUtil.GET_TXID)
    Observable<BaseEntity> creatTxid(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_LIHAOLIKONG)
    Observable<BaseEntity> getLihaokong(@FieldMap Map<String ,Object> map);

    
    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_POST_GROUP_ARTICLE)
    Observable<BaseEntity> addPostGroupArticle(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.EDIT_POST_GROUP_ARTICLE)
    Observable<BaseEntity> editPostGroupArticle(@FieldMap Map<String, Object> map);
    


    
    @FormUrlEncoded
    @POST(MeHttpUtil.LOGIN_REGIST)
    Observable<BaseEntity> loginRegist(@FieldMap Map<String, Object> map);



    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_POST_ARTICLE_DETAIL)
    Observable<BaseEntity> getPostArticleDetail(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.DEL_POST_ARTICLE)
    Observable<BaseEntity> delPostArticle(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_ARTICLE)
    Observable<BaseEntity> getArticle(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_SHARE_DATA)
    Observable<BaseEntity> getShare(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_ARTICLE_INFO)
    Observable<BaseEntity> getArticleInfo(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_TRANSFER)
    Observable<BaseEntity> getTranfer(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_TRANSFER_HASH)
    Observable<BaseEntity> getTranferHash(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_KLINE)
    Observable<BaseEntity> getKline(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_HELP_INFO)
    Observable<BaseEntity> getHelpInfo(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BANLANCE)
    Observable<BaseEntity> getBanlance(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BANLANCES)
    Observable<BaseEntity> getBanlances(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_PRICE)
    Observable<BaseEntity> getPrice(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_MARKET_PRICE)
    Observable<BaseEntity> getMarketPrice(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.PUSH_CON)
    Observable<BaseEntity> pushCon(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.SEARCH_TOKEN)
    Observable<BaseEntity> seachToken(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_TRADSACTION_LIST)
    Observable<BaseEntity> getTransctionList(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_ADDRESS)
    Observable<BaseEntity> addAddress(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CHAINS)
    Observable<BaseEntity> getChains(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_WALLET_QUOTES_LIST)
    Observable<BaseEntity> getQuotesList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_NONCE)
    Observable<BaseEntity> getNonce(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST("basic/version/GetVersion")
    Observable<BaseEntity> getVersion(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.NOTICE_LIST)
    Observable<BaseEntity> getNoticeList(@FieldMap Map<String ,Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.NOTICE_DETAIL)
    Observable<BaseEntity> getNoticeDetail(@FieldMap Map<String ,Object> map);

    
    @FormUrlEncoded
    @POST("basic/banner/GetBanner")
    Observable<BaseEntity> getAdvertList(@FieldMap Map<String ,Object> map);

    @Multipart
    @POST("user/file/Uploads")
    Observable<Result<List<String>>> uploadFiles(@PartMap Map<String, RequestBody> map,
                                                 @Part List<MultipartBody.Part> files);





    
    @GET(MeHttpUtil.GET_GAS_PRICE)
    Observable<BaseEntity> getGasPrice(@QueryMap Map<String ,Object> map, @HeaderMap Map<String, Object> headermap);

    
    @GET(MeHttpUtil.GET_GAS_DEF_PRICE)
    Observable<BaseEntity> getGasDefPrice(@QueryMap Map<String ,Object> map,@HeaderMap Map<String, Object> headermap);

    @GET(MeHttpUtil.GET_GAS_DEF_PRICE2)
    Observable<BaseEntity> getGasDefPrice2(@QueryMap Map<String ,Object> map,@HeaderMap Map<String, Object> headermap);


    @FormUrlEncoded
    @POST(MeHttpUtil.CREAT_ETH)
    Observable<BaseEntity> creatEth(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_GAS)
    Observable<BaseEntity> getGas(@FieldMap Map<String ,Object> map,@HeaderMap Map<String, Object> headermap);
    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_TRANSFER_BTC)
    Observable<BaseEntity> getTransfer(@FieldMap Map<String ,Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_WEEK_TRANSFER)
    Observable<BaseEntity> getWeekTransfer(@FieldMap Map<String ,Object> map,@HeaderMap Map<String, Object> headermap);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_DEF_ASSETS)
    Observable<BaseEntity> getDefAssert(@FieldMap Map<String ,Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_ADDRESS)
    Observable<BaseEntity> getAddress(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_ETH_CREATE_PRICE)
    Observable<BaseEntity> getEthCreatPrice(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.SET_ADDRESS)
    Observable<BaseEntity> setAddress(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_SYS_ADDRESS)
    Observable<BaseEntity> getSysAddress(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_LIST)
    Observable<BaseEntity> getCandyList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_LINCANDY_LIST)
    Observable<BaseEntity> getLinCandyList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_SEND_CANDY_LIST)
    Observable<BaseEntity> getSendCandyList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_INFO)
    Observable<BaseEntity> getCandyInfo(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_SEND)
    Observable<BaseEntity> getCandySend(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY)
    Observable<BaseEntity> getCandy(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_DETAIL)
    Observable<BaseEntity> getCandyDetail(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_SXF)
    Observable<BaseEntity> getCandysxf(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CANDY_SYDB)
    Observable<BaseEntity> getCandysy(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_POSTER_LIST)
    Observable<BaseEntity> getPostrList(@FieldMap Map<String, Object> map);



    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_COMMAND_LIST)
    Observable<BaseEntity> getCommandList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_ARTICLE_LIKE)
    Observable<BaseEntity> getAddArticleLike(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_COMMAND)
    Observable<BaseEntity> addCommand(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.DEL_COMMAND)
    Observable<BaseEntity> delCommand(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.DEL_COMMAND_REPLY)
    Observable<BaseEntity> delCommandReply(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_COMMAND_REPLY)
    Observable<BaseEntity> addCommandReply(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_COMMAND_REPLY_REPLY)
    Observable<BaseEntity> addCommandReplyReply(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_COMMAND_REPLY_LIST)
    Observable<BaseEntity> getCommandReplyList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_COMMAND_DETAIL)
    Observable<BaseEntity> getCommandDetail(@FieldMap Map<String, Object> map);

    

    @FormUrlEncoded
    @POST(MeHttpUtil.CHECK_USER_AUTH)
    Observable<BaseEntity> checkUserAutk(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST(MeHttpUtil.GET_USER_AUTH)
    Observable<BaseEntity> getUserAutk(@FieldMap Map<String, Object> map);

    
    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_BANLANCE)
    Observable<BaseEntity> getBtcBanlance(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_WEEK_DAY)
    Observable<BaseEntity> getBtcWeekday(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_TRADSACTION_LIST)
    Observable<BaseEntity> getBtcTranscationList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_MINI_BANLANCE)
    Observable<BaseEntity> getBtcUsdtBanlance(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_MINI_WEEK_DAY)
    Observable<BaseEntity> getBtcUsdtWeekday(@FieldMap Map<String, Object> map);
    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_MINI_TRADSACTION_LIST)
    Observable<BaseEntity> getBtcUsdtTranscationList(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_BTC_ADDRESS)
    Observable<BaseEntity> addBtcAddress(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_TRANSFER)
    Observable<BaseEntity> getBtcTransfer(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_FEES)
    Observable<BaseEntity> getBtcFees(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_BTC_TRANSFER_DATA)
    Observable<BaseEntity> getBtcData(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_CURRENCYS)
    Observable<BaseEntity> getCurrencys(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_LOCALS)
    Observable<BaseEntity> getlocals(@FieldMap Map<String, Object> map);



    
    @FormUrlEncoded
    @POST(MeHttpUtil.ADD_XRP_ADDRESS)
    Observable<BaseEntity> addXrpAddress(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_TRANS_FEE)
    Observable<BaseEntity> getXrpTransFee(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_TRANS_SEQUENCE)
    Observable<BaseEntity> getXrpTransSequence(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_BALANCE)
    Observable<BaseEntity> getXrpBanlance(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_TOKEN_BALANCE)
    Observable<BaseEntity> getXrpTokenBanlance(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_TRNAS_HISTORY)
    Observable<BaseEntity> getXrpTransctionList(@FieldMap Map<String, Object> map);


    
    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_WEEK_TRANSFER)
    Observable<BaseEntity> getXrpWeekTransfer(@FieldMap Map<String ,Object> map);

    @FormUrlEncoded
    @POST(MeHttpUtil.GET_XRP_TOKEN_WEEK_TRANSFER)
    Observable<BaseEntity> getXrpTokenWeekTransfer(@FieldMap Map<String ,Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.SEND_XRP_TRANSFER)
    Observable<BaseEntity> sendXrpTranfer(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST(MeHttpUtil.SEARCH_XRP_TOKEN)
    Observable<BaseEntity> seachXrpToken(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(MeHttpUtil.ETHGASLIMIT)
    Observable<BaseEntity> getGasLimit(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);

    @FormUrlEncoded
    @POST(MeHttpUtil.ETHGASPRICE)
    Observable<BaseEntity> getMaxPriorityFeePerGas(@FieldMap Map<String, Object> map,@HeaderMap Map<String, Object> headermap);



}
