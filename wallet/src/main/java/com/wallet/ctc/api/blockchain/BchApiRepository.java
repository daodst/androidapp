

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BchBalanceBean;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class BchApiRepository extends HttpDataRepositoryBase {
    private static BchApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private BchApiRepository() {
    }

    public static BchApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (BchApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new BchApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getBchRetrofit();
    }
    
    public void getBanlance(String address, ApiNetResponse<BchBalanceBean> netResponse) {
        toGetRequestApi("api/v2/xpub/"+address, null,netResponse);
    }

}
