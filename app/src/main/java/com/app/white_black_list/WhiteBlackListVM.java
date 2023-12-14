package com.app.white_black_list;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.EvmosUserBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.mall.util.ToastUtil;
import im.vector.app.provide.ChatStatusProvide;
import im.vector.app.provide.UserInfo;
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
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class WhiteBlackListVM extends BaseViewModel {
    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<EvmosPledgeResultBean> mTransfResultLD;
    public MutableLiveData<EvmosChatFeeBean> mSettingLD;
    public MutableLiveData<List<EvmosUserBean>> mListLD;
    public static List<EvmosUserBean> mUnUpWhiteList = new ArrayList<>();
    public static List<EvmosUserBean> mUnUpBlackList = new ArrayList<>();

    public MutableLiveData<List<EvmosUserBean>> mToRemoveLD;

    
    public WhiteBlackListVM(@NonNull Application application) {
        super(application);
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mTransfResultLD = new MutableLiveData<>();
        mSettingLD = new MutableLiveData<>();
        mListLD = new MutableLiveData<>();
        mToRemoveLD = new MutableLiveData<>();
    }

    
    public void removeDelListUser(EvmosUserBean userBean, int mode) {
        if (null == userBean) {
            return;
        }
        List<EvmosUserBean> delList = mToRemoveLD.getValue();
        if (null != delList) {
            delList.remove(userBean);
        }
        mToRemoveLD.setValue(delList);

        List<EvmosUserBean> userBeanList = mListLD.getValue();
        if (null == userBeanList) {
            userBeanList = new ArrayList<>();
        }
        userBeanList.add(userBean);
        mListLD.setValue(userBeanList);
    }

    
    public void addLocalUser(String address, String allUserId, String mobile, int mode) {
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(allUserId)) {
            Log.w("WhiteBlackList","adreess or allUserid is null");
            return;
        }
        boolean hasAdded = false;
        if (mode == WhiteBlackListActivity.MODE_WHITE) {
            
            if (mUnUpWhiteList.size() > 0) {
                for (EvmosUserBean userInfo : mUnUpWhiteList) {
                    if (address.equalsIgnoreCase(userInfo.address)) {
                        hasAdded = true;
                        break;
                    }
                }
            }
            if (!hasAdded) {
                mUnUpWhiteList.add(new EvmosUserBean(address, allUserId, mobile, false));
            }
        } else if(mode == WhiteBlackListActivity.MODE_BLACK) {
            
            if (mUnUpBlackList.size() > 0) {
                for (EvmosUserBean userInfo : mUnUpBlackList) {
                    if (address.equalsIgnoreCase(userInfo.address)) {
                        hasAdded = true;
                        break;
                    }
                }
            }
            if (!hasAdded) {
                mUnUpBlackList.add(new EvmosUserBean(address, allUserId, mobile, false));
            }
        }
    }

    
    public void removeUser(EvmosUserBean toRemoveUser, int mode) {
        if (null == toRemoveUser || TextUtils.isEmpty(toRemoveUser.address)) {
            return;
        }
        String delAddr = toRemoveUser.address;
        if (mode == WhiteBlackListActivity.MODE_WHITE && mUnUpWhiteList.size() > 0) {
            
            Iterator<EvmosUserBean> iterator = mUnUpWhiteList.iterator();
            while (iterator.hasNext()) {
                EvmosUserBean user = iterator.next();
                if (delAddr.equalsIgnoreCase(user.address)) {
                    iterator.remove();
                }
            }
        } else if (mode == WhiteBlackListActivity.MODE_BLACK && mUnUpBlackList.size() > 0) {
            
            Iterator<EvmosUserBean> iterator = mUnUpBlackList.iterator();
            while (iterator.hasNext()) {
                EvmosUserBean user = iterator.next();
                if (delAddr.equalsIgnoreCase(user.address)) {
                    iterator.remove();
                }
            }
        }

        
        List<EvmosUserBean> usersList = mListLD.getValue();
        List<EvmosUserBean> toRemoveList = new ArrayList<>();
        Iterator<EvmosUserBean> iterator = usersList.iterator();
        while (iterator.hasNext()) {
            EvmosUserBean user = iterator.next();
            if (delAddr.equalsIgnoreCase(user.address)) {
                iterator.remove();
                
                if (user.isEffect) {
                    toRemoveList.add(user);
                }
            }
        }
        mListLD.setValue(usersList);

        
        if (toRemoveList.size() > 0) {
            List<EvmosUserBean> nowRemoveList = mToRemoveLD.getValue();
            if (null == nowRemoveList) {
                nowRemoveList = new ArrayList<>();
            }
            nowRemoveList.addAll(toRemoveList);
            mToRemoveLD.setValue(nowRemoveList);
        }

    }

    
    public void getUserList(String address, int mode) {
        mRpcApi.getEvmosChatFeeSetting(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosChatFeeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosChatFeeBean evmosChatFeeBean) {
                        if (null != evmosChatFeeBean && evmosChatFeeBean.isSuccess() && null != evmosChatFeeBean.data) {
                            mSettingLD.setValue(evmosChatFeeBean);
                            List<EvmosUserBean> users = null;
                            if (mode == WhiteBlackListActivity.MODE_WHITE) {
                                
                                

                                
                                if (users != null && users.size() > 0 && mUnUpWhiteList.size() > 0) {
                                    Iterator<EvmosUserBean> iterator = mUnUpWhiteList.iterator();
                                    while (iterator.hasNext()){
                                        EvmosUserBean user = iterator.next();
                                        for (EvmosUserBean listUser : users) {
                                            if (user.address.equalsIgnoreCase(listUser.address)) {
                                                
                                                iterator.remove();
                                                break;
                                            }
                                        }
                                    }
                                } else if (null == users) {
                                    users = new ArrayList<>();
                                }
                                if (mUnUpWhiteList.size() > 0) {
                                    users.addAll(0, mUnUpWhiteList);
                                }
                            } else if(mode == WhiteBlackListActivity.MODE_BLACK) {
                                
                                
                                
                                if (users != null && users.size() > 0 && mUnUpBlackList.size() > 0) {
                                    Iterator<EvmosUserBean> iterator = mUnUpBlackList.iterator();
                                    while (iterator.hasNext()){
                                        EvmosUserBean user = iterator.next();
                                        for (EvmosUserBean listUser : users) {
                                            if (user.address.equalsIgnoreCase(listUser.address)) {
                                                
                                                iterator.remove();
                                                break;
                                            }
                                        }
                                    }
                                } else if (null == users) {
                                    users = new ArrayList<>();
                                }
                                if (mUnUpBlackList.size() > 0) {
                                    users.addAll(0, mUnUpBlackList);
                                }
                            }
                            getUserList(users);
                        } else {
                            String errorInfo = evmosChatFeeBean != null ? evmosChatFeeBean.getInfo() : "get fee setting fail";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    
    private void getUserList(List<EvmosUserBean> userLists) {
        if (null == userLists || userLists.size() == 0) {
            mListLD.setValue(new ArrayList<>());
            return;
        }
        Context context = getApplication();

        List<String> addressList = new ArrayList<>();
        for (EvmosUserBean userBean : userLists) {
            if (!TextUtils.isEmpty(userBean.comm_address)) {
                addressList.add(userBean.comm_address);
            }
        }
        
        ChatStatusProvide.searchUserInfoList(WhiteBlackListVM.this, context, addressList, new Function1<List<UserInfo>, Unit>() {
            @Override
            public Unit invoke(List<UserInfo> userInfos) {
                LogUtil.i("---"+Thread.currentThread());
                if (null != userInfos && userInfos.size() > 0) {
                    for (int i=0; i<userLists.size(); i++) {
                        EvmosUserBean userBean = userLists.get(i);
                        for (int j=0; j<userInfos.size(); j++) {
                            UserInfo userInfo = userInfos.get(j);
                            if (userInfo.getAddress().equalsIgnoreCase(userBean.comm_address)) {
                                userBean.logo = userInfo.getAvatarUrl();
                                userBean.nickName = userInfo.getDisplayName();
                                break;
                            }
                        }
                    }
                }
                mListLD.setValue(userLists);
                return null;
            }
        });

    }

    
    private List<String> getResultList() {
        List<EvmosUserBean> list = mListLD.getValue();
        List<String> addressList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (EvmosUserBean userBean : list) {
                addressList.add(userBean.address);
            }
        }
        return addressList;
    }


    public void setChatFee(String fromAddr, int mode, EvmosChatFeeBean feeSetting,  WalletEntity wallet, String pwd) {
        ChatStatusProvide provide = ChatStatusProvide.INSTANCE;
        provide.getServerPubKeyTask(getApplication(), this, gateWayPublickKey -> {
            if (TextUtils.isEmpty(gateWayPublickKey)) {
                showToast("no found gateway publickey!!");
                return null;
            }
            setChatFee(fromAddr, mode, feeSetting, wallet, pwd, gateWayPublickKey);
            return null;
        });
    }

    
    public Observable<EvmosSeqGasBean> getSaveListGas(int mode, String fromAddress, String encryList,
                                                           String encryListGateWay, EvmosChatFeeBean chatFeeBean) {
        if (mode == WhiteBlackListActivity.MODE_BLACK) {
            
            return mRpcApi.getSaveBlackListGas(fromAddress, encryList, encryListGateWay, chatFeeBean);
        } else {
            
            return mRpcApi.getSaveWhiteListGas(fromAddress, encryList, encryListGateWay, chatFeeBean);
        }
    }

    
    public void setChatFee(String fromAddr, int mode, EvmosChatFeeBean feeSetting,  WalletEntity wallet, String pwd, String serverPublicKey) {


        
        String encryPublickKey = ChatSdk.getPubEcKeyFromPri(wallet.decodePrivateKey(pwd));
        List<String> list = getResultList();
        String encryStr = EvmosChatFeeBean.encryList(encryPublickKey, list);
        String encryStrGateWay = EvmosChatFeeBean.encryList(serverPublicKey, list);

        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        String feeMode = feeSetting.data.chat_restricted_mode;
        String bigAmount = feeSetting.data.getChatFeeAmount();

        
        
        String finalBigAmount = bigAmount;
        showLoadingDialog("");
        getSaveListGas(mode, fromAddr, encryStr, encryStrGateWay, feeSetting).concatMap(new Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>() {
                    @Override
                    public ObservableSource<EvmosSignResult> apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                        if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                            return signSetChatFee(mode, fromAddr, encryStr, encryStrGateWay, feeSetting,  evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(),
                                    evmosSeqGasBean.gas.getGasLimit(), wallet, pwd);
                        } else {
                            throw new Exception(evmosSeqGasBean.getInfo());
                        }
                    }
                }).concatMap(new Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>() {
                    @Override
                    public ObservableSource<EvmosTransferResultBean> apply(EvmosSignResult evmosSignResult) throws Exception {
                        if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                            return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                        } else {
                            throw new Exception(evmosSignResult.getInfo());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosTransferResultBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data, false, 1);
                        } else {
                            dismissLoadingDialog();
                            String errorInfo = data != null ? data.getInfo() : "set chat fee fail";
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


    
    private Observable<EvmosSignResult> signSetChatFee(int mode, String address, String encryStr, String encryStrGateWay,
                                                       EvmosChatFeeBean feeSetting, EvmosSeqAcountBean.Data seqAccountBean,
                                                       final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                
                String publickey = new String(wallet.getmPublicKey());
                String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
                LogUtil.i("publickey="+publickey+", \nprivateKey="+privateKey+", \naddress="+address);
                ChatSdk.setupCosmosWallet(address, publickey, privateKey);

                
                String gasAmount2 = gasAmount;
                if (TextUtils.isEmpty(gasAmount2)) {
                    gasAmount2 = "100000000000000000";
                }
                String gasLimit2 = gasLimit;
                if (TextUtils.isEmpty(gasLimit2)) {
                    gasLimit2 = "2000000";
                }
                String accountNum = seqAccountBean.account_number+"";
                String accountSeq = seqAccountBean.sequence+"";
                String remark = "";
                LogUtil.i("accountNum="+accountNum+", accountSeq="+accountSeq);
                ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, remark);

                byte[] signByte = {};
                if (mode == WhiteBlackListActivity.MODE_BLACK) {
                    
                    signByte = ChatSdk.signSaveBlackList(encryStr, encryStrGateWay, feeSetting);
                } else {
                    
                    signByte = ChatSdk.signSaveWhiteList(encryStr, encryStrGateWay, feeSetting);
                }

                
                
                String jsonSignResult = new String(signByte);
                LogUtil.i("jsonSignResult="+jsonSignResult);
                if (TextUtils.isEmpty(jsonSignResult)) {
                    emitter.onNext(new EvmosSignResult());
                } else {
                    try {
                        EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
                        emitter.onNext(result);
                    } catch (Exception e){
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    private int mUseTime;
    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime +=times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time->{
            mRpcApi.getEvmosHxResult(transferResult.data.tx_hash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EvmosHxResultBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(EvmosHxResultBean evmosHxResultBean) {
                            if (evmosHxResultBean.isTxSuccess()) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = true;
                                postTxReslult(result);
                            } else if(evmosHxResultBean.isTxFail(mUseTime)) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = getApplication().getString(R.string.transfer_fail);
                                postTxReslult(result);
                            } else {
                                checkTxResult(transferResult, true, 2);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mUseTime > 7) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = "";
                                postTxReslult(result);
                            } else {
                                checkTxResult(transferResult, true, 2);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        });
    }



    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        mTransfResultLD.setValue(resultBean);
    }

    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
