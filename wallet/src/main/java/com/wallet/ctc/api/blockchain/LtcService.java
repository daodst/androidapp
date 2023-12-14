

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.LtcBalanceBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface LtcService {

    @GET("api/v2/address/{addr}")
    Observable<LtcBalanceBean> getLtcBalance(@Path("addr") String Addr);
    @GET("api/v2/estimatefee/2")
    Observable<BchEstimateFeeBean> getLTCestimateFee();
    @GET("api/v2/utxo/{zpubaddr}?confirmed=true")
    Observable<List<BchTxIdBean>> getLTCTxId(@Path("zpubaddr") String xpubAddr);
    @GET("api/v2/sendtx/{sign}")
    Observable<BchTransResultBean> trans(@Path("sign") String sign);
}
