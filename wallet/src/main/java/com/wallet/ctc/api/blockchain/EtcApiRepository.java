

package com.wallet.ctc.api.blockchain;

import com.google.gson.Gson;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.EtcBalanceBean;

import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class EtcApiRepository extends HttpDataRepositoryBase {
    private static EtcApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private EtcApiRepository() {
    }

    public static EtcApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (EtcApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new EtcApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getmEtcRetrofit();
    }
    
    public void getBanlance(Map params, ApiNetResponse<EtcBalanceBean> netResponse) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(params));
        toRequestApi("/", requestBody,netResponse);
    }

}
