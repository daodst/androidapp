

package com.app.node;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.AppController;
import com.app.R;
import com.app.pojo.NodeConfigBean;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import common.app.utils.NetWorkUtils;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
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


public class NodeCheckVM extends BaseViewModel {
    private static final String TAG = "NodeCheckVM";
    public MutableLiveData<List<SettingNodeEntity>> mGateWaysLD;
    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;
    private List<SettingNodeEntity> mData;
    public MutableLiveData<Boolean> mRefreshLD;
    public MutableLiveData<EvmosPledgeResultBean> mChangeNodeResultLD;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    public MutableLiveData<Integer> mShowPwdLD;

    
    public NodeCheckVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mGateWaysLD = new MutableLiveData<>();
        mRefreshLD = new MutableLiveData<>();
        mChangeNodeResultLD = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
        mShowPwdLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
    }

    
    public void refreshData(boolean isLogin) {
        getGateWays(isLogin, true, 0, 0);
    }

    
    public void sort(boolean isLogin, int sortByAmount, int sortByTime) {
        getGateWays(isLogin, false, sortByAmount, sortByTime);
    }


    public void getEvmosGateway() {
        String noSegment = SpUtil.getNodeNoSegm();
        mRpcApi.getEvmosGateway(noSegment).compose(RxSchedulers.io_main()).subscribe(new NextSubscriber<EvmosGatewayBean>() {
            @Override
            public void dealData(EvmosGatewayBean value) {
                if (value.isSuccess() && null != value.data) {
                    EvmosGatewayBean.Data data = value.data;

                    SettingNodeEntity entity = new SettingNodeEntity();
                    entity.setNodeUrl(entity.getNodeUrl());
                } else {
                    dealError(new RuntimeException(value.getInfo()));
                }
            }
        });
    }

    public void getGateWays(boolean isLogin, boolean refresh, int sortByAmount, int sortByTime) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<SettingNodeEntity>>) emitter -> {
            List<SettingNodeEntity> list = mGateWaysLD.getValue();
            if (refresh || null == list || list.size() == 0) {
                
                list = DBManager.getInstance(getApplication()).getAllTypeNode(WalletUtil.MCC_COIN);
            }

            if (isLogin) {
                String nowUrl = SpUtil.getDefNode(WalletUtil.MCC_COIN);
                String nowHost = "";
                if (!TextUtils.isEmpty(nowUrl)) {
                    nowHost = AllUtils.urlToHost(nowUrl);
                }

                for (int i = 0; null != list && i < list.size(); i++) {
                    SettingNodeEntity entity = list.get(i);
                    Log.i(TAG, list.size() + "-----" + nowHost + "-------" + entity.getNodeName());
                    if (TextUtils.equals(nowHost, entity.getHost())) {
                        list = new ArrayList<>();
                        list.add(entity);
                        break;
                    }
                }
            }
            Log.i(TAG, list.size() + "--------");
            emitter.onNext(sortDatas(isLogin, list, sortByAmount, sortByTime));
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(nodeLists -> {
            mRefreshLD.setValue(true);
            mGateWaysLD.setValue(nodeLists);
            
            checkUrlTime();
        }, error -> {
            mRefreshLD.setValue(false);
            error.printStackTrace();
            String errorInfo = error.getMessage();
            showToast(errorInfo);
        });
        mDisposable.add(disposable);
    }

    
    private boolean hasAddNowNode = false;

    private List<SettingNodeEntity> sortDatas(boolean isLogin, List<SettingNodeEntity> datas, int sortByAmount, int sortByTime) {
        if (null == datas || datas.size() == 0) {
            return datas;
        }
        String nowUrl = SpUtil.getDefNode(WalletUtil.MCC_COIN);
        String nowHost = "";
        if (!TextUtils.isEmpty(nowUrl)) {
            nowHost = AllUtils.urlToHost(nowUrl);
        }
        hasAddNowNode = false;
        String finalNowHost = nowHost;
        Collections.sort(datas, new Comparator<SettingNodeEntity>() {
            @Override
            public int compare(SettingNodeEntity n1, SettingNodeEntity n2) {
                
                

                if (!TextUtils.isEmpty(finalNowHost) && finalNowHost.equals(n1.getHost())) {
                    hasAddNowNode = true;
                    n1.setChoose(true);
                    return -1;
                } else if (!TextUtils.isEmpty(finalNowHost) && finalNowHost.equals(n2.getHost())) {
                    hasAddNowNode = true;
                    n2.setChoose(true);
                    return 1;
                } else {
                    if (n1.getIsDef() == 1 || n2.getIsDef() == 1) {
                        
                        return n2.getIsDef() - n1.getIsDef();
                    }

                    if (sortByAmount > 0) {
                        String num1 = n1.getTokenNum();
                        String num2 = n2.getTokenNum();
                        if (TextUtils.isEmpty(num1) && TextUtils.isEmpty(num2)) {
                            return 0;
                        } else if (!TextUtils.isEmpty(num1) && !TextUtils.isEmpty(num2)) {
                            return num2.compareTo(num1);
                        } else if (!TextUtils.isEmpty(num1)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (sortByTime > 0) {
                        if (n2.getOnLineTime() > n1.getOnLineTime()) {
                            return 1;
                        } else if (n2.getOnLineTime() == n1.getOnLineTime()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                }
                return 0;
            }
        });
        boolean b = datas.size() == 1;
        SettingNodeEntity entity = null;
        if (b) {
            entity = datas.get(0);
        }
        
        if (!hasAddNowNode && !TextUtils.isEmpty(nowUrl) && !isLogin) {

            String nodeName = SpUtil.getNodeName();
            String mainPhoneIndex = SpUtil.getNodeNoSegm();
            

            SettingNodeEntity customNode = new SettingNodeEntity(nodeName, nowUrl, WalletUtil.MCC_COIN, true, 1, mainPhoneIndex, "", 0, "");
            if (null != entity && !TextUtils.equals(entity.getHost(), customNode.getHost())) {
                datas.add(0, customNode);
            }
        }
        return datas;
    }

    private ExecutorService mExService;
    private Map<String, Integer> mCheckingUrlMap = new ConcurrentHashMap<>();

    private void checkUrlTime() {
        stopCalculateTask();
        if (null == mData) {
            mData = new ArrayList<>();
        }
        mData.clear();
        mData.addAll(mGateWaysLD.getValue());
        if (null == mData || mData.size() == 0) {
            return;
        }
        
        if (null == mExService) {
            mExService = Executors.newFixedThreadPool(3);
        }
        for (int i = 0; i < mData.size(); i++) {
            SettingNodeEntity node = mData.get(i);
            String host = node.getHost();
            NodeConfigBean newNodeConfig = NodeConfigBean.prase(node.getNodeUrl(), node.getMainPhoneIndex(), node.getNodeName());
            String url = newNodeConfig != null ? newNodeConfig.node_address : "";
            if (TextUtils.isEmpty(url) || mCheckingUrlMap.containsKey(host)) {
                LogUtil.w(url + " is checking return.");
                continue;
            }
            mCheckingUrlMap.put(host, 1);
            mExService.submit(new CheckUrlTimeTask(host, url));
        }
    }

    private class CheckUrlTimeTask implements Runnable {
        private String url;
        private String host;

        public CheckUrlTimeTask(String host, String url) {
            this.host = host;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                long time = NetWorkUtils.pingIpAddress2(url);
                updateTime(host, url, time);
            } catch (Exception e) {
                e.printStackTrace();
                updateTime(host, url, -1);
            } finally {
                mCheckingUrlMap.remove(host);
            }
        }
    }

    
    public void updateTime(String host, String url, long time) {
        if (null == mData || mData.size() == 0 || TextUtils.isEmpty(url)) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            SettingNodeEntity node = mData.get(i);
            if (host.equals(node.getHost())) {
                node.setPingTime(time);
                break;
            }
        }
        mGateWaysLD.postValue(mData);
    }

    
    private void stopCalculateTask() {
        if (null == mExService) {
            return;
        }
        try {
            
            mExService.shutdown();
            
            if (!mExService.awaitTermination(1, TimeUnit.SECONDS)) {
                
                mExService.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mExService.shutdownNow();
        } finally {
            mExService = null;
        }
    }

    
    public void showGasAlert(String address, SettingNodeEntity newGateWay) {
        NodeConfigBean newNodeConfig = NodeConfigBean.prase(newGateWay.getNodeUrl(), newGateWay.getMainPhoneIndex(), newGateWay.getNodeName());
        if (newNodeConfig == null || !newNodeConfig.isValidate()) {
            showToast(getApplication().getString(R.string.node_is_unright));
            return;
        }
        mNewRpcHost = newNodeConfig.node_address;
        String newNodeAddress = newGateWay.getGateWayAddr();
        showLoadingDialog("");
        mRpcApi.getEvmosChatFeeSetting(mNewRpcHost, address).concatMap(new Function<EvmosChatFeeBean, ObservableSource<EvmosSeqGasBean>>() {
            @Override
            public ObservableSource<EvmosSeqGasBean> apply(EvmosChatFeeBean evmosChatFeeBean) throws Exception {
                if (null == evmosChatFeeBean || null == evmosChatFeeBean.data || !evmosChatFeeBean.isSuccess()) {
                    String errorInfo = evmosChatFeeBean != null ? evmosChatFeeBean.getInfo() : "get chat fee setting。 ";
                    throw new Exception(errorInfo);
                } else if(TextUtils.isEmpty(evmosChatFeeBean.data.from_address)) {
                    
                    throw new Exception("canDirectSwitchNode");
                }
                return mRpcApi.getChangeNodeGas(mNewRpcHost, address, newNodeAddress, evmosChatFeeBean).map(new Function<EvmosSeqGasBean, EvmosSeqGasBean>() {
                    @Override
                    public EvmosSeqGasBean apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                        evmosSeqGasBean.feeSetting = evmosChatFeeBean;
                        return evmosSeqGasBean;
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NextSubscriber<EvmosSeqGasBean>() {
            @Override
            public void dealData(EvmosSeqGasBean value) {
                dismissLoadingDialog();
                if (value != null && value.isSuccess()) {
                    mShowGasDialogLD.setValue(value);
                } else {
                    String errorInfo = value != null ? value.getInfo() : "get gas result is null";
                    showToast(errorInfo);
                }
            }

            @Override
            protected void dealError(Throwable e) {
                LogUtil.w(e+">>"+e.getMessage());
                if (null != e && "canDirectSwitchNode".equals(e.getMessage())) {
                    mShowPwdLD.setValue(1);
                } else {
                    super.dealError(e);
                }
                dismissLoadingDialog();

            }
        });
    }


    
    private String mNewRpcHost;

    public void swithNode(String address, SettingNodeEntity newGateWay, WalletEntity wallet, String pwd) {
        NodeConfigBean newNodeConfig = NodeConfigBean.prase(newGateWay.getNodeUrl(), newGateWay.getMainPhoneIndex(), newGateWay.getNodeName());
        if (newNodeConfig == null || !newNodeConfig.isValidate()) {
            showToast(getApplication().getString(R.string.node_is_unright));
            return;
        }
        mNewRpcHost = newNodeConfig.node_address;

        String newNodeAddress = newGateWay.getGateWayAddr();
        showLoadingDialog("");

        Observable.zip(mRpcApi.getImGateWayPublickey(newNodeConfig.im_url), mRpcApi.getEvmosChatFeeSetting(mNewRpcHost, address), (serverPublickKey, evmosChatFeeBean) -> {
                    if (TextUtils.isEmpty(serverPublickKey)) {
                        throw new Exception(getApplication().getString(R.string.get_gateway_key_fail));
                    }
                    if (null == evmosChatFeeBean || null == evmosChatFeeBean.data || !evmosChatFeeBean.isSuccess()) {
                        String errorInfo = evmosChatFeeBean != null ? evmosChatFeeBean.getInfo() : "get chat fee setting。 ";
                        throw new Exception(errorInfo);
                    }
                    if (evmosChatFeeBean.isHasBlackOrWhiteDatas()) {
                        
                        String onlineHash = evmosChatFeeBean.getBlackWhiteHash();
                        String whiteListEncryStr = evmosChatFeeBean.getWhiteListEncryStr();
                        String blackListEncryStr = evmosChatFeeBean.getBlackListEncryStr();
                        List<String> whiteList = EvmosChatFeeBean.decryList(wallet, pwd, whiteListEncryStr);
                        List<String> blackList = EvmosChatFeeBean.decryList(wallet, pwd, blackListEncryStr);
                        if (whiteList != null && whiteList.size() > 0) {
                            
                            String encryWhiteListGateway = EvmosChatFeeBean.encryListGateWay(serverPublickKey, whiteList);
                            evmosChatFeeBean.data.chat_white_enc_list = encryWhiteListGateway;
                        }
                        if (blackList != null && blackList.size() > 0) {
                            
                            String encryBlackListGateWay = EvmosChatFeeBean.encryListGateWay(serverPublickKey, blackList);
                            evmosChatFeeBean.data.chat_black_enc_list = encryBlackListGateWay;
                        }
                    }
                    return evmosChatFeeBean;
                })
                .concatMap(new Function<EvmosChatFeeBean, ObservableSource<EvmosSeqGasBean>>() {
                    @Override
                    public ObservableSource<EvmosSeqGasBean> apply(EvmosChatFeeBean evmosChatFeeBean) throws Exception {

                        return mRpcApi.getChangeNodeGas(mNewRpcHost, address, newNodeAddress, evmosChatFeeBean).map(new Function<EvmosSeqGasBean, EvmosSeqGasBean>() {
                            @Override
                            public EvmosSeqGasBean apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                                evmosSeqGasBean.feeSetting = evmosChatFeeBean;
                                return evmosSeqGasBean;
                            }
                        });
                    }
                }).concatMap(new Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>() {
                    @Override
                    public ObservableSource<EvmosSignResult> apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                        if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                            return changeNodeSign(address, newNodeAddress, evmosSeqGasBean.feeSetting, evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(), evmosSeqGasBean.gas.getGasLimit(), wallet, pwd);
                        } else {
                            throw new Exception(evmosSeqGasBean.getInfo());
                        }
                    }
                }).concatMap(new Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>() {
                    @Override
                    public ObservableSource<EvmosTransferResultBean> apply(EvmosSignResult evmosSignResult) throws Exception {
                        if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                            return mRpcApi.submitEvmosTransfer(mNewRpcHost, evmosSignResult.Data);
                        } else {
                            throw new Exception(evmosSignResult.getInfo());
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosTransferResultBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        dismissLoadingDialog();
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data, false, 1, newGateWay);
                        } else {
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dismissLoadingDialog();
                        ToastUtil.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    private int mUseTime;

    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times, SettingNodeEntity newGateWay) {
        if (retry) {
            mUseTime += times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
            mRpcApi.getEvmosHxResult(mNewRpcHost, transferResult.data.tx_hash).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosHxResultBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(EvmosHxResultBean evmosHxResultBean) {
                    if (evmosHxResultBean.isTxSuccess()) {
                        dismissLoadingDialog();
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = true;
                        postTxReslult(result, newGateWay);
                    } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                        dismissLoadingDialog();
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = false;
                        result.info = getApplication().getString(com.wallet.ctc.R.string.transfer_fail);
                        postTxReslult(result, newGateWay);
                    } else {
                        if (mUseTime > 7) {
                            dismissLoadingDialog();
                            
                            EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                            result.success = false;
                            result.info = getApplication().getString(R.string.wait_confirm_trx_result);
                            postTxReslult(result, newGateWay);
                        } else {
                            checkTxResult(transferResult, true, 2, newGateWay);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (mUseTime > 7) {
                        dismissLoadingDialog();
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = false;
                        result.info = getApplication().getString(R.string.wait_confirm_trx_result);
                        postTxReslult(result, newGateWay);
                    } else {
                        checkTxResult(transferResult, true, 2, newGateWay);
                    }
                }

                @Override
                public void onComplete() {

                }
            });
        });
        mDisposable.add(disposable);
    }

    
    public void postTxReslult(EvmosPledgeResultBean resultBean, SettingNodeEntity newGateWay) {
        if (resultBean.success) {
            
            NodeConfigBean config = NodeConfigBean.prase(newGateWay.getNodeUrl(), newGateWay.getMainPhoneIndex(), newGateWay.getNodeName());
            if (null != config && config.isValidate()) {
                WalletUtil.saveNodeInfo(WalletUtil.MCC_COIN, config.node_address, config.number_index, config.node_name, config.im_url, config.node_smart_url, config.node_info_url, config.chatCall, config.chain_id,config.ws_url,config.tts_url);

                
                AppController.initSdkNodeAddr();
            }
        }
        mChangeNodeResultLD.setValue(resultBean);
    }


    
    private Observable<EvmosSignResult> changeNodeSign(String address, String nodeAddress, EvmosChatFeeBean feeSetting, EvmosSeqAcountBean.Data seqAccountBean, final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                
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
                String memo = "";
                LogUtil.i("accountNum=" + accountNum + ", accountSeq=" + accountSeq);
                ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, memo);
                byte[] signByte = {};
                signByte = ChatSdk.signChangeNode(nodeAddress, feeSetting);
                
                
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
            }
        }).subscribeOn(Schedulers.io());
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
        stopCalculateTask();
    }
}

