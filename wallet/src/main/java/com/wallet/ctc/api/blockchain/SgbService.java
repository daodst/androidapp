

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.model.blockchain.BaseSgbTransHistory;
import com.wallet.ctc.model.blockchain.SgbDataBean;
import com.wallet.ctc.model.blockchain.SgbHeaderBean;
import com.wallet.ctc.model.blockchain.SgbRuntimeVersionBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface SgbService {

    @POST()
    Observable<SgbDataBean> getData(@Url String url, @Body RequestBody body);
    @POST()
    Observable<SgbDataBean<String>> getFinalizedHead(@Url String url, @Body RequestBody body);

    @POST()
    Observable<SgbDataBean<SgbHeaderBean>> getHeader(@Url String url, @Body RequestBody body);
    @POST()
    Observable<SgbDataBean<SgbRuntimeVersionBean>> getRuntimeVersion(@Url String url, @Body RequestBody body);
    @POST()
    Observable<BaseSgbTransHistory> getTransHistory(@Url String url, @Body RequestBody body);


}
