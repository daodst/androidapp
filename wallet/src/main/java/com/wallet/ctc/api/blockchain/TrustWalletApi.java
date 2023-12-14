

package com.wallet.ctc.api.blockchain;

import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.model.blockchain.HostConfigBean;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.AppApplication;
import common.app.base.fragment.mall.api.RequestHelper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class TrustWalletApi {
    private TrustWalletService mService;
    private RequestHelper mHelp = new RequestHelper();
    private Map<String, String> heard = new HashMap<>();
    private String url;

    public TrustWalletApi() {
        url = SettingPrefUtil.getTrustWalletHostApi(AppApplication.getContext());
        if (!url.endsWith("/")) {
            url = url + "/";
        }

        LogUtil.d("" + url + "||" + url.indexOf(":"));
        Retrofit retrofit = HttpMethods.getInstance().getTrustWalletRetrofit(url);
        mService = retrofit.create(TrustWalletService.class);
    }

    public Observable<FilTransRecordBean> getTransRecords(String coin, String addr) {
        return mService.getTransactions(coin, addr).subscribeOn(Schedulers.io());
    }

    public Observable<FilTransRecordBean> getTransRecordsToken(String coin, String addr, String token) {
        return mService.getTransactionsToken(coin, addr, token).subscribeOn(Schedulers.io());
    }

    public Observable<FilTransRecordBean> getXpubTransRecords(String coin, String addr) {
        return mService.getXpubTransactions(coin, addr).subscribeOn(Schedulers.io());
    }

    public Observable<List<HostConfigBean>> getWalletConFig() {
        return mService.getWalletConFig().subscribeOn(Schedulers.io());
    }
}
