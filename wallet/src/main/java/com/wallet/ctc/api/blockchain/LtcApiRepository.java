

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.LtcBalanceBean;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class LtcApiRepository extends HttpDataRepositoryBase {
    private static LtcApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private LtcApiRepository() {
    }

    public static LtcApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (LtcApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new LtcApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getmLtcRetrofit();
    }
    
    public void getBanlance(String address, ApiNetResponse<LtcBalanceBean> netResponse) {
        toGetRequestApi("api/v2/address/"+address,null,netResponse);
    }

}
