package com.app.home.net.rpc;

import android.text.TextUtils;

import com.app.home.pojo.PayVoteStep1Info;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.LogUtil;

import common.app.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class WalletRpcImpl implements IWalletRpcNet {


    private RpcApi mApi;
    private VoteRpcService mService;

    public WalletRpcImpl() {
        mApi = new RpcApi();
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(VoteRpcService.class);
    }

    
    private Observable<EvmosSeqAcountBean> getEvmosSeqAccountInfo(String address) {
        return mApi.getEvmosSeqAccountInfo("", address);
    }


    


    
    @Override
    public Observable<EvmosTransferResultBean> getRPCVoteInfo(RPCVoteParam<?> param, String memo, WalletEntity entity, String pwd, IDoSdk doSdk) {
        String url = getEvmosRpcUrl() + "chat/tx/gas";
        return getEvmosSeqAccountInfo(entity.getAllAddress()).flatMap(bean -> {
            param.seq_detail = bean.data;
            return mService.getData(url, param).flatMap(info -> {
                if (!info.isOk()) {
                    return Observable.error(new RuntimeException(info.getInfo()));
                }
                RPCVoteInfo data = info.getData();
                data.mSeqAcountBean = bean;
                return sign(entity.getAllAddress(), doSdk, data.mSeqAcountBean.data, memo, data.fee.amount, data.gas_used, entity, pwd);
            });
        });
    }

    @Override
    public Observable<RPCVoteInfo> getGas(String walletAddress, RPCVoteParam<?> param) {
        String url = getEvmosRpcUrl() + "chat/tx/gas";
        return getEvmosSeqAccountInfo(walletAddress).flatMap(bean -> {
            param.seq_detail = bean.data;
            return mService.getData(url, param).flatMap(info -> {
                if (!info.isOk()) {
                    return Observable.error(new RuntimeException(info.getInfo()));
                }
                RPCVoteInfo data = info.getData();
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
                    if (null != result) {
                        if (result.isSuccess()) {
                            emitter.onNext(result);
                        } else {
                            emitter.onError(new RuntimeException(result.getInfo()));
                        }
                    } else {
                        emitter.onError(new RuntimeException(jsonSignResult + " paras error"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
            emitter.onComplete();
        }).flatMap(result -> mApi.submitEvmosTransfer(result.Data)).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PayVoteStep1Info> getPayVoteStep1Info() {
        
        String url = getNodeInfoUrl() + "cosmos/distribution/v1beta1/community_pool";
        return mService.getPayVoteStep1Info(url).compose(RxSchedulers.io_main());
    }


}
