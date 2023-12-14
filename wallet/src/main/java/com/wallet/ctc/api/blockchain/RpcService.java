

package com.wallet.ctc.api.blockchain;

import com.google.gson.JsonObject;
import com.wallet.ctc.model.blockchain.AirDropRedPackate;
import com.wallet.ctc.model.blockchain.BasefeeBean;
import com.wallet.ctc.model.blockchain.BlockChainBtcBean;
import com.wallet.ctc.model.blockchain.BlockDepositBean;
import com.wallet.ctc.model.blockchain.DaodstTipsBean;
import com.wallet.ctc.model.blockchain.DefaultAssetsBean;
import com.wallet.ctc.model.blockchain.EvmosBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosBlockHeightOrRateBean;
import com.wallet.ctc.model.blockchain.EvmosBlockInfoBean;
import com.wallet.ctc.model.blockchain.EvmosChatBurnRatio;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.EvmosChatParamsBean;
import com.wallet.ctc.model.blockchain.EvmosChatToBurn;
import com.wallet.ctc.model.blockchain.EvmosChatUnPledgeAvailable;
import com.wallet.ctc.model.blockchain.EvmosGasBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayListBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayNumberCountBean;
import com.wallet.ctc.model.blockchain.EvmosGetIncomeHistoryBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeParamsBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosTokenInfo;
import com.wallet.ctc.model.blockchain.EvmosTokenRecordsBean;
import com.wallet.ctc.model.blockchain.EvmosTotalPledgeBean;
import com.wallet.ctc.model.blockchain.EvmosTransRecordsBean;
import com.wallet.ctc.model.blockchain.EvmosTransTypesBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.EvmosUsersBean;
import com.wallet.ctc.model.blockchain.EvmosVoteDuringBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.NodeDataWapper;
import com.wallet.ctc.model.blockchain.RedeemGatewayInfoEntity;
import com.wallet.ctc.model.blockchain.ResponseBodyT;
import com.wallet.ctc.model.blockchain.TransactionInfoBean;
import com.wallet.ctc.model.blockchain.ValidatorInfo;
import com.wallet.ctc.model.blockchain.XrpAccountInfoBean;
import com.wallet.ctc.model.blockchain.XrpAccountLinesBean;
import com.wallet.ctc.model.blockchain.XrpSubmitBean;
import com.wallet.ctc.model.me.SMLevelEntity;

import java.util.List;
import java.util.Map;

import common.app.im.model.base.ResponseBody;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


public interface RpcService {
    
    @POST()
    Observable<FilBalanceBean> getData(@Url String url, @Body RequestBody body);

    @POST()
    Observable<TransactionInfoBean> getTransactionData(@Url String url, @Body RequestBody body);

    @POST()
    Observable<BasefeeBean> getObjectData(@Url String url, @Body RequestBody body);


    @GET()
    Observable<BlockChainBtcBean> getBalance(@Url String url);

    
    @POST()
    Observable<XrpAccountInfoBean> getXrpBalance(@Url String url, @Body RequestBody body);

    @POST()
    Observable<XrpAccountLinesBean> getXrpTokenBalance(@Url String url, @Body RequestBody body);

    @POST()
    Observable<XrpSubmitBean> submitXrp(@Url String url, @Body RequestBody body);


    
    
    @GET()
    Observable<EvmosSeqAcountBean> getEvmosSeqAccountInfo(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<EvmosBalanceBean> getEvmosAllBalance(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<EvmosTokenInfo> getTokeninfo(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<ResponseBodyT<List<SMLevelEntity>>> getPledgeRank(@Url String url, @QueryMap Map<String, Object> params);


    
    @GET()
    Observable<EvmosOneBalanceBean> getEvmosOneBalance(@Url String url, @QueryMap Map<String, Object> params);

    
    @POST()
    Observable<EvmosTransRecordsBean> getEvmosHistory(@Url String url, @Body RequestBody body);

    
    @POST()
    Observable<EvmosTransTypesBean> getEvmosTransfTypes(@Url String url, @Body RequestBody body);

    
    @POST()
    Observable<EvmosTokenRecordsBean> getEvmosTokenHistory(@Url String url, @Body RequestBody body);

    
    @POST()
    Observable<EvmosHxResultBean> getEvmosHxResult(@Url String url, @Body RequestBody body);

    
    @POST()
    Observable<EvmosTransferResultBean> submitEvmosTransfer(@Url String url, @Body RequestBody body);

    
    @POST()
    Observable<EvmosGasBean> getEvmosGas(@Url String url, @Body RequestBody body);

    
    @GET()
    Observable<EvmosTotalPledgeBean> getEvmosTotalPledge(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<EvmosChatInfoBean> getEvmosChatInfo(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<DaodstTipsBean> getDaodstTips(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<NodeDataWapper> getNodeData(@Url String url);

    
    @GET()
    Observable<EvmosChatUnPledgeAvailable> getEvmosChatUnPledgeAvailable(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<EvmosPledgeParamsBean> getEvmosPledgeParams(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<EvmosChatParamsBean> getEvmosChatParams(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<EvmosChatToBurn> getEvmosChatToBurn(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<EvmosChatBurnRatio> getBurnRatio(@Url String url, @QueryMap Map<String, Object> params);


    
    @POST()
    Observable<EvmosGatewayBean> getEvmosGateway(@Url String url, @Body RequestBody body);

    
    @GET
    Observable<EvmosBlockInfoBean> getBlockHeight(@Url String url);

    
    @GET
    Observable<EvmosVoteDuringBean> getVoteDuring(@Url String url);

    @POST
    Observable<EvmosGatewayNumberCountBean> getGatewayNumberCount(@Url String url, @Body RequestBody body);

    @POST
    Observable<RedeemGatewayInfoEntity> getGatewayAddressInfo(@Url String url, @Body RequestBody body);

    @POST
    Observable<EvmosBlockHeightOrRateBean> getBlockHeightOrRate(@Url String url, @Body RequestBody body);

    
    @GET
    Observable<BlockDepositBean> getBlockDeposit(@Url String url);

    
    @POST()
    Observable<EvmosGatewayListBean> getEvmosGatewayList(@Url String url, @Body RequestBody body);

    
    @GET()
    Observable<EvmosChatFeeBean> getEvmosChatFeeSetting(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<DefaultAssetsBean> getDefaultAssets(@Url String url, @QueryMap Map<String, Object> params);

    
    @GET()
    Observable<String> getImGateWayPublickey(@Url String url, @QueryMap Map<String, Object> params);

    
    @POST()
    Observable<EvmosUsersBean> searchUserDatas(@Url String url, @Body RequestBody body);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> getChartInfo(@Url String url, @FieldMap Map<String, Object> body);

    
    @GET()
    Observable<JsonObject> getParamsVoteKeys(@Url String url);

    
    @GET()
    Observable<EvmosGetIncomeHistoryBean> getEvmosGetIncomeHistory(@Url String url, @QueryMap Map<String, Object> params);

    @GET()
    Observable<ValidatorInfo> getValidatorInfo(@Url String url, @QueryMap Map<String, Object> params);

    
    @POST()
    Observable<Boolean> checkCanAirDrop(@Url String url, @Body RequestBody body);


    
    @POST()
    Observable<AirDropRedPackate> doGetAirDrop(@Url String url, @Body RequestBody body);
}
