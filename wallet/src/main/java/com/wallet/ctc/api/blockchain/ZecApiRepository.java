

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.ZecBalanceBean;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class ZecApiRepository extends HttpDataRepositoryBase {
    private static ZecApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private ZecApiRepository() {
    }

    public static ZecApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (ZecApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new ZecApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getmEtcRetrofit();
    }
    
    public void getBanlance(String address, ApiNetResponse<ZecBalanceBean> netResponse) {
        toGetRequestApi("api/v2/xpub/"+address, null,netResponse);
    }

}
