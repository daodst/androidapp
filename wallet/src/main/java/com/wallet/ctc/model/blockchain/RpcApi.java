

package com.wallet.ctc.model.blockchain;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.RpcService;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.https.HttpMethods;
import com.wallet.ctc.model.me.SMLevelEntity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.Injection;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;



public class RpcApi {
    
    protected final RpcService mService;

    public RpcApi() {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(RpcService.class);
    }

    
    public Observable<FilBalanceBean> getEthBanlance(String address, String contract, int walletType) {
        Map<String, Object> params = new TreeMap<>();
        if (TextUtils.isEmpty(contract)) {
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_getBalance");
            params.put("id", MeApi.getETHID());
            List<String> addr = new ArrayList<>();
            addr.add(address);
            addr.add("latest");
            params.put("params", addr);
        } else {
            String walletAddress = address;
            String dataAddr = walletAddress.substring(2);
            dataAddr = "0x70a08231000000000000000000000000" + dataAddr;
            params.put("addr", walletAddress);
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_call");
            params.put("id", MeApi.getETHID());
            List<Object> addr = new ArrayList<>();
            addr.add(new EthCallBean(contract, dataAddr));
            addr.add("latest");
            params.put("params", addr);
        }
        String paramsJsonStr = new Gson().toJson(params);
        return getBalance(paramsJsonStr, walletType);
    }

    
    public Observable<FilBalanceBean> getBalance(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        if (type == WalletUtil.MCC_COIN) {
            
            url = SpUtil.getNodeSmartUrl();
        }
        return mService.getData(url, requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<BlockChainBtcBean> getBtcBalance(String address, int type) {
        String url = SpUtil.getDefNode(type);
        return mService.getBalance(url + "/api/v2/address/" + address).subscribeOn(Schedulers.io());
    }

    public Observable<FilBalanceBean> getData(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        if (type == WalletUtil.MCC_COIN) {
            
            url = SpUtil.getNodeSmartUrl();
        }
        return mService.getData(url, requestBody).subscribeOn(Schedulers.io());
    }

    
    public Observable<TransactionInfoBean> getTransactionData(String txHash, int walletType) {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_getTransactionReceipt");
        params.put("id", MeApi.getETHID());
        List<Object> data = new ArrayList<>();
        if (!txHash.startsWith("0x")) {
            txHash += "0x";
        }
        data.add(txHash);
        params.put("params", data);
        String paramsJsonStr = new Gson().toJson(params);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramsJsonStr);
        String url = SpUtil.getDefNode(walletType);
        if (walletType == WalletUtil.MCC_COIN) {
            
            url = SpUtil.getNodeSmartUrl();
        }
        return mService.getTransactionData(url, requestBody).subscribeOn(Schedulers.io());
    }

    
    public void timerCheckEthTxResult(String txHash, int walletType, CompositeDisposable compositeDisposable,
                                      Observer<EthTxResultBean> subscriber) {
        int[] useTime = {0};
        doTimerCheckEthTxResult(txHash, walletType, false, 1, useTime, compositeDisposable, subscriber);
    }

    
    private void doTimerCheckEthTxResult(String txHash, int walletType, boolean retry, int times, final int[] useTime,
                                         CompositeDisposable compositeDisposable,
                                         Observer<EthTxResultBean> subscriber) {
        if (retry) {
            useTime[0] += times;
        } else {
            useTime[0] = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
            getTransactionData(txHash, walletType)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransactionInfoBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            if (compositeDisposable != null) {
                                compositeDisposable.add(d);
                            }
                        }

                        @Override
                        public void onNext(TransactionInfoBean ethHxResultBean) {
                            if (null == ethHxResultBean || null == ethHxResultBean.getResult()) {
                                
                                doTimerCheckEthTxResult(txHash, walletType, true, 2, useTime, compositeDisposable, subscriber);
                                return;
                            }

                            if (null != ethHxResultBean.getError()) {
                                
                                subscriber.onNext(EthTxResultBean.newFailResult(txHash, ethHxResultBean.getError().getMessage()));
                                subscriber.onComplete();
                                return;
                            }

                            TransactionInfoBean.TransactionData result = ethHxResultBean.getResult();
                            if (result.isTransferSuccess()) {
                                
                                subscriber.onNext(EthTxResultBean.newSuccessResult(txHash));
                                subscriber.onComplete();
                            } else if (result.isTransferFail()) {
                                
                                subscriber.onNext(EthTxResultBean.newFailResult(txHash, ""));
                                subscriber.onComplete();
                            } else {
                                if (useTime[0] > 10) {
                                    
                                    subscriber.onNext(EthTxResultBean.newUnknownResult(txHash));
                                    subscriber.onComplete();
                                } else {
                                    doTimerCheckEthTxResult(txHash, walletType, true, 2, useTime, compositeDisposable, subscriber);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (useTime[0] > 10) {
                                
                                subscriber.onNext(EthTxResultBean.newUnknownResult(txHash));
                                subscriber.onComplete();
                            } else {
                                doTimerCheckEthTxResult(txHash, walletType, true, 2, useTime, compositeDisposable, subscriber);
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        });
        if (null != compositeDisposable) {
            compositeDisposable.add(disposable);
        }
    }


    public Observable<BasefeeBean> getObjectData(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        return mService.getObjectData(url, requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<XrpAccountInfoBean> getXrpBalance(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        return mService.getXrpBalance(url, requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<XrpAccountLinesBean> getXrpTokenBalance(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        return mService.getXrpTokenBalance(url, requestBody).subscribeOn(Schedulers.io());
    }

    public Observable<XrpSubmitBean> submitXrp(String params, int type) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        String url = SpUtil.getDefNode(type);
        return mService.submitXrp(url, requestBody).subscribeOn(Schedulers.io());
    }


    
    private String getEvmosRpcUrl() {
        return SpUtil.getDefNode(WalletUtil.MCC_COIN);
    }

    public String getNodeInfoUrl() {
        return SpUtil.getNodeInfoUrl();
    }

    
    private String getImUrl(String imUrlHost) {
        String imUrl = "";
        if (!TextUtils.isEmpty(imUrlHost)) {
            imUrl = imUrlHost;
        } else {
            imUrl = SpUtil.getImUrl();
        }
        if (!TextUtils.isEmpty(imUrl) && !imUrl.endsWith("/")) {
            imUrl = imUrl + "/";
        }
        return imUrl;
    }

    protected RequestBody getJsonRequestBody(Map<String, Object> paramsMap) {
        return common.app.base.model.http.config.HttpMethods.getInstance().getJsonRequestBody(paramsMap);
    }

    private RequestBody getJsonRequestBody(List<String> paramsMap) {
        return common.app.base.model.http.config.HttpMethods.getInstance().getJsonRequestBody(paramsMap);
    }

    
    public Observable<EvmosSeqAcountBean> getEvmosSeqAccountInfo(String address) {
        return getEvmosSeqAccountInfo("", address);
    }

    public Observable<EvmosSeqAcountBean> getEvmosSeqAccountInfo(String rpcHost, String address) {
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/accountNumberSeq";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account_addr", address);
        return mService.getEvmosSeqAccountInfo(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosBalanceBean> getEvmosAllBalance(String address) {
        String url = getEvmosRpcUrl() + "chat/balances/all";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        return mService.getEvmosAllBalance(url, paramsMap).subscribeOn(Schedulers.io());
    }


    public Observable<List<SMLevelEntity>> getPledgeRank() {
        String url = getEvmosRpcUrl() + "pledge/rank";
        return mService.getPledgeRank(url, new HashMap<>()).subscribeOn(Schedulers.io()).flatMap(new Function<ResponseBodyT<List<SMLevelEntity>>, ObservableSource<List<SMLevelEntity>>>() {
            @Override
            public ObservableSource<List<SMLevelEntity>> apply(ResponseBodyT<List<SMLevelEntity>> listResponseBodyT) throws Exception {
                return Observable.just(listResponseBodyT.data);
            }
        });
    }

    
    public Observable<EvmosOneBalanceBean> getEvmosOneBalance(String address, String coinName) {
        boolean fangZuobi = true;
        if (fangZuobi) {
            return getEvmosOneBalanceBySdk(address, coinName);
        } else {
            return getEvmosOneBalanceByApi(address, coinName);
        }
    }

    
    private Observable<EvmosOneBalanceBean> getEvmosOneBalanceByApi(String address, String coinName) {
        String url = getEvmosRpcUrl() + "chat/balances";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        paramsMap.put("token", coinName);
        return mService.getEvmosOneBalance(url, paramsMap).subscribeOn(Schedulers.io());
    }


    
    private Observable<EvmosOneBalanceBean> getEvmosOneBalanceBySdk(String address, String coinName) {
        return Observable.create(new ObservableOnSubscribe<EvmosOneBalanceBean>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosOneBalanceBean> emitter) throws Exception {
                EvmosOneBalanceBean bean = ChatSdk.getOneBalance(address, coinName);
                emitter.onNext(bean);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    

    
    public Observable<EvmosTransRecordsBean> getEvmosHistory(int page, String address, String coinName) {
        String url = getEvmosRpcUrl() + "chat/transfer/list";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        paramsMap.put("page", page);
        paramsMap.put("token", coinName);
        return mService.getEvmosHistory(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosTransTypesBean> getEvmosTransfTypes(String language) {
        String url = getEvmosRpcUrl() + "wallet/transfer_type";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("lang", language);
        return mService.getEvmosTransfTypes(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosTokenRecordsBean> getEvmosTokenHistory(int page, String address, String contract) {
        String url = getEvmosRpcUrl() + "chat/ethTransfer/list";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        paramsMap.put("page", page);
        paramsMap.put("contract", contract);
        return mService.getEvmosTokenHistory(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }


    
    public void timerCheckTxResult(EvmosTransferResultBean transferResult, CompositeDisposable compositeDisposable,
                                   Observer<EvmosPledgeResultBean> subscriber) {
        int[] useTime = {0};
        doTimerCheckTxResult(transferResult, false, 1, useTime, compositeDisposable, subscriber);
    }

    
    private void doTimerCheckTxResult(EvmosTransferResultBean transferResult, boolean retry, int times, final int[] useTime,
                                      CompositeDisposable compositeDisposable,
                                      Observer<EvmosPledgeResultBean> subscriber) {
        if (retry) {
            useTime[0] += times;
        } else {
            useTime[0] = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
            getEvmosHxResult(transferResult.data.tx_hash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EvmosHxResultBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            if (compositeDisposable != null) {
                                compositeDisposable.add(d);
                            }
                        }

                        @Override
                        public void onNext(EvmosHxResultBean evmosHxResultBean) {
                            if (evmosHxResultBean.isTxSuccess()) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = true;
                                subscriber.onNext(result);
                                subscriber.onComplete();
                            } else if (evmosHxResultBean.isTxFail(useTime[0])) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = AppApplication.getContext().getString(R.string.transfer_fail);
                                subscriber.onNext(result);
                                subscriber.onComplete();
                            } else {
                                doTimerCheckTxResult(transferResult, true, 2, useTime, compositeDisposable, subscriber);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (useTime[0] > 7) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = AppApplication.getContext().getString(R.string.wait_confirm_hash_result);
                                subscriber.onNext(result);
                                subscriber.onComplete();
                            } else {
                                doTimerCheckTxResult(transferResult, true, 2, useTime, compositeDisposable, subscriber);
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        });
        if (null != compositeDisposable) {
            compositeDisposable.add(disposable);
        }
    }

    
    public Observable<EvmosHxResultBean> getEvmosHxResult(String txHash) {
        return getEvmosHxResult("", txHash);
    }

    
    public Observable<EvmosHxResultBean> getEvmosHxResult(String rpcHost, String txHash) {
        boolean fangzhuoBi = true;
        if (fangzhuoBi) {
            return getEvmosHxResultBySdk(rpcHost, txHash);
        } else {
            return getEvmosHxResultByApi(rpcHost, txHash);
        }
    }

    
    public Observable<EvmosHxResultBean> getEvmosHxResultByApi(String rpcHost, String txHash) {
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/tx/hash";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tx_hash", txHash);

        return mService.getEvmosHxResult(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosHxResultBean> getEvmosHxResultBySdk(String rpcHost, String txHash) {
        return Observable.create(new ObservableOnSubscribe<EvmosHxResultBean>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosHxResultBean> emitter) throws Exception {
                EvmosHxResultBean bean = ChatSdk.queryTxResultByHash(rpcHost, txHash);
                emitter.onNext(bean);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    
    public Observable<EvmosTransferResultBean> submitEvmosTransfer(String signStr) {
        return submitEvmosTransfer("", signStr);
    }

    public Observable<EvmosTransferResultBean> submitEvmosTransfer(String rpcHost, String signStr) {
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/tx/broadcast";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("tx_string", signStr);
        return mService.submitEvmosTransfer(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGasBean> getEvmosGas(String msgType, String msgs, EvmosSeqAcountBean.Data seqAccountInfo) {
        
        return getEvmosGas("", msgType, msgs, seqAccountInfo);
    }

    
    public Observable<EvmosGasBean> getEvmosMultiGas(String msgs, EvmosSeqAcountBean.Data seqAccountInfo) {
        
        return getEvmosGas("", msgs, seqAccountInfo);
    }


    
    public Observable<EvmosGasBean> getEvmosGas(String rpcHost, String msgType, String msgs, EvmosSeqAcountBean.Data seqAccountInfo) {
        
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/tx/gas";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("msg_type", msgType);
        paramsMap.put("msgs", msgs);
        paramsMap.put("seq_detail", seqAccountInfo);
        return mService.getEvmosGas(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGasBean> getEvmosMultiGas(String rpcHost, List<Map<String, Object>> msgs, EvmosSeqAcountBean.Data seqAccountInfo) {
        
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/tx/gas_multi";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("msgs", msgs);
        paramsMap.put("seq_detail", seqAccountInfo);
        return mService.getEvmosGas(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }


    
    public Observable<EvmosSeqGasBean> getEvmosRegistGas(String fromAddr, String nodeAddr, final String coinName, String phoneStart, String amountBigNum) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(evmosSeqAcountBean -> {
            if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                throw new Exception(errorInfo);
            }
            String msgType = EvmosMsgTypes.MSG_REGISTE;
            String tokenName = coinName;
            
            
            
            JSONObject msgsJsonObject = new JSONObject();
            try {
                msgsJsonObject.put("from_address", fromAddr);
                msgsJsonObject.put("node_address", nodeAddr);
                JSONObject amountItem = new JSONObject();
                amountItem.put("denom", tokenName);
                amountItem.put("amount", amountBigNum);
                msgsJsonObject.put("mortgage_amount", amountItem);

                msgsJsonObject.put("mobile_prefix", phoneStart);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(evmosGasBean -> {
                EvmosSeqGasBean data = new EvmosSeqGasBean();
                data.status = evmosGasBean.status;
                data.info = evmosGasBean.info;
                data.gas = evmosGasBean;
                data.seqAccount = evmosSeqAcountBean.data;
                return data;
            });
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosUnPledgeGas(String fromAddr, String nodeAddr, final String coinName,
                                                           String amountBigNum) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                String msgType = EvmosMsgTypes.MSG_UNPLEDGE;
                String tokenName = coinName;
                JSONObject msgsJsonObject = new JSONObject();
                try {
                    msgsJsonObject.put("delegator_address", fromAddr);
                    msgsJsonObject.put("validator_address", nodeAddr);
                    JSONObject amountItem = new JSONObject();
                    amountItem.put("denom", tokenName);
                    amountItem.put("amount", amountBigNum);
                    msgsJsonObject.put("amount", amountItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosWithdrawGas(String fromAddr, String nodeAddr) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(evmosSeqAcountBean -> {
            if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                throw new Exception(errorInfo);
            }
            String msgType = EvmosMsgTypes.MSG_REWARD;
            JSONObject msgsJsonObject = new JSONObject();
            try {
                msgsJsonObject.put("delegator_address", fromAddr);
                msgsJsonObject.put("validator_address", nodeAddr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                @Override
                public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                    EvmosSeqGasBean data = new EvmosSeqGasBean();
                    data.status = evmosGasBean.status;
                    data.info = evmosGasBean.info;
                    data.gas = evmosGasBean;
                    data.seqAccount = evmosSeqAcountBean.data;
                    return data;
                }
            });
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosTransferMobileGas(String fromAddr, String toAddr, String mobile) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                String msgType = EvmosMsgTypes.MSG_MOBILETRANSFER;
                JSONObject msgsJsonObject = new JSONObject();
                try {
                    msgsJsonObject.put("from_address", fromAddr);
                    msgsJsonObject.put("to_address", toAddr);
                    msgsJsonObject.put("mobile", mobile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getSetPrivacySettingGas(String fromAddr, String feeMode, String bigAmount, EvmosChatFeeBean chatFeeBean) {
        return getEvmosSetChatFeeGas("", fromAddr, chatFeeBean.data.node_address, feeMode,
                bigAmount, chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist,
                chatFeeBean.data.chat_whitelist, chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list, chatFeeBean.data.chat_white_enc_list);
    }

    
    public Observable<EvmosSeqGasBean> getChangeNodeGas(String rpcPhost, String fromAddr, String nodeAddress, EvmosChatFeeBean chatFeeBean) {
        return getEvmosSetChatFeeGas(rpcPhost, fromAddr, nodeAddress, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist,
                chatFeeBean.data.chat_whitelist, chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list, chatFeeBean.data.chat_white_enc_list);
    }


    
    public Observable<EvmosSeqGasBean> getSaveFBWInfoGas(String fromAddr, String encryBlackList, String encryWhiteList, String encryAddressBook,
                                                         String encryBlackListGateWay, String encryWhiteListGateway, EvmosChatFeeBean chatFeeBean) {
        return getEvmosSetChatFeeGas("", fromAddr, chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), encryBlackList,
                encryWhiteList, encryAddressBook, encryBlackListGateWay, encryWhiteListGateway);
    }


    
    public Observable<EvmosSeqGasBean> getSaveBlackListGas(String fromAddr, String encryBlackList,
                                                           String encryBlackListGateWay, EvmosChatFeeBean chatFeeBean) {
        return getEvmosSetChatFeeGas("", fromAddr, chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), encryBlackList,
                chatFeeBean.data.chat_whitelist, chatFeeBean.data.address_book, encryBlackListGateWay, chatFeeBean.data.chat_white_enc_list);
    }

    
    public Observable<EvmosSeqGasBean> getSaveWhiteListGas(String fromAddr, String encryWhiteList,
                                                           String encryWhiteListGateWay, EvmosChatFeeBean chatFeeBean) {
        return getEvmosSetChatFeeGas("", fromAddr, chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist,
                encryWhiteList, chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list,
                encryWhiteListGateWay);
    }

    private String getS(String str) {
        if (null != str) {
            return str;
        }
        return "";
    }

    
    public Observable<EvmosSeqGasBean> getEvmosSetChatFeeGas(String rpcPhost, String fromAddr,
                                                             String nodeAddress, String chatRestrictedMode, String chatFeeAmount,
                                                             String chatFeeCoinSymbol, String chatBlackList, String chatWhiteList,
                                                             String addressBookList, String chatBlackListEnc, String chatWhiteListEnc) {
        return getEvmosSeqAccountInfo(rpcPhost, fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                String msgType = EvmosMsgTypes.MSG_SETCHATFEE;
                String tokenName = chatFeeCoinSymbol;
                JSONObject msgsJsonObject = new JSONObject();
                try {
                    msgsJsonObject.put("from_address", fromAddr);
                    msgsJsonObject.put("node_address", nodeAddress);

                    msgsJsonObject.put("chat_restricted_mode", chatRestrictedMode);
                    msgsJsonObject.put("update_time", System.currentTimeMillis() / 1000);

                    msgsJsonObject.put("chat_fee_amount", chatFeeAmount);
                    msgsJsonObject.put("chat_fee_coin_symbol", tokenName);

                    msgsJsonObject.put("chat_blacklist", getS(chatBlackList));
                    msgsJsonObject.put("chat_whitelist", getS(chatWhiteList));
                    msgsJsonObject.put("address_book", getS(addressBookList));
                    msgsJsonObject.put("chat_blacklist_enc", getS(chatBlackListEnc));
                    msgsJsonObject.put("chat_whitelist_enc", getS(chatWhiteListEnc));
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw e;
                }
                return getEvmosGas(rpcPhost, msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    public JSONArray getJsonArray(List<String> list) {
        JSONArray array = new JSONArray();
        if (null != list && list.size() > 0) {
            for (String ob : list) {
                array.put(ob);
            }
        }
        return array;
    }

    public Observable<EvmosSeqGasBean> getEvmosBurnGas(String fromAddr, String to_address, String nodeAddr, final String coinName,
                                                       String amountBigNum) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(evmosSeqAcountBean -> {
            if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                throw new Exception(errorInfo);
            }
            String msgType = EvmosMsgTypes.MSG_BURN;
            String tokenName = coinName;
            JSONObject msgsJsonObject = new JSONObject();
            try {
                msgsJsonObject.put("from_address", fromAddr);
                msgsJsonObject.put("to_address", to_address);
                msgsJsonObject.put("gateway", nodeAddr);
                JSONObject amountItem = new JSONObject();
                amountItem.put("denom", tokenName);
                amountItem.put("amount", amountBigNum);
                msgsJsonObject.put("burn_coin", amountItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                @Override
                public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                    EvmosSeqGasBean data = new EvmosSeqGasBean();
                    data.status = evmosGasBean.status;
                    data.info = evmosGasBean.info;
                    data.gas = evmosGasBean;
                    data.seqAccount = evmosSeqAcountBean.data;
                    return data;
                }
            });
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosPledgeGas(String fromAddr, String delegatorAddr, String nodeAddr, final String coinName,
                                                         String amountBigNum) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(evmosSeqAcountBean -> {
            if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                throw new Exception(errorInfo);
            }
            String msgType = EvmosMsgTypes.MSG_PLEDGE;
            String tokenName = coinName;
            JSONObject msgsJsonObject = new JSONObject();
            try {
                msgsJsonObject.put("from_address", fromAddr);
                msgsJsonObject.put("delegator_address", delegatorAddr);
                msgsJsonObject.put("validator_address", nodeAddr);
                JSONObject amountItem = new JSONObject();
                amountItem.put("denom", tokenName);
                amountItem.put("amount", amountBigNum);
                msgsJsonObject.put("amount", amountItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                @Override
                public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                    EvmosSeqGasBean data = new EvmosSeqGasBean();
                    data.status = evmosGasBean.status;
                    data.info = evmosGasBean.info;
                    data.gas = evmosGasBean;
                    data.seqAccount = evmosSeqAcountBean.data;
                    return data;
                }
            });
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosChatPayGas(String fromAddr, String toAddress, final String coinName,
                                                          String amountBigNum) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                String msgType = EvmosMsgTypes.MSG_CHATSENDGIFT;
                String tokenName = coinName;
                JSONObject msgsJsonObject = new JSONObject();
                try {
                    msgsJsonObject.put("from_address", fromAddr);
                    msgsJsonObject.put("to_address", toAddress);
                    JSONObject amountItem = new JSONObject();
                    amountItem.put("denom", tokenName);
                    amountItem.put("amount", amountBigNum);
                    msgsJsonObject.put("gift_value", amountItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosBurnPhoneGas(String fromAddr, String phoneStart, String gateWayAddr, String chatAddr) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                String msgType = EvmosMsgTypes.MSG_BURN_GET_MOBILE;
                JSONObject msgsJsonObject = new JSONObject();
                try {
                    msgsJsonObject.put("from_address", fromAddr);
                    msgsJsonObject.put("mobile_prefix", phoneStart);
                    msgsJsonObject.put("gateway_address", gateWayAddr);
                    msgsJsonObject.put("chat_address", chatAddr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getEvmosGas(msgType, msgsJsonObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosSeqGasBean> getEvmosChainEthsGas(String fromAddr, String toAddress, String bigCoinAmount,
                                                            String coinSymbol, String chainType, String remark) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("send_address", fromAddr);
            msgsJsonObject.put("to_address", toAddress);
            msgsJsonObject.put("coin_amount", bigCoinAmount);
            msgsJsonObject.put("coin_symbol", coinSymbol);
            msgsJsonObject.put("chain_type", chainType);
            msgsJsonObject.put("remark", remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_CROSSCHAINOUT, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getEvmosParamsVoteGas(String fromAddr, String title, String description,
                                                             String bigMoneyCoin, String changeJsonStr) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("proposer", fromAddr);
            msgsJsonObject.put("title", title);
            msgsJsonObject.put("description", description);
            msgsJsonObject.put("deposit", bigMoneyCoin);
            msgsJsonObject.put("change", changeJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_VOTE_PARAMS, msgsJsonObject);
    }

    
    public Observable<EvmosSeqGasBean> getEvmosPayVoteGas(String fromAddr, String title, String description,
                                                          String bigMoneyCoin, String payReceiveAddr, String bigPayNumCoin) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("proposer", fromAddr);
            msgsJsonObject.put("title", title);
            msgsJsonObject.put("description", description);
            msgsJsonObject.put("deposit", bigMoneyCoin);
            msgsJsonObject.put("recipient", payReceiveAddr);
            msgsJsonObject.put("amount", bigPayNumCoin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_VOTE_PAY, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getEvmosUpgradeVoteGas(String fromAddr, String title, String description,
                                                              String bigMoneyCoin, String upgradeName, String upgradeInfoJson, long upgradeHeight) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("proposer", fromAddr);
            msgsJsonObject.put("title", title);
            msgsJsonObject.put("description", description);
            msgsJsonObject.put("deposit", bigMoneyCoin);
            msgsJsonObject.put("name", upgradeName);
            msgsJsonObject.put("info", upgradeInfoJson);
            msgsJsonObject.put("height", upgradeHeight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_VOTE_UPGRADE, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getCreateDeviceGroupGas(String fromAddr, String gatewayAddr, String groupId,
                                                               String deviceRatio, String salaryRatio, String bigburnAmount, String chatAddress, String clusterName,
                                                               String freezeAmount, List<DeviceGroupMember> members) {
        
        Map<String, Object> createGroupGasMsg = new HashMap<>();
        try {
            JSONObject msgsJsonObject = new JSONObject();
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("gate_address", gatewayAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("device_ratio", deviceRatio);
            msgsJsonObject.put("salary_ratio", salaryRatio);
            msgsJsonObject.put("burn_amount", bigburnAmount);
            msgsJsonObject.put("chat_address", chatAddress);
            msgsJsonObject.put("cluster_name", clusterName);
            msgsJsonObject.put("freeze_amount", freezeAmount);


            createGroupGasMsg.put("msg_type", EvmosMsgTypes.MSG_CREATE_DEVICE_GROUP);
            createGroupGasMsg.put("msg", msgsJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, Object> addMembersGasMsg = new HashMap<>();
        try {
            JSONObject msgsJsonObject = new JSONObject();
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            JSONArray array = new JSONArray();
            if (null != members && members.size() > 0) {
                for (DeviceGroupMember member : members) {
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put("member_address", member.memberAddress);
                    jsonItem.put("index_num", member.indexNum);
                    jsonItem.put("chat_address", member.chatAddress);
                    array.put(jsonItem);
                }
            }
            msgsJsonObject.put("members", array);
            addMembersGasMsg.put("msg_type", EvmosMsgTypes.MSG_ADD_DEVICE_MEMBERS);
            addMembersGasMsg.put("msg", msgsJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(createGroupGasMsg);
        list.add(addMembersGasMsg);

        return getEvmosMultiSeqGas(fromAddr, list);
    }


    
    public Observable<EvmosSeqGasBean> getEvmosSeqGas(String fromAddr, String msgType, JSONObject params) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                JSONObject msgObject = params;
                if (msgObject == null) {
                    msgObject = new JSONObject();
                }
                return getEvmosGas(msgType, msgObject.toString(), evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        });
    }


    
    public Observable<EvmosSeqGasBean> getEvmosMultiSeqGas(String fromAddr, List<Map<String, Object>> params) {
        return getEvmosSeqAccountInfo(fromAddr).concatMap(new Function<EvmosSeqAcountBean, ObservableSource<? extends EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<? extends EvmosSeqGasBean> apply(EvmosSeqAcountBean evmosSeqAcountBean) throws Exception {
                if (null == evmosSeqAcountBean || !evmosSeqAcountBean.isSuccess()) {
                    String errorInfo = evmosSeqAcountBean == null ? "get seqAccount fail!" : evmosSeqAcountBean.getInfo();
                    throw new Exception(errorInfo);
                }
                List<Map<String, Object>> msgObject = params;
                if (msgObject == null) {
                    msgObject = new ArrayList<>();
                }
                return getEvmosMultiGas("", msgObject, evmosSeqAcountBean.data).map(new Function<EvmosGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosGasBean evmosGasBean) throws Exception {
                        EvmosSeqGasBean data = new EvmosSeqGasBean();
                        data.status = evmosGasBean.status;
                        data.info = evmosGasBean.info;
                        data.gas = evmosGasBean;
                        data.seqAccount = evmosSeqAcountBean.data;
                        return data;
                    }
                });
            }
        });
    }


    
    public Observable<EvmosGasBean> getEvmosTransferGas(String fromAddr, String toAddress, String amount, String coinName, EvmosSeqAcountBean.Data seqAccountInfo) {
        
        if (TextUtils.isEmpty(toAddress)) {
            
            toAddress = "dst1nhta6gsf9x9l2pd2m5qg6lya0h3m0pjy4vtd35";
        }
        if (TextUtils.isEmpty(amount) || amount.equals("0")) {
            amount = "100000000000000";
        }

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("msg_type", EvmosMsgTypes.MSG_SEND);
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("to_address", toAddress);
            JSONArray amountArray = new JSONArray();
            JSONObject amountItem = new JSONObject();
            amountItem.put("denom", coinName);
            amountItem.put("amount", amount);
            amountArray.put(amountItem);
            msgsJsonObject.put("amount", amountArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        paramsMap.put("msgs", msgsJsonObject.toString());
        paramsMap.put("seq_detail", seqAccountInfo);

        return getEvmosGas(EvmosMsgTypes.MSG_SEND, msgsJsonObject.toString(), seqAccountInfo);
    }

    
    public Observable<EvmosTotalPledgeBean> getEvmosTotalPledge(String address) {
        String url = getEvmosRpcUrl() + "pledge/all";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        return mService.getEvmosTotalPledge(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosChatInfoBean> getEvmosChatInfo(String address) {
        String url = getEvmosRpcUrl() + "chat/userinfo";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        return mService.getEvmosChatInfo(url, paramsMap).subscribeOn(Schedulers.io());
    }

    public Observable<NodeData> getNodeData(String url) {
        return mService.getNodeData(url).compose(RxSchedulers.io_main()).flatMap(wapper -> Observable.just(wapper.data));
    }

    public Observable<DaodstTipsBean> getDaodstTips(String key, String lang) {
        String url = getEvmosRpcUrl() + "daodst/tips";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("key", key);
        paramsMap.put("lang", lang);
        return mService.getDaodstTips(url, paramsMap).subscribeOn(Schedulers.io());
    }

    public Observable<EvmosChatUnPledgeAvailable> getEvmosChatUnPledgeAvailable(String address) {
        String url = getEvmosRpcUrl() + "pledge/can_undelegate";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("address", address);
        return mService.getEvmosChatUnPledgeAvailable(url, paramsMap).subscribeOn(Schedulers.io());
    }

    private Gson mGson = new Gson();

    public Observable<SmOuterChartInfo> getChartInfo(String gather_type, String page_size) {
        String url = getEvmosRpcUrl() + "fm/statistics_info";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("gather_type", gather_type);
        paramsMap.put("page_size", page_size);
        return mService.getChartInfo(url, paramsMap).map(responseBody -> mGson.fromJson(responseBody.getData(), SmOuterChartInfo.class)).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosPledgeParamsBean> getEvmosPledgeParams() {
        String url = getEvmosRpcUrl() + "chat/pledgeParams";
        Map<String, Object> paramsMap = new HashMap<>();
        return mService.getEvmosPledgeParams(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosChatParamsBean> getEvmosChatParams() {
        String url = getNodeInfoUrl() + "chat/params";
        Map<String, Object> paramsMap = new HashMap<>();
        return mService.getEvmosChatParams(url, paramsMap).subscribeOn(Schedulers.io());
    }

    public Observable<EvmosChatToBurn> getToBurn(String address) {
        String url = getEvmosRpcUrl() + "pledge/to_burn";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("address", address);
        return mService.getEvmosChatToBurn(url, paramsMap).subscribeOn(Schedulers.io());
    }

    public Observable<EvmosChatBurnRatio> getBurnRatio() {
        String url = getEvmosRpcUrl() + "burn/burn_info";
        Map<String, Object> paramsMap = new HashMap<>();
        return mService.getBurnRatio(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGatewayBean> getEvmosGateway(String phoneNumStar) {
        String url = getEvmosRpcUrl() + "gateway/info";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("number", phoneNumStar);
        return mService.getEvmosGateway(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io()).doOnError(throwable -> {
            throwable.printStackTrace();
        }).doOnNext(bean -> {
            if (bean.isSuccess() && null == bean.data) {
                
                Application mApplication = Injection.mApplication;
                Intent intent = ActivityRouter.getIntent(mApplication, ActivityRouter.App.A_NodeUnableDialogActivity);
                intent.putExtra("isgateway", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.startActivity(intent);
            }
        });
    }

    
    public Observable<EvmosBlockInfoBean> getBlockHeight() {
        
        String url = getNodeInfoUrl() + "cosmos/base/tendermint/v1beta1/blocks/latest";
        return mService.getBlockHeight(url).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosVoteDuringBean> getVoteDuring() {
        String url = getNodeInfoUrl() + "cosmos/gov/v1beta1/params/voting";
        return mService.getVoteDuring(url).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGatewayNumberCountBean> getGatewayNumberCount(String gatewayAddress, String amount) {
        
        return Observable.create(new ObservableOnSubscribe<EvmosGatewayNumberCountBean>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosGatewayNumberCountBean> emitter) throws Exception {
                EvmosGatewayNumberCountBean data = ChatSdk.httpGetGatewayNumberCount(gatewayAddress, amount);
                emitter.onNext(data);
                emitter.onComplete();
            }
        });
    }

    
    public Observable<EvmosBlockHeightOrRateBean> getBlockHeightOrRate(String validatorAddress, String walletAddress) {
        String url = getEvmosRpcUrl() + "gateway/undelegate/rate";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("delegate_address", walletAddress);
        paramsMap.put("validator_address", validatorAddress);
        return mService.getBlockHeightOrRate(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<RedeemGatewayInfoEntity> getGatewayAddressInfo(String gatewayAddress, String account) {
        String url = getEvmosRpcUrl() + "gateway/address/info";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("gateway_address", gatewayAddress);
        paramsMap.put("account", account);
        return mService.getGatewayAddressInfo(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }

    
    public Observable<BlockDepositBean> getBlockDeposit() {
        
        String url = getNodeInfoUrl() + "cosmos/gov/v1beta1/params/deposit";
        return mService.getBlockDeposit(url).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGatewayListBean> getEvmosGatewayList(int page, int sortByAmount, int sortByTime) {
        String url = getEvmosRpcUrl() + "gateway/list";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("page", page);
        paramsMap.put("sortByAmount", sortByAmount + "");
        paramsMap.put("sortByTime", sortByTime + "");
        return mService.getEvmosGatewayList(url, getJsonRequestBody(paramsMap)).subscribeOn(Schedulers.io());
    }


    
    public Observable<EvmosChatFeeBean> getEvmosChatFeeSetting(String address) {
        return getEvmosChatFeeSetting("", address);
    }

    
    public Observable<EvmosBWUsersBean> searchBlackWhitesUserDatas(List<String> blacks, List<String> whites) {
        return Observable.zip(
                searchUserDatas(blacks),
                searchUserDatas(whites),
                (blackList, whiteList) -> {
                    EvmosBWUsersBean data = new EvmosBWUsersBean();
                    data.blackList = blackList;
                    data.whiteList = whiteList;
                    return data;
                }
        ).subscribeOn(Schedulers.io());
    }

    
    public Observable<List<EvmosUserBean>> searchUserDatas(List<String> addressList) {
        if (null == addressList || addressList.size() == 0) {
            return Observable.just(new ArrayList<>());
        }
        String url = getEvmosRpcUrl() + "chat/chatinfos";
        
        List<String> addrs = new ArrayList<>();
        for (String userId : addressList) {
            addrs.add(AllUtils.getAddressByUid(userId));
        }
        return mService.searchUserDatas(url, getJsonRequestBody(addrs)).concatMap(new Function<EvmosUsersBean, ObservableSource<List<EvmosUserBean>>>() {
            @Override
            public ObservableSource<List<EvmosUserBean>> apply(EvmosUsersBean evmosUsers) throws Exception {
                List<EvmosUserBean> evmosUserBeans = new ArrayList<>();
                if (evmosUsers != null && evmosUsers.isSuccess() && evmosUsers.data != null && evmosUsers.data.size() > 0) {
                    evmosUserBeans = evmosUsers.data;
                    
                    for (int i = 0; i < evmosUserBeans.size(); i++) {
                        EvmosUserBean bean = evmosUserBeans.get(i);
                        if (null != bean && !TextUtils.isEmpty(bean.address) && TextUtils.isEmpty(bean.comm_address)) {
                            for (String srcAddr : addressList) {
                                if (srcAddr.equalsIgnoreCase(bean.address) && srcAddr.contains(":")) {
                                    
                                    evmosUserBeans.get(i).comm_address = srcAddr;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (evmosUsers == null || !evmosUsers.isSuccess()) {
                        String errorInfo = evmosUsers != null ? evmosUsers.getInfo() : "get data is null";
                        throw new Exception(errorInfo);
                    }
                }
                return Observable.just(evmosUserBeans).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<EvmosChatFeeBean> getEvmosChatFeeSetting(String rpcHost, String address) {
        if (TextUtils.isEmpty(rpcHost)) {
            rpcHost = getEvmosRpcUrl();
        }
        String url = rpcHost + "chat/userinfo";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("account", address);
        return mService.getEvmosChatFeeSetting(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<String> getImGateWayPublickey(String imRpcHost) {
        String imHost = getImUrl(imRpcHost);
        String url = imHost + "_matrix/client/r0/server/pubKey";
        Map<String, Object> paramsMap = new HashMap<>();
        return mService.getImGateWayPublickey(url, paramsMap).subscribeOn(Schedulers.io());
    }


    
    public Observable<DefaultAssetsBean> getDefaultAssets() {
        String url = getEvmosRpcUrl() + "contract/main_token_info";
        Map<String, Object> paramsMap = new HashMap<>();
        return mService.getDefaultAssets(url, paramsMap).subscribeOn(Schedulers.io());
    }

    
    public Observable<EvmosGetIncomeHistoryBean> getEvmosGetIncomeHistory(String account, String groupId) {
        String url = getEvmosRpcUrl() + "dao/last_device_reward";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("from", account);
        paramsMap.put("cluster_id", groupId);
        return mService.getEvmosGetIncomeHistory(url, paramsMap);
    }

    
    public Observable<JsonObject> getParamsVoteKeys(String subSpace) {
        String url = "";
        if (subSpace.equals("chat") || subSpace.equals("gateway") || subSpace.equals("dao")) {
            url = getNodeInfoUrl() + subSpace + "/params";
        } else {
            if ("minting".equals(subSpace)) {
                subSpace = "mint";
            }
            url = getNodeInfoUrl() + "cosmos/" + subSpace + "/v1beta1/params";
        }
        return mService.getParamsVoteKeys(url).subscribeOn(Schedulers.io());
    }











    
    public Observable<CoinPriceBean> getPancakeSwapUsdtPrice(AssertBean assertBean) {
        return getPancakeSwapUsdtPrice(false, assertBean);
    }

    
    public Observable<CoinPriceBean> getPancakeSwapAmountsOut(String amount, AssertBean assertBean, AssertBean toAssert, AssertBean... centerAsserts) {
        return getPancakeSwapUsdtPrice(amount, assertBean, toAssert, true, centerAsserts);
    }

    
    public Observable<CoinPriceBean> getPancakeSwapAmountsIn(String amount, AssertBean assertBean, AssertBean toAssert, AssertBean... centerAssets) {
        return getPancakeSwapUsdtPrice(amount, assertBean, toAssert, false, centerAssets);
    }

    
    public Observable<CoinPriceBean> getPancakeSwapUsdtPrice(String amount, AssertBean assertBean, final AssertBean toAssert, boolean isOut, AssertBean... centerAssets) {
        return Observable.create(new ObservableOnSubscribe<CoinPriceBean>() {
            @Override
            public void subscribe(ObservableEmitter<CoinPriceBean> emitter) throws Exception {

                if (null == assertBean) {
                    throw new Exception("assert is null");
                }
                AssertBean mapToAssert = toAssert;


                int walletType = assertBean.getType();
                
                String myWallet = "0x8aB9f0f74B39211c3D26101367010896E905baaC";

                String swapContract = "";
                String toContract = "";
                int toDecimal = 18; 
                if (walletType == BNB_COIN) {
                    
                    swapContract = Constants.PANCAKESWAP_BSC_CONTRACT;
                    if (mapToAssert == null) {
                        
                        mapToAssert = Constants.getUSDTBscAssets();
                    }
                    toContract = mapToAssert.getContract();
                    toDecimal = mapToAssert.getDecimal();
                } else if (walletType == ETH_COIN) {
                    
                    String uniSwapContract = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
                    swapContract = uniSwapContract;
                    toContract = "0xdac17f958d2ee523a2206206994597c13d831ec7";
                    toDecimal = 6;
                } else {
                    throw new Exception("un support this chain price get");
                }

                
                String rpcUrl = SpUtil.getDefNode(walletType);
                Web3j web3j = Web3j.build(new HttpService(rpcUrl));

                
                List<String> tokens = new ArrayList<>();
                
                
                String coinContract = getContract(assertBean);
                if (toContract.equalsIgnoreCase(coinContract)) {
                    
                    CoinPriceBean coinPriceBean = new CoinPriceBean();
                    coinPriceBean.assertBean = assertBean;
                    coinPriceBean.price = "1";
                    coinPriceBean.toAmount = amount;
                    emitter.onNext(coinPriceBean);
                    emitter.onComplete();
                    return;
                }
                String FUNC_GETAMOUNTSOUT = "getAmountsOut";
                int resultIndex = 0;
                if (isOut) {
                    FUNC_GETAMOUNTSOUT = "getAmountsOut";
                    tokens.add(coinContract); 
                    
                    if (null != centerAssets && centerAssets.length > 0) {
                        for (AssertBean center : centerAssets) {
                            if (null != center) {
                                String centerContract = center.getContract();
                                if (!TextUtils.isEmpty(centerContract)) {
                                    tokens.add(centerContract);
                                }
                            }
                        }
                    }
                    tokens.add(toContract); 
                    resultIndex = tokens.size() - 1;
                } else {
                    
                    FUNC_GETAMOUNTSOUT = "getAmountsIn";
                    tokens.add(toContract); 
                    resultIndex = 0;
                    
                    if (null != centerAssets && centerAssets.length > 0) {
                        for (AssertBean center : centerAssets) {
                            if (null != center) {
                                String centerContract = center.getContract();
                                if (!TextUtils.isEmpty(centerContract)) {
                                    tokens.add(centerContract);
                                }
                            }
                        }
                    }
                    tokens.add(coinContract); 
                }
                
                int fsvDecimal = assertBean.getDecimal(); 
                int outputSize = tokens.size() + 2;
                resultIndex = resultIndex + 2;
                List<TypeReference<?>> outputParameters = new ArrayList<>(outputSize);
                for (int i = 0; i < outputSize; i++) {
                    outputParameters.add(new TypeReference<Uint256>() {
                    });
                }

                
                
                
                
                BigInteger amountIn = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, fsvDecimal))).toBigInteger();
                final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                        FUNC_GETAMOUNTSOUT,
                        Arrays.asList(new Uint256(amountIn),
                                new DynamicArray<Address>(
                                        Address.class,
                                        Utils.typeMap(tokens, Address.class))),
                        outputParameters);
                
                String data = FunctionEncoder.encode(function);
                
                EthCall response = web3j.ethCall(
                        Transaction.createEthCallTransaction(myWallet, swapContract, data),
                        DefaultBlockParameterName.LATEST).sendAsync().get();

                
                List<Type> someTypes = FunctionReturnDecoder.decode(
                        response.getValue(), function.getOutputParameters());
                String price = "";
                String toAmount = "";

                if (null != someTypes && someTypes.size() == outputSize) {
                    Uint256 value = (Uint256) someTypes.get(resultIndex);
                    BigDecimal toAmountD = new BigDecimal(value.getValue()).divide(new BigDecimal(Math.pow(10, toDecimal)), 8, RoundingMode.HALF_UP).stripTrailingZeros();
                    toAmount = toAmountD.toPlainString();
                    
                    price = toAmountD.divide(new BigDecimal(amount), 8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                }
                LogUtil.d("PancakeSwap:price: " + price + ",toAmount:" + toAmount + " ,getShort_name: " + assertBean.getShort_name() + "  , getContract: " + assertBean.getContract() + " ,getDecimal: " + assertBean.getDecimal());

                
                CoinPriceBean coinPriceBean = new CoinPriceBean();
                coinPriceBean.assertBean = assertBean;
                coinPriceBean.price = price;
                coinPriceBean.toAmount = toAmount;
                emitter.onNext(coinPriceBean);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public Observable<CoinPriceBean> getPancakeSwapUsdtPrice(boolean isDebug, AssertBean assertBean) {
        return Observable.create(new ObservableOnSubscribe<CoinPriceBean>() {
            @Override
            public void subscribe(ObservableEmitter<CoinPriceBean> emitter) throws Exception {

                if (null == assertBean) {
                    throw new Exception("assert is null");
                }
                int walletType = assertBean.getType();
                
                String myWallet = "0x8aB9f0f74B39211c3D26101367010896E905baaC";

                String swapContract = "";
                String usdtContract = "";
                int usdtDecimal = 18; 
                if (walletType == WalletUtil.BNB_COIN) {
                    
                    String pancakeSwapContract = isDebug ? "0xD99D1c33F9fC3444f8101754aBC46c52416550D1" : "0x10ED43C718714eb63d5aA57B78B54704E256024E";
                    swapContract = pancakeSwapContract;
                    usdtContract = isDebug ? "0xb0f5E208507dED150fEE5b39F78A1b109Cd81A12" : "0x55d398326f99059fF775485246999027B3197955";
                    usdtDecimal = 18;
                } else if (walletType == WalletUtil.ETH_COIN) {
                    
                    String uniSwapContract = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D";
                    swapContract = uniSwapContract;
                    usdtContract = "0xdac17f958d2ee523a2206206994597c13d831ec7";
                    usdtDecimal = 6;
                } else {
                    throw new Exception("un support this chain price get");
                }

                
                String rpcUrl = SpUtil.getDefNode(walletType);
                Web3j web3j = Web3j.build(new HttpService(rpcUrl));

                
                List<String> tokens = new ArrayList<>();
                
                
                String coinContract = getContract(assertBean);
                if (usdtContract.equalsIgnoreCase(coinContract)) {
                    
                    CoinPriceBean coinPriceBean = new CoinPriceBean();
                    coinPriceBean.assertBean = assertBean;
                    coinPriceBean.price = "1";
                    emitter.onNext(coinPriceBean);
                    emitter.onComplete();
                    return;
                }
                tokens.add(coinContract); 
                
                tokens.add(usdtContract);

                
                int fsvDecimal = assertBean.getDecimal(); 

                
                
                
                
                String FUNC_GETAMOUNTSOUT = "getAmountsOut";
                BigInteger amountIn = new BigDecimal(Math.pow(10, fsvDecimal)).toBigInteger();
                final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                        FUNC_GETAMOUNTSOUT,
                        Arrays.asList(new org.web3j.abi.datatypes.generated.Uint256(amountIn),
                                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                        org.web3j.abi.datatypes.Address.class,
                                        org.web3j.abi.Utils.typeMap(tokens, org.web3j.abi.datatypes.Address.class))),
                        Arrays.asList(new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        }));

                
                String data = FunctionEncoder.encode(function);
                
                org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                        Transaction.createEthCallTransaction(myWallet, swapContract, data),
                        DefaultBlockParameterName.LATEST).sendAsync().get();

                
                List<Type> someTypes = FunctionReturnDecoder.decode(
                        response.getValue(), function.getOutputParameters());
                String price = "";
                if (null != someTypes && someTypes.size() == 4) {
                    Uint256 value = (Uint256) someTypes.get(3);
                    price = new BigDecimal(value.getValue()).divide(new BigDecimal(Math.pow(10, usdtDecimal)), 5, RoundingMode.HALF_UP).toPlainString();
                }
                LogUtil.d("getContract??==    price: " + price + " ,getShort_name: " + assertBean.getShort_name() + "  , getContract: " + assertBean.getContract() + " ,getDecimal: " + assertBean.getDecimal());

                
                CoinPriceBean coinPriceBean = new CoinPriceBean();
                coinPriceBean.assertBean = assertBean;
                coinPriceBean.price = price;
                emitter.onNext(coinPriceBean);
                emitter.onComplete();
            }
        });
    }

    public static String getContract(AssertBean assertBean) {
        if (TextUtils.isEmpty(assertBean.getContract()) && assertBean.getType() == WalletUtil.BNB_COIN) {
            return Constants.WBNB_BSC_CONTRACT;
        } else if (TextUtils.isEmpty(assertBean.getContract()) && assertBean.getType() == WalletUtil.ETH_COIN) {
            return "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2";
        }
        return assertBean.getContract();
    }


    
    public Observable<EthAssertBean> searchTokenInfoByContract(int walletType, String contractAddr) {
        return Observable.create(new ObservableOnSubscribe<EthAssertBean>() {
                    @Override
                    public void subscribe(@NotNull ObservableEmitter<EthAssertBean> emitter) throws Exception {
                        String nodeUrl = SpUtil.getDefNode(walletType);
                        if (walletType == MCC_COIN) {
                            
                            nodeUrl = SpUtil.getNodeSmartUrl();
                        }
                        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
                        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                                "decimals",
                                Arrays.<Type>asList(),
                                Arrays.asList(new TypeReference<Uint8>() {
                                }));

                        final org.web3j.abi.datatypes.Function symbolFunction = new org.web3j.abi.datatypes.Function(
                                "symbol",
                                Arrays.<Type>asList(),
                                Arrays.asList(new TypeReference<Utf8String>() {
                                }));

                        final org.web3j.abi.datatypes.Function nameFunction = new org.web3j.abi.datatypes.Function(
                                "name",
                                Arrays.<Type>asList(),
                                Arrays.asList(new TypeReference<Utf8String>() {
                                }));

                        String encodedFunction = FunctionEncoder.encode(function);
                        String encodedSymbolFunction = FunctionEncoder.encode(symbolFunction);
                        String encodedNameFunction = FunctionEncoder.encode(nameFunction);
                        EthCall decimalsResponse = null;
                        EthCall symbolResponse = null;
                        EthCall nameResponse = null;
                        try {
                            decimalsResponse = web3j.ethCall(
                                    Transaction.createEthCallTransaction("0x8aB9f0f74B39211c3D26101367010896E905baaC", contractAddr, encodedFunction),
                                    DefaultBlockParameterName.LATEST).sendAsync().get();

                            symbolResponse = web3j.ethCall(
                                    Transaction.createEthCallTransaction("0x8aB9f0f74B39211c3D26101367010896E905baaC", contractAddr, encodedSymbolFunction),
                                    DefaultBlockParameterName.LATEST).sendAsync().get();

                            nameResponse = web3j.ethCall(
                                    Transaction.createEthCallTransaction("0x8aB9f0f74B39211c3D26101367010896E905baaC", contractAddr, encodedNameFunction),
                                    DefaultBlockParameterName.LATEST).sendAsync().get();

                            String decimals = "";
                            String symbos = "";
                            String name = "";
                            List<Type> someTypes = FunctionReturnDecoder.decode(
                                    decimalsResponse.getValue(), function.getOutputParameters());
                            if (null != someTypes && someTypes.size() == 1) {
                                Uint8 value = (Uint8) someTypes.get(0);
                                decimals = String.valueOf(value.getValue());
                                LogUtil.i("decimals =" + value.getValue());
                            }


                            List<Type> someTypes2 = FunctionReturnDecoder.decode(
                                    symbolResponse.getValue(), symbolFunction.getOutputParameters());
                            if (null != someTypes2 && someTypes2.size() == 1) {
                                Utf8String value = (Utf8String) someTypes2.get(0);
                                symbos = value.getValue();
                                LogUtil.i("symbol =" + value.getValue());
                            }

                            List<Type> someTypes3 = FunctionReturnDecoder.decode(
                                    nameResponse.getValue(), nameFunction.getOutputParameters());
                            if (null != someTypes3 && someTypes3.size() == 1) {
                                Utf8String value = (Utf8String) someTypes3.get(0);
                                name = value.getValue();
                                LogUtil.i("name =" + value.getValue());
                            }
                            EthAssertBean assertBean = new EthAssertBean();
                            if (!TextUtils.isEmpty(decimals) && !TextUtils.isEmpty(symbos) && !TextUtils.isEmpty(name)) {
                                assertBean.setAddress(contractAddr);
                                assertBean.setDecimals(decimals);
                                assertBean.setSymbol(symbos);
                                assertBean.setName(name);
                                if ("usdt".equalsIgnoreCase(symbos)) {
                                    assertBean.setLogo("res://mipmap/usdt_logo");
                                } else {
                                    assertBean.setLogo("res://drawable/coin_default");
                                }
                            }
                            if (walletType == MCC_COIN) {
                                

                                String url = getEvmosRpcUrl() + "contract/tokeninfo";
                                Map<String, Object> paramsMap = new HashMap<>();
                                paramsMap.put("address", contractAddr);
                                mService
                                        .getTokeninfo(url, paramsMap)
                                        .filter(info -> info.isSuccess() && null != info.data && !TextUtils.isEmpty(info.data.logo))
                                        .map(info -> info.data.logo).subscribe(s -> assertBean.setLogo(s));
                            }
                            emitter.onNext(assertBean);
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            emitter.onError(e);
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            emitter.onError(e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    
    public Observable<String> getTokenAllowanceAmount(String address, int walletType, String spenderContractAddr, String tokenContractAddr) {
        return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NotNull ObservableEmitter<String> emitter) throws Exception {
                        String nodeUrl = SpUtil.getDefNode(walletType);
                        Web3j web3j = Web3j.build(new HttpService(nodeUrl));

                        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                                "allowance",
                                Arrays.<Type>asList(new Address(address), new Address(spenderContractAddr)),
                                Arrays.asList(new TypeReference<Uint256>() {
                                }));

                        String encodedFunction = FunctionEncoder.encode(function);
                        org.web3j.protocol.core.methods.response.EthCall response = null;
                        try {
                            response = web3j.ethCall(
                                    Transaction.createEthCallTransaction(address, tokenContractAddr, encodedFunction),
                                    DefaultBlockParameterName.LATEST).sendAsync().get();

                            LogUtil.i("" + response.toString());
                            String amount = "";
                            List<Type> someTypes = FunctionReturnDecoder.decode(
                                    response.getValue(), function.getOutputParameters());
                            if (null != someTypes && someTypes.size() == 1) {
                                Uint256 value = (Uint256) someTypes.get(0);
                                amount = String.valueOf(value.getValue());
                                LogUtil.i("amount =" + value.getValue());
                            }

                            emitter.onNext(amount);
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            emitter.onError(e);
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            emitter.onError(e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    
    public Observable<ValidatorInfo> getValidatorInfo(String validator_address, int page, int offset) {
        String url = getNodeInfoUrl() + "cosmos/staking/v1beta1/validators/" + validator_address + "/delegations";

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("pagination.limit", "1");
        paramsMap.put("pagination.offset", offset);
        return mService.getValidatorInfo(url, paramsMap);
    }
}
