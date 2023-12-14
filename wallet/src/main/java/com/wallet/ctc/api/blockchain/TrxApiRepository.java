

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;

import java.util.HashMap;
import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class TrxApiRepository extends HttpDataRepositoryBase {
    private static TrxApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private TrxApiRepository() {
    }

    public static TrxApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (TrxApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new TrxApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getTrxRetrofit();
    }
    
    public void getBanlance(Map params, ApiNetResponse<BaseTrxBanlanceBean> netResponse) {
        Map<String,String> heard=new HashMap<>();
        heard.put("TRON-PRO-API-KEY", BuildConfig.TRON_PRO_API_KEY);
        toRequestApi("wallet/getaccount", params,heard,netResponse);
    }

}
