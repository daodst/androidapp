

package com.wallet.ctc.api.blockchain;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.AccountResourceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBean;
import com.wallet.ctc.model.blockchain.QuotesBean;
import com.wallet.ctc.model.blockchain.TrxBlockHeardBean;
import com.wallet.ctc.model.blockchain.TrxSeachBean;
import com.wallet.ctc.model.blockchain.TrxSeachDeatilBean;
import com.wallet.ctc.model.blockchain.TrxTransactionResultBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;



public class TrxApi {
    private TrxService mService;
    private RequestHelper mHelp=new RequestHelper();
    private Map<String,String> heard=new HashMap<>();
    public TrxApi() {

        Retrofit retrofit = HttpMethods.getInstance().getTrxRetrofit();
        mService = retrofit.create(TrxService.class);
    }
    public Observable<List<QuotesBean>> getAccount(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        heard.put("TRON-PRO-API-KEY", BuildConfig.TRON_PRO_API_KEY);
        return mService.getAccount(params,heard).subscribeOn(Schedulers.io());
    }

    public Observable<AccountResourceBean> getAccountResource(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        heard.put("TRON-PRO-API-KEY",BuildConfig.TRON_PRO_API_KEY);
        return mService.getAccountResource(requestBody,heard).subscribeOn(Schedulers.io());
    }

    public Observable<BaseTrxBanlanceBean> getAccounts(Map<String, Object> params) {
        return mService.getAccounts(params).subscribeOn(Schedulers.io());
    }

    public Observable<List<TrxSeachBean>> seachCoin(Map<String, Object> params) {
        String data=new Gson().toJson(params);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), data);
        return mService.seachCoin(requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<TrxSeachDeatilBean> getCoinDetail(Map<String, Object> params) {
        return mService.getCoinDetail(params).subscribeOn(Schedulers.io());
    }



    public Observable<BaseTrxBean> getTransactions(String address, Map<String,Object> map) {
        heard.put("TRON-PRO-API-KEY",BuildConfig.TRON_PRO_API_KEY);
        return mService.getTransactions(address,map,heard).subscribeOn(Schedulers.io());
    }
    public Observable<BaseTrxBean> getTrc20Transactions(String address,Map<String,Object> map) {
        heard.put("TRON-PRO-API-KEY",BuildConfig.TRON_PRO_API_KEY);
        return mService.getTrc20Transactions(address,map,heard).subscribeOn(Schedulers.io());
    }

    public Observable<TrxBlockHeardBean> getnowblock() {
        heard.put("TRON-PRO-API-KEY",BuildConfig.TRON_PRO_API_KEY);
        return mService.getNowBlock(heard).subscribeOn(Schedulers.io());
    }

    public Observable<TrxTransactionResultBean> sendTransaction(String params) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        heard.put("TRON-PRO-API-KEY",BuildConfig.TRON_PRO_API_KEY);
        return mService.sendTransaction(requestBody,heard).subscribeOn(Schedulers.io());
    }


    public Observable<List<QuotesBean>> getAccountNet(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getAccountNet(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> createTransaction(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.createTransaction(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTransactionsList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getTransactionsList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getAssetsList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getAssetsList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> broadcastTransaction(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.broadcastTransaction(params).subscribeOn(Schedulers.io());
    }
}
