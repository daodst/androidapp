

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.model.blockchain.HostConfigBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TrustWalletService {

    @GET("v2/{coin}/transactions/{addr}")
    Observable<FilTransRecordBean> getTransactions(@Path("coin") String coinfullname, @Path("addr") String address);
    @GET("v2/{coin}/transactions/{addr}")
    Observable<FilTransRecordBean> getTransactionsToken(@Path("coin") String coinfullname, @Path("addr") String address, @Query("token") String appid);
    @GET("v2/{coin}/transactions/xpub/{addr}")
    Observable<FilTransRecordBean> getXpubTransactions(@Path("coin") String coinfullname, @Path("addr") String address);
    @GET("static/chains.json")
    Observable<List<HostConfigBean>> getWalletConFig();
}
