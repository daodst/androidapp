

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.DotBalanceBean;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class DotApiRepository extends HttpDataRepositoryBase {
    private static DotApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private DotApiRepository() {
    }

    public static DotApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (DotApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new DotApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getDotRetrofit();
    }
    
    public void getBanlance(String address, ApiNetResponse<DotBalanceBean> netResponse) {
        toGetRequestApi("accounts/"+address+"/balance-info", null,netResponse);
    }

}
