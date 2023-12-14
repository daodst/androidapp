

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.NewAssertBean;

import java.util.List;
import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class OtherApiRepository extends HttpDataRepositoryBase {
    private static OtherApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private OtherApiRepository() {
    }

    public static OtherApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (OtherApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new OtherApiRepository();
                }
            }
        }
        return mDataRepository;
    }

    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getOtherRetrofit();
    }

    
    public void getBanlance(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("bal", params,netResponse);
    }
    
    public void getTkList(Map params, ApiNetResponse<List<NewAssertBean>> netResponse) {
        toRequestApi("tklist", params,netResponse);
    }
    
    public void getTkDetail(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("tkinfo", params,netResponse);
    }
    
    public void getTransList(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("trade", params,netResponse);
    }
    
    public void getCreatTrand(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("ivk", params,netResponse);
    }
    
    public void creatAssest(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("token", params,netResponse);
    }
    
    public void creatTxid(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("crtTx", params,netResponse);
    }
    
    public void getBlock(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("block", params,netResponse);
    }
    
    public void getBlockDetail(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("qry", params,netResponse);
    }
    
    public void getDiyaDetail(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("awardpreview", params,netResponse);
    }
    
    public void getWithdrawList(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("withdrawlist", params,netResponse);
    }
    
    public void getAwardList(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("tokenawddetail", params,netResponse);
    }
    
    public void getMinner(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("minner", params,netResponse);
    }
    
    public void getAward(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("award", params,netResponse);
    }
    
    public void getWithdraw(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("withdraw", params,netResponse);
    }
    
    public void getTokenDetail(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("tkinfo", params,netResponse);
    }
    
    public void getTrend(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("trend", params,netResponse);
    }
    
    public void checkUrl(Map params, ApiNetResponse<Map<String, String>> netResponse) {
        toRequestApi("trend", params,netResponse);
    }
}
