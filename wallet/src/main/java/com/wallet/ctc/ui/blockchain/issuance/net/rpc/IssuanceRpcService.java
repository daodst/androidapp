

package com.wallet.ctc.ui.blockchain.issuance.net.rpc;

import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinItemResult;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinParam;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteParam;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPcResponseBody;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface IssuanceRpcService {
    
    @POST()
    Observable<WRPcResponseBody<WRPCVoteInfo>> getData(@Url String url, @Body WRPCVoteParam param);

    @POST()
    Observable<IssuanceCoinItemResult> getIssuanceCoinPageInfo(@Url String url, @Body IssuanceCoinParam param);

    @POST()
    Observable<IssuanceCoinItemResult> getIssuanceCoinPageInfo(@Url String url, @Body RequestBody param);

}
