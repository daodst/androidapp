

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.DogeBalanceBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface DogeService {

    @GET("api/v2/xpub/{xpubaddr}")
    Observable<DogeBalanceBean> getDogeBalance(@Path("xpubaddr") String xpubAddr);
    @GET("api/v2/estimatefee/{block}")
    Observable<BchEstimateFeeBean> getGogeestimateFee(@Path("block") int block);

    @GET("api/v2/utxo/{dgubaddr}?confirmed=true")
    Observable<List<BchTxIdBean>> getDogeTxId(@Path("dgubaddr") String dgubAddr);

    @GET("api/v2/sendtx/{sign}")
    Observable<BchTransResultBean> trans(@Path("sign") String sign);
}
