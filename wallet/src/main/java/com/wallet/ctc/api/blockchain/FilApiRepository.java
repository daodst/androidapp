

package com.wallet.ctc.api.blockchain;

import com.google.gson.Gson;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.FilBalanceBean;

import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


public class FilApiRepository extends HttpDataRepositoryBase {
    private static FilApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private FilApiRepository() {
    }

    public static FilApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (FilApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new FilApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getFilRetrofit();
    }
    
    public void getBanlance(Map params, ApiNetResponse<FilBalanceBean> netResponse) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(params));
        toRequestApi("/", requestBody,netResponse);
    }

}
