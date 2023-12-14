

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.FilChainHeadBean;
import com.wallet.ctc.model.blockchain.FilGasMsgBean;
import com.wallet.ctc.model.blockchain.FilTransResultBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface FilService {
    
    @POST("/")
    Observable<FilBalanceBean> getBalance(@Body RequestBody body);
    @POST("/")
    Observable<FilBalanceBean> getFilGasPrice(@Body RequestBody body);
    @POST("/")
    Observable<FilTransResultBean> toTrans(@Body RequestBody body);
    @POST("/")
    Observable<FilChainHeadBean> getChainHead(@Body RequestBody body);
    @POST("/")
    Observable<FilBalanceBean> getNonce(@Body RequestBody body);
    @POST("/")
    Observable<FilGasMsgBean> getGasEstimateMessageGas(@Body RequestBody body);
}
