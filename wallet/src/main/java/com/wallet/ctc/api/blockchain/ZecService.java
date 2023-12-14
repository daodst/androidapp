

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.ZECInfoBean;
import com.wallet.ctc.model.blockchain.ZecBalanceBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ZecService {

    @GET("api/v2/xpub/{xpubaddr}")
    Observable<ZecBalanceBean> getZecBalance(@Path("xpubaddr") String xpubAddr);
    @GET("api/v2/estimatefee/2")
    Observable<BchEstimateFeeBean> getZECestimateFee();

    @GET("api/v2/utxo/{xpubaddr}?confirmed=true")
    Observable<List<BchTxIdBean>> getZECTxId(@Path("xpubaddr") String xpubAddr);

    
    @GET("api/v2")
    Observable<ZECInfoBean> getInfo();

    @GET("api/v2/sendtx/{sign}")
    Observable<BchTransResultBean> trans(@Path("sign") String sign);


}
