

package com.app.home.net.rpc;

import com.app.home.pojo.PayVoteStep1Info;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.app.home.pojo.rpc.RPcResponseBody;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface VoteRpcService {
    
    @POST()
    Observable<RPcResponseBody<RPCVoteInfo>> getData(@Url String url, @Body RPCVoteParam param);


    @GET()
    Observable<PayVoteStep1Info> getPayVoteStep1Info(@Url String url);


}
