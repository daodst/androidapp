package com.wallet.ctc.ui.blockchain.issuance.net.rpc;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinItem;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinItemResult;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinPageInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteParam;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class SWalletRpcImpl implements ISWalletRpcNet {


    private static final String TAG = "SWalletRpcImpl";
    private RpcApi mApi;
    private IssuanceRpcService mService;

    public SWalletRpcImpl() {
        mApi = new RpcApi();
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(IssuanceRpcService.class);
    }

    
    private Observable<EvmosSeqAcountBean> getEvmosSeqAccountInfo(String address) {
        return mApi.getEvmosSeqAccountInfo("", address);
    }


    


    
    @Override
    public Observable<EvmosTransferResultBean> getRPCVoteInfo(WRPCVoteParam<?> param, String memo, WalletEntity entity, String pwd, IDoSdk doSdk) {
        String url = getEvmosRpcUrl() + "chat/tx/gas";
        return getEvmosSeqAccountInfo(entity.getAllAddress()).flatMap(bean -> {
            param.seq_detail = bean.data;
            return mService.getData(url, param).flatMap(info -> {
                if (!info.isOk()) {
                    return Observable.error(new RuntimeException(info.getInfo()));
                }
                WRPCVoteInfo data = info.getData();
                data.mSeqAcountBean = bean;
                return sign(entity.getAllAddress(), doSdk, data.mSeqAcountBean.data, memo, data.fee.amount, data.gas_used, entity, pwd);
            });
        });
    }

    @Override
    public Observable<WRPCVoteInfo> getGas(String walletAddress, WRPCVoteParam<?> param) {
        String url = getEvmosRpcUrl() + "chat/tx/gas";
        return getEvmosSeqAccountInfo(walletAddress).flatMap(bean -> {
            param.seq_detail = bean.data;
            return mService.getData(url, param).flatMap(info -> {
                if (!info.isOk()) {
                    return Observable.error(new RuntimeException(info.getInfo()));
                }
                WRPCVoteInfo data = info.getData();
                data.mSeqAcountBean = bean;
                return Observable.just(data);
            });
        });
    }


    public interface IDoSdk {
        byte[] doIt();
    }


    public Observable<EvmosTransferResultBean> sign(String address, IDoSdk iDoSdk,
                                                    EvmosSeqAcountBean.Data seqAccountBean,
                                                    String memo, String gasAmount, String gasLimit, WalletEntity wallet, String pwd) {
        return Observable.create((ObservableOnSubscribe<EvmosSignResult>) emitter -> {
            
            String publickey = new String(wallet.getmPublicKey());
            String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
            LogUtil.i("publickey=" + publickey + ", \nprivateKey=" + privateKey + ", \naddress=" + address);
            ChatSdk.setupCosmosWallet(address, publickey, privateKey);

            
            String gasAmount2 = gasAmount;
            if (TextUtils.isEmpty(gasAmount2)) {
                gasAmount2 = "100000000000000000";
            }
            String gasLimit2 = gasLimit;
            if (TextUtils.isEmpty(gasLimit2)) {
                gasLimit2 = "2000000";
            }
            String accountNum = seqAccountBean.account_number + "";
            String accountSeq = seqAccountBean.sequence + "";
            String remark = memo;
            LogUtil.i("accountNum=" + accountNum + ", accountSeq=" + accountSeq);
            ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, remark);

            
            byte[] signByte = iDoSdk.doIt();

            
            
            String jsonSignResult = new String(signByte);
            LogUtil.i("jsonSignResult=" + jsonSignResult);
            if (TextUtils.isEmpty(jsonSignResult)) {
                emitter.onNext(new EvmosSignResult());
            } else {
                try {
                    EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
                    emitter.onNext(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
            emitter.onComplete();
        }).flatMap(result -> mApi.submitEvmosTransfer(result.Data)).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<IssuanceCoinPageInfo> getIssuanceCoinPageInfo(IssuanceCoinPageInfo info, String owner) {
        String url = getEvmosRpcUrl() + "contract/token_issue_log";

        int page = null == info ? 1 : info.page + 1;

        HashMap<String, Object> dataParam = new HashMap<>();
        dataParam.put("page", page);
        dataParam.put("page_size", LIMIT);
        dataParam.put("owner", owner);
        return mService.getIssuanceCoinPageInfo(url, getJsonRequestBody(dataParam)).flatMap((Function<IssuanceCoinItemResult, ObservableSource<IssuanceCoinPageInfo>>) data -> {
            List<IssuanceCoinItem> issuanceCoinItems = data.data;
            if (null == issuanceCoinItems) {
                issuanceCoinItems = new ArrayList<>();
            }
            IssuanceCoinPageInfo value = info;
            if (null == value) {
                value = new IssuanceCoinPageInfo();
                value.result = new ArrayList<>();
            }
            value.page = page;
            value.result.addAll(issuanceCoinItems);
            if (issuanceCoinItems.size() < LIMIT) {
                value.isEnd = true;
            } else {
                value.isEnd = false;
            }

            return Observable.just(value);
        }).compose(RxSchedulers.io_main());

    }

    private RequestBody getJsonRequestBody(Map<String, Object> paramsMap) {
        return common.app.base.model.http.config.HttpMethods.getInstance().getJsonRequestBody(paramsMap);
    }


}
