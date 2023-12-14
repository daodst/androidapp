

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.DogeBalanceBean;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class DogeApiRepository extends HttpDataRepositoryBase {
    private static DogeApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private DogeApiRepository() {
    }

    public static DogeApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (DogeApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new DogeApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getDogeRetrofit();
    }
    
    public void getBanlance(String address, ApiNetResponse<DogeBalanceBean> netResponse) {
        toGetRequestApi("api/v2/xpub/"+address, null,netResponse);
    }

}
