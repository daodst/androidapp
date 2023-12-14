

package com.wallet.ctc.api.blockchain;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.EvmosTransRecordsBean;
import com.wallet.ctc.model.blockchain.QuotesBean;
import com.wallet.ctc.util.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;



public class BlockChainApi {
    private BlockChainService mService;
    private BlockChainService mService2;
    private BlockChainService mService3;
    private RequestHelper mHelp=new RequestHelper();
    public BlockChainApi() {

        Retrofit retrofit = HttpMethods.getInstance().getMccRetrofit();
        mService = retrofit.create(BlockChainService.class);
        Retrofit retrofit2 = HttpMethods.getInstance().getDmRetrofit();
        mService2 = retrofit2.create(BlockChainService.class);
        Retrofit retrofit3 = HttpMethods.getInstance().getOtherRetrofit();
        mService3 = retrofit3.create(BlockChainService.class);
    }
    public Observable<List<QuotesBean>> getQuotesList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getQuotesList(params).subscribeOn(Schedulers.io());
    }

    public Observable<List<QuotesBean>> getSeachQuotesList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getSeachQuotesList(params).subscribeOn(Schedulers.io());

    }

    public Observable<BaseEntity> getTkList(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getTkList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getTkList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getTkList(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getTkList(params).subscribeOn(Schedulers.io());
        }
    }

    public Observable<BaseEntity> getTkDetail(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getTkDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getTkDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getTkDetail(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getTkDetail(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> getBanlance(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getBanlance(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getBanlance(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getBanlance(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getBanlance(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> getNode(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getNode(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTransList(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        LogUtil.d(new Gson().toJson(params));
        if(type== WalletUtil.DM_COIN){
            return mService2.getTransList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getTransList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getTransList(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getTransList(params).subscribeOn(Schedulers.io());
        }

    }


    
    private String getEvmosRpcUrl() {
        return SpUtil.getDefNode(WalletUtil.MCC_COIN);
    }

    private RequestBody getJsonRequestBody(Map<String,Object> paramsMap) {
        return common.app.base.model.http.config.HttpMethods.getInstance().getJsonRequestBody(paramsMap);
    }
    
    public Observable<EvmosTransRecordsBean> getEvmosHistory(int page, String address, String coinName){
        String url = getEvmosRpcUrl()+"chat/transfer/list";
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        paramsMap.put("page", page);
        if (!TextUtils.isEmpty(coinName)) {
            paramsMap.put("token", coinName);
        }
        return mService.getEvmosHistory(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCreatTrand(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getCreatTrand(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getCreatTrand(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getCreatTrand(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getCreatTrand(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> creatAssest(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.creatAssest(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.creatAssest(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.creatAssest(params).subscribeOn(Schedulers.io());
        }else {
            return mService.creatAssest(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> creatTxid(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.creatTxid(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.creatTxid(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.creatTxid(params).subscribeOn(Schedulers.io());
        }else {
            return mService.creatTxid(params).subscribeOn(Schedulers.io());
        }

    }
    public Observable<BaseEntity> getAward(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getAward(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getAward(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getAward(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getAward(params).subscribeOn(Schedulers.io());
        }

    }
    public Observable<BaseEntity> getDiyaDetail(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getDiyaDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getDiyaDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getDiyaDetail(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getDiyaDetail(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> getWithdrawList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getWithdrawList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getAwardList(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getAwardList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getAwardList(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getAwardList(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getAwardList(params).subscribeOn(Schedulers.io());
        }
    }

    public Observable<BaseEntity> getMinner(Map<String, Object> params) {
        return mService.getMinner(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getWithdraw(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getWithdraw(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getWithdraw(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getWithdraw(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getWithdraw(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> getTokenDetail(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getTokenDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getTokenDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getTokenDetail(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getTokenDetail(params).subscribeOn(Schedulers.io());
        }

    }


    public Observable<BaseEntity> getTrend(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        if(type== WalletUtil.DM_COIN){
            return mService2.getTrend(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getTrend(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getTrend(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getTrend(params).subscribeOn(Schedulers.io());
        }

    }

    public Observable<BaseEntity> getBlockData(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        params.put("method","sync");
        if(type== WalletUtil.DM_COIN){
            return mService2.getBlock(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getBlock(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getBlock(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getBlock(params).subscribeOn(Schedulers.io());
        }


    }
    
    public Observable<BaseEntity> getBlockNum(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        params.put("method","max");
        if(type== WalletUtil.DM_COIN){
            return mService2.getBlock(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getBlock(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getBlock(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getBlock(params).subscribeOn(Schedulers.io());
        }
    }

    public Observable<BaseEntity> getBlockDetail(Map<String, Object> params,int type) {
        params=mHelp.getMapParem(params);
        params.put("cmd","qryblo");
        if(type== WalletUtil.DM_COIN){
            return mService2.getBlockDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.MCC_COIN){
            return mService.getBlockDetail(params).subscribeOn(Schedulers.io());
        }else if(type== WalletUtil.OTHER_COIN){
            return mService3.getBlockDetail(params).subscribeOn(Schedulers.io());
        }else {
            return mService.getBlockDetail(params).subscribeOn(Schedulers.io());
        }
    }

    public Observable<Object> checkUrl(String url,String params) {
        RequestBody requestBody= RequestBody.create(MediaType.parse("application/json"),params);
        return mService.checkUrl(url,requestBody).subscribeOn(Schedulers.io());
    }

}
