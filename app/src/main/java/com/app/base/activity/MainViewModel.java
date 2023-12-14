package com.app.base.activity;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.ChainBridgeServiceStatusBean;
import com.wallet.ctc.model.blockchain.ChatSdkExBean;
import com.wallet.ctc.model.blockchain.EvmosBlockInfoBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.LogUtil;

import org.matrix.android.sdk.internal.database.model.ChainEntity;
import org.matrix.android.sdk.internal.session.remark.Remark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.pojo.FriendsRemarkBean;
import common.app.utils.AppWidgetUtils;
import common.app.utils.NetWorkUtils;
import common.app.utils.SpUtil;
import im.vector.app.provide.ChatStatusProvide;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainViewModel extends BaseViewModel {
    private static final String TAG = "MainViewModel";
    private final RpcApi mRpcApi;
    public MutableLiveData<String> nowPhoneLD;

    private final CompositeDisposable mDisposable;

    private List<SettingNodeEntity> mData = new ArrayList<>();
    private ExecutorService mExService;
    private Map<String, Integer> mCheckingUrlMap = new ConcurrentHashMap<>();

    
    public MainViewModel(@NonNull Application application) {
        super(application);
        nowPhoneLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mData = DBManager.getInstance(application).getAllTypeNode(WalletUtil.MCC_COIN);
    }

    public void getChatInfo(String userId) {
        
        String address = AllUtils.getAddressByUid(userId);
        if (TextUtils.isEmpty(address)) {
            return;
        }
        String nowPhone = SpUtil.getNowPhone(address);
        if (!TextUtils.isEmpty(nowPhone)) {
            nowPhoneLD.setValue(nowPhone);
        }

        mRpcApi.getEvmosChatInfo(address).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosChatInfoBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onNext(EvmosChatInfoBean chatInfoBean) {
                if (null != chatInfoBean && chatInfoBean.isSuccess() && null != chatInfoBean.data) {
                    List<String> myPhonelist = chatInfoBean.data.mobile;
                    SpUtil.saveMyPhoneList(address, myPhonelist);
                    String nowPhone = SpUtil.getNowPhone(address);
                    if (null != myPhonelist && myPhonelist.size() > 0) {
                        if (TextUtils.isEmpty(nowPhone)) {
                            nowPhone = myPhonelist.get(0);
                        } else if (!TextUtils.isEmpty(nowPhone) && !myPhonelist.contains(nowPhone)) {
                            nowPhone = myPhonelist.get(0);
                        }
                        SpUtil.saveNowPhone(address, nowPhone);
                    } else {
                        nowPhone = "";
                        SpUtil.saveNowPhone(address, "");
                    }
                    nowPhoneLD.setValue(nowPhone);
                } else {
                    String erroInfo = chatInfoBean == null ? "getChatInfo error" : chatInfoBean.getInfo();
                    LogUtil.e(erroInfo);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });

    }

    
    protected void getBlockHeight() {
        Disposable disposable = mRpcApi.getBlockHeight().subscribeWith(new NextSubscriber<EvmosBlockInfoBean>() {
            @Override
            public void dealData(EvmosBlockInfoBean value) {
                if (null != value) AppWidgetUtils.gatewayBlock.postValue(value.getBlockHeight());
            }

            @Override
            protected void dealError(Throwable e) {
                
            }
        });
        mDisposable.add(disposable);
    }

    
    protected void getPing() {
        
        if (null == mExService) mExService = Executors.newFixedThreadPool(1);

        String url = SpUtil.getDefNode(WalletUtil.MCC_COIN);
        if (TextUtils.isEmpty(url) || mCheckingUrlMap.containsKey(url)) {
            LogUtil.w(url + " is checking return.");
            return;
        }
        mCheckingUrlMap.put(url, 1);
        mExService.submit(new CheckUrlTimeTask(url));
    }

    private class CheckUrlTimeTask implements Runnable {
        private final String url;

        public CheckUrlTimeTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                long time = NetWorkUtils.pingIpAddress2(url);
                
                mCheckingUrlMap.clear();
                AppWidgetUtils.gatewayPing.postValue(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    
    public void updateChainBridgeRpcNode() {
       Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                ChainBridgeServiceStatusBean serviceStatusBean = ChatSdk.serviceStatus();
                if (serviceStatusBean != null && serviceStatusBean.isRunning()) {
                    ChatSdk.setChainBridgeClientAddr(getApplication(), false);
                } else {
                    Log.i(TAG, "chain bridge is no running");
                }
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    Log.i(TAG, "update chan bridge success");
                }, throwable -> {
                    throwable.printStackTrace();
                });
       mDisposable.add(disposable);
    }

    
    private long mLastChainBridgeStartTaskTime = 0L;
    AtomicBoolean showErrorAtomic = new AtomicBoolean();
    public void startChainBridgeTask(boolean showError) {
        
        if (System.currentTimeMillis() - mLastChainBridgeStartTaskTime < 10000){
            Log.i(TAG, "start chain bridge task no timeout");
            if(showError && !showErrorAtomic.get()){
                showErrorAtomic.set(showError);
            }
            return;
        }
        showErrorAtomic.set(showError);
        mLastChainBridgeStartTaskTime = System.currentTimeMillis();
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ChatSdkExBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChatSdkExBean> emitter) throws Exception {
                ChainBridgeServiceStatusBean serviceStatusBean = ChatSdk.serviceStatus();
                ChatSdkExBean result = null;
                if (serviceStatusBean == null || !serviceStatusBean.isRunning()) {
                    ChatSdk.stopSync();
                    result = ChatSdk.startChainBridgeTask(getApplication());
                } else {
                    
                    result = new ChatSdkExBean();
                    result.setStatus(1);
                }

                if (null == result){
                    result = new ChatSdkExBean();
                    result.setInfo("start fail no get data.");
                }
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(exResult->{
              if (exResult != null && exResult.isSuccess()){
                  Log.i(TAG, "start chain bridge task success.");
                  if(showErrorAtomic.get()){
                      ToastUtil.showToast(getApplication().getString(R.string.chain_b_service_start_success));
                  }
              } else {
                  String errorInfo = exResult != null ? exResult.getInfo() : "start chain bridge task fail";
                  Log.e(TAG, showErrorAtomic.get()+"start chain bridge task fail:"+errorInfo);
                  if(showErrorAtomic.get()){
                      ToastUtil.showToast(getApplication().getString(R.string.chain_b_service_start_fail)+errorInfo);
                  }
              }
          }, throwable -> {
              ChatSdk.stopSync();
              throwable.printStackTrace();
              String errorInfo = throwable+":"+throwable.getMessage();
              Log.e(TAG, "start chain bridge task fail: "+throwable.getMessage());
              if(showErrorAtomic.get()) {
                  ToastUtil.showToast(getApplication().getString(R.string.chain_b_service_start_fail)+errorInfo);
              }
          });
        mDisposable.add(disposable);

    }



    
    public void checkMyPhoneList(String walletPwd) {
        String userId = ChatStatusProvide.getUserId(getApplication());
        
        String address = AllUtils.getAddressByUid(userId);
        if (TextUtils.isEmpty(address)) {
            return;
        }
        mRpcApi.getEvmosChatFeeSetting(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosChatFeeBean>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onNext(EvmosChatFeeBean chatInfoBean) {
                if (null != chatInfoBean && chatInfoBean.isSuccess() && null != chatInfoBean.data) {
                    List<String> myPhonelist = chatInfoBean.data.mobile;
                    SpUtil.saveMyPhoneList(address, myPhonelist);

                    String nowPhone = SpUtil.getNowPhone(address);
                    if (TextUtils.isEmpty(nowPhone) && null != myPhonelist && myPhonelist.size() > 0) {
                        SpUtil.saveNowPhone(address, myPhonelist.get(0));
                    } else if (null == myPhonelist || myPhonelist.size() == 0 || !myPhonelist.contains(nowPhone)) {
                        if (null != myPhonelist && myPhonelist.size() > 0) {
                            SpUtil.saveNowPhone(address, myPhonelist.get(0));
                        } else {
                            SpUtil.saveNowPhone(address, "");
                        }
                    }


                    if (!TextUtils.isEmpty(walletPwd)) {
                        
                        praseBlackWhiteDatas(address, chatInfoBean, walletPwd);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    
    private void praseBlackWhiteDatas(String address, EvmosChatFeeBean onlineData, String walletPwd) {
        
        ChainEntity dbData = getBlackWhiteDbBean();
        boolean needPwd = false;
        if (onlineData != null && onlineData.isHasBlackOrWhiteDatas()) {
            
            String onlineHash = onlineData.getBlackWhiteHash();
            if (null == dbData || TextUtils.isEmpty(dbData.getJson()) || !onlineHash.equals(dbData.getMd5())) {
                
                needPwd = true;
            }
        } else {
            
            deleteLocalBlackWhiteList();
            dbData = null;
        }
        if (onlineData != null && onlineData.isHasAddressBookData()) {
            needPwd = true;
        } else {
            
        }
        if (needPwd) {
            WalletEntity wallet = WalletDBUtil.getInstent(getApplication()).getWalletInfoByAddress(address, WalletUtil.MCC_COIN);
            refreshDatas(address, onlineData, wallet, walletPwd);
            return;
        }
    }

    
    public ChainEntity getBlackWhiteDbBean() {
        return ChatStatusProvide.getChainInfoCache(getApplication());
    }

    
    public void deleteLocalBlackWhiteList() {
        ChatStatusProvide.deleteAllChainInfo(getApplication());
    }


    
    public void refreshDatas(String address, EvmosChatFeeBean onlineData, WalletEntity wallet, String pwd) {
        if (onlineData != null) {
            
            if (onlineData.isHasBlackOrWhiteDatas()) {
                
                String onlineHash = onlineData.getBlackWhiteHash();
                String whiteListEncryStr = onlineData.getWhiteListEncryStr();
                String blackListEncryStr = onlineData.getBlackListEncryStr();
                List<String> whiteList = EvmosChatFeeBean.decryList(wallet, pwd, whiteListEncryStr);
                List<String> blackList = EvmosChatFeeBean.decryList(wallet, pwd, blackListEncryStr);


                if ((whiteList == null || whiteList.size() == 0) && (blackList == null || blackList.size() == 0)) {
                    showToast(getApplication().getString(R.string.decode_msg_error));
                } else {
                    saveBlackWhiteJsonDb(blackList , whiteList, onlineHash);
                }
            }
            if (onlineData.isHasAddressBookData()) {
                
                FriendsRemarkBean friendsRemarkBean =  EvmosChatFeeBean.decryAddresBook(wallet, pwd, onlineData.getAddressBookEncryStr());
                if (null != friendsRemarkBean) {
                    if (!friendsRemarkBean.isFriendsEmpty()) {
                        
                    }
                    if (!friendsRemarkBean.isRemarksEmpty()) {
                        List<FriendsRemarkBean.FRemark> fRemarks = friendsRemarkBean.remarks;
                        List<Remark> remarkList = new ArrayList<>();
                        for (FriendsRemarkBean.FRemark r : fRemarks) {
                            remarkList.add(new Remark(r.id, r.name, 1, "", "", "", "", 0L));
                        }
                        
                        ChatStatusProvide.insertOnlineRemarks(getApplication(), remarkList);
                    }
                }
            }

        }
    }

    
    public void saveBlackWhiteJsonDb(List<String> blackList, List<String> whiteList, String dateHash) {
        String objectJsonStr = EvmosChatFeeBean.convertBlackWhiteJsonStr(blackList, whiteList);
        ChainEntity chainEntity = new ChainEntity("1", dateHash, objectJsonStr);
        ChatStatusProvide.saveChainInfo(getApplication(), chainEntity,() -> {
            
        });
    }


    
    public void freshRootWalletInfo() {
        
        List<WalletEntity> wallets = WalletDBUtil.getInstent(getApplication()).getWallName();
        if (wallets != null && wallets.size() > 0) {
            int dstCount =0, ethCount=0, bscCount = 0;
            for (int i=0; i<wallets.size(); i++) {
                int walleType = wallets.get(i).getType();
                if (walleType == WalletUtil.MCC_COIN) {
                    dstCount++;
                } else if(walleType == WalletUtil.ETH_COIN){
                    ethCount++;
                } else if(walleType == WalletUtil.BNB_COIN) {
                    bscCount++;
                }
            }
            AppWidgetUtils.updateWalletCount(dstCount, ethCount, bscCount);
        } else {
            AppWidgetUtils.updateWalletCount(0, 0, 0);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable){
            mDisposable.clear();
        }
    }
}
