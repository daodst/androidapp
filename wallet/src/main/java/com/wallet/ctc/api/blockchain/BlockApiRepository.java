

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.api.me.MeHttpUtil;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.BtcBanlanceBean;
import com.wallet.ctc.model.blockchain.ETHBanlanceBean;
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.model.blockchain.XrpAssertBean;
import com.wallet.ctc.model.blockchain.XrpTokenBalanceBean;

import java.util.List;
import java.util.Map;

import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.callback.ApiNetResponse;
import retrofit2.Retrofit;


public class BlockApiRepository extends HttpDataRepositoryBase {
    private static BlockApiRepository mDataRepository = null;
    private final String TAG = "BlockApiRepository";

    private BlockApiRepository() {
    }

    public static BlockApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (BlockApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new BlockApiRepository();
                }
            }
        }
        return mDataRepository;
    }
    @Override
    protected Retrofit useRetrofit() {
        return  HttpMethods.getInstance().getRetrofit();
    }

    
    public void getBanlance(Map params,Map heard, ApiNetResponse<ETHBanlanceBean> netResponse) {
        toRequestApi(MeHttpUtil.GET_BANLANCE, params,heard, netResponse);
    }
    
    public void getBtcBanlance(Map params,ApiNetResponse<BtcBanlanceBean> netResponse) {
        toRequestApi(MeHttpUtil.GET_BTC_BANLANCE, params, netResponse);
    }
    
    public void getBtcUsdtBanlance(Map params,ApiNetResponse<BtcBanlanceBean> netResponse) {
        toRequestApi(MeHttpUtil.GET_BTC_MINI_BANLANCE, params, netResponse);
    }

    
    public void getXrpBanlance(Map params,ApiNetResponse<ETHBanlanceBean> netResponse) {
        toRequestApi(MeHttpUtil.GET_XRP_BALANCE, params, netResponse);
    }
    
    public void getXrpTokenBanlance(Map params,ApiNetResponse<List<XrpTokenBalanceBean>> netResponse) {
        toRequestApi(MeHttpUtil.GET_XRP_TOKEN_BALANCE, params, netResponse);
    }

    
    public void seachToken(Map params,Map heard, ApiNetResponse<List<EthAssertBean>> netResponse) {
        toRequestApi(MeHttpUtil.SEARCH_TOKEN, params,heard, netResponse);
    }
    
    public void seachXrpToken(Map params,ApiNetResponse<List<XrpAssertBean>> netResponse) {
        toRequestApi(MeHttpUtil.SEARCH_XRP_TOKEN, params, netResponse);
    }
}
