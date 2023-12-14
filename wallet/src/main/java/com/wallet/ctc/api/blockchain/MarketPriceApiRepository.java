

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.MarketPriceBean;

import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class MarketPriceApiRepository extends HttpDataRepositoryBase {
    private static MarketPriceApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private MarketPriceApiRepository() {
    }

    public static MarketPriceApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (MarketPriceApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new MarketPriceApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getmMarketPriceRetrofit();
    }
    
    public void getPrice(String coinid,Map params, ApiNetResponse<MarketPriceBean> netResponse) {
        toRequestApi("v2/market/ticker/c"+coinid, params,netResponse);
    }

}
