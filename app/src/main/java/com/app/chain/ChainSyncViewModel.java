package com.app.chain;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosBWUsersBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.EvmosUserBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.LogUtil;

import org.matrix.android.sdk.internal.database.model.ChainEntity;
import org.matrix.android.sdk.internal.session.remark.Remark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.pojo.BlackWhiteBean;
import common.app.pojo.FriendsRemarkBean;
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


public class ChainSyncViewModel extends BaseViewModel {

    private static final String TAG = "ChainSyncVM";

    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<EvmosPledgeResultBean> mTransfResultLD;
    public MutableLiveData<EvmosChatFeeBean> mSettingLD;

    public MutableLiveData<Boolean> mVerfyPwdLD; 
    public MutableLiveData<List<EvmosUserBean>> mWhiteListLD;
    public MutableLiveData<List<EvmosUserBean>> mWhiteSearchListLD;
    public MutableLiveData<List<EvmosUserBean>> mBlackListLD;
    public MutableLiveData<List<EvmosUserBean>> mBlackSearchListLD;
    public static List<EvmosUserBean> mUnUpWhiteList = new ArrayList<>();
    public static List<EvmosUserBean> mUnUpBlackList = new ArrayList<>();

    public MutableLiveData<List<EvmosUserBean>> mWhiteToRemoveLD;
    public MutableLiveData<List<EvmosUserBean>> mBlackToRemoveLD;

    public MutableLiveData<List<Remark>> mRemarkListLD;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;

    
    public List<Remark> remarks = new ArrayList<>();
    private Gson mGson;


    
    public ChainSyncViewModel(@NonNull Application application) {
        super(application);
        mGson = new Gson();
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mTransfResultLD = new MutableLiveData<>();
        mSettingLD = new MutableLiveData<>();
        mWhiteListLD = new MutableLiveData<>();
        mWhiteSearchListLD = new MutableLiveData<>();
        mBlackListLD = new MutableLiveData<>();
        mBlackSearchListLD = new MutableLiveData<>();
        mWhiteToRemoveLD = new MutableLiveData<>();
        mBlackToRemoveLD = new MutableLiveData<>();
        mVerfyPwdLD = new MutableLiveData<>();
        mRemarkListLD = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
    }


    
    public void getSetting(String address) {
        mRpcApi.getEvmosChatFeeSetting(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosChatFeeBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onNext(EvmosChatFeeBean evmosChatFeeBean) {
                if (null != evmosChatFeeBean && evmosChatFeeBean.isSuccess() && null != evmosChatFeeBean.data) {
                    mSettingLD.setValue(evmosChatFeeBean);
                    praseBlackWhiteDatas(evmosChatFeeBean);
                } else {
                    String errorInfo = evmosChatFeeBean != null ? evmosChatFeeBean.getInfo() : "get fee setting fail";
                    showToast(errorInfo);
                }

                
                getRemarks();
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

        
        getLocalBlackWhiteDatas();
    }

    
    public void praseBlackWhiteDatas(EvmosChatFeeBean onlineData) {
        
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

        if (needPwd) {
            mVerfyPwdLD.setValue(true);
            return;
        }
        
        getLocalBlackWhiteDatas();
    }


    
    public void getLocalBlackWhiteDatas() {
        ChainEntity dbData = getBlackWhiteDbBean();

        List<String> blackList = new ArrayList<>();
        List<String> whiteList = new ArrayList<>();
        if (dbData != null) {
            Log.i(TAG, "："+dbData.toStr());
            BlackWhiteBean localData = EvmosChatFeeBean.blackWhiteFromJson(dbData.getJson());
            if (null != localData) {
                if (localData.blacks != null) {
                    blackList.addAll(localData.blacks);
                }
                if (localData.whites != null) {
                    whiteList.addAll(localData.whites);
                }
            }
        } else {
            Log.w(TAG, "dbData is null");
        }

        mRpcApi.searchBlackWhitesUserDatas(blackList, whiteList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosBWUsersBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosBWUsersBean evmosBWUsersBean) {
                        List<EvmosUserBean> blackList = new ArrayList<>();
                        List<EvmosUserBean> whiteList = new ArrayList<>();
                        if (null != evmosBWUsersBean && null != evmosBWUsersBean.blackList) {
                            blackList.addAll(evmosBWUsersBean.blackList);
                        }
                        if (null != evmosBWUsersBean && null != evmosBWUsersBean.whiteList) {
                            whiteList.addAll(evmosBWUsersBean.whiteList);
                        }
                        convertListDatas(blackList, ChainSyncActivity.MODE_BLACK);

                        convertListDatas(whiteList, ChainSyncActivity.MODE_WHITE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }




    
    public void removeDelListUser(EvmosUserBean userBean, int mode) {
        if (null == userBean) {
            return;
        }


        if (mode == ChainSyncActivity.MODE_WHITE) {

            List<EvmosUserBean> delList = mWhiteToRemoveLD.getValue();
            if (null != delList) {
                delList.remove(userBean);
            }
            mWhiteToRemoveLD.setValue(delList);

            List<EvmosUserBean> userBeanList = mWhiteListLD.getValue();
            if (null == userBeanList) {
                userBeanList = new ArrayList<>();
            }
            userBeanList.add(userBean);
            mWhiteListLD.setValue(userBeanList);
        } else if (mode == ChainSyncActivity.MODE_BLACK) {
            List<EvmosUserBean> delList = mBlackToRemoveLD.getValue();
            if (null != delList) {
                delList.remove(userBean);
            }
            mBlackToRemoveLD.setValue(delList);

            List<EvmosUserBean> userBeanList = mBlackListLD.getValue();
            if (null == userBeanList) {
                userBeanList = new ArrayList<>();
            }
            userBeanList.add(userBean);
            mBlackListLD.setValue(userBeanList);
        }
    }

    
    public void addLocalUser(String address, String allUserId, String mobile, int mode) {
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(allUserId)) {
            Log.w("WhiteBlackList", "adreess or allUserid is null");
            return;
        }
        boolean hasAdded = false;
        if (mode == ChainSyncActivity.MODE_WHITE) {
            
            if (mUnUpWhiteList.size() > 0) {
                for (EvmosUserBean userInfo : mUnUpWhiteList) {
                    if (address.equalsIgnoreCase(userInfo.address)) {
                        hasAdded = true;
                        break;
                    }
                }
            }

            
            if (mUnUpBlackList.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mUnUpBlackList.removeIf(item -> address.equalsIgnoreCase(item.address));
                } else {
                    Iterator<EvmosUserBean> iterator = mUnUpBlackList.iterator();
                    while (iterator.hasNext()) {
                        if (address.equalsIgnoreCase(iterator.next().address)) {
                            iterator.remove();
                        }
                    }
                }
            }

            if (!hasAdded) {
                mUnUpWhiteList.add(new EvmosUserBean(address, allUserId, mobile, false));
            }
        } else if (mode == ChainSyncActivity.MODE_BLACK) {
            
            if (mUnUpBlackList.size() > 0) {
                for (EvmosUserBean userInfo : mUnUpBlackList) {
                    if (address.equalsIgnoreCase(userInfo.address)) {
                        hasAdded = true;
                        break;
                    }
                }
            }

            
            if (mUnUpWhiteList.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mUnUpWhiteList.removeIf(item -> address.equalsIgnoreCase(item.address));
                } else {
                    Iterator<EvmosUserBean> iterator = mUnUpWhiteList.iterator();
                    while (iterator.hasNext()) {
                        if (address.equalsIgnoreCase(iterator.next().address)) {
                            iterator.remove();
                        }
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
        if (mode == ChainSyncActivity.MODE_WHITE && mUnUpWhiteList.size() > 0) {
            
            Iterator<EvmosUserBean> iterator = mUnUpWhiteList.iterator();
            while (iterator.hasNext()) {
                EvmosUserBean user = iterator.next();
                if (delAddr.equalsIgnoreCase(user.address)) {
                    iterator.remove();
                }
            }
        } else if (mode == ChainSyncActivity.MODE_BLACK && mUnUpBlackList.size() > 0) {
            
            Iterator<EvmosUserBean> iterator = mUnUpBlackList.iterator();
            while (iterator.hasNext()) {
                EvmosUserBean user = iterator.next();
                if (delAddr.equalsIgnoreCase(user.address)) {
                    iterator.remove();
                }
            }
        }
        List<EvmosUserBean> toRemoveList = new ArrayList<>();
        if (mode == ChainSyncActivity.MODE_WHITE) {
            
            List<EvmosUserBean> usersList = mWhiteListLD.getValue();
            
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
            mWhiteListLD.setValue(usersList);

            
            if (toRemoveList.size() > 0) {
                List<EvmosUserBean> nowRemoveList = mWhiteToRemoveLD.getValue();
                if (null == nowRemoveList) {
                    nowRemoveList = new ArrayList<>();
                }
                nowRemoveList.addAll(toRemoveList);
                mWhiteToRemoveLD.setValue(nowRemoveList);
            }
        } else if (mode == ChainSyncActivity.MODE_BLACK) {
            
            List<EvmosUserBean> usersList = mBlackListLD.getValue();
            
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
            mBlackListLD.setValue(usersList);

            
            if (toRemoveList.size() > 0) {
                List<EvmosUserBean> nowRemoveList = mBlackToRemoveLD.getValue();
                if (null == nowRemoveList) {
                    nowRemoveList = new ArrayList<>();
                }
                nowRemoveList.addAll(toRemoveList);
                mBlackToRemoveLD.setValue(nowRemoveList);
            }
        }
    }


    
    private void convertListDatas(List<EvmosUserBean> users, int mode) {
        if (mode == ChainSyncActivity.MODE_WHITE) {
            
            
            if (users != null && users.size() > 0 && mUnUpWhiteList.size() > 0) {
                Iterator<EvmosUserBean> iterator = mUnUpWhiteList.iterator();
                while (iterator.hasNext()) {
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
        } else if (mode == ChainSyncActivity.MODE_BLACK) {
            
            
            if (users != null && users.size() > 0 && mUnUpBlackList.size() > 0) {
                Iterator<EvmosUserBean> iterator = mUnUpBlackList.iterator();
                while (iterator.hasNext()) {
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
        getUserList(users, mode);
    }

    
    private void getUserList(List<EvmosUserBean> userLists, int mode) {
        if (null == userLists || userLists.size() == 0) {
            if (mode == ChainSyncActivity.MODE_WHITE) {
                mWhiteListLD.setValue(new ArrayList<>());
            } else if (mode == ChainSyncActivity.MODE_BLACK) {
                mBlackListLD.setValue(new ArrayList<>());
            }
            return;
        }
        Context context = getApplication();

        List<String> addressList = new ArrayList<>();
        for (EvmosUserBean userBean : userLists) {
            if (!TextUtils.isEmpty(userBean.comm_address)) {
                addressList.add(userBean.comm_address);
            }
        }
        
        ChatStatusProvide.searchUserInfoList(ChainSyncViewModel.this, context, addressList, new Function1<List<UserInfo>, Unit>() {
            @Override
            public Unit invoke(List<UserInfo> userInfos) {
                if (null != userInfos && userInfos.size() > 0) {
                    for (int i = 0; i < userLists.size(); i++) {
                        EvmosUserBean userBean = userLists.get(i);
                        for (int j = 0; j < userInfos.size(); j++) {
                            UserInfo userInfo = userInfos.get(j);
                            if (!TextUtils.isEmpty(userBean.comm_address) && userBean.comm_address.equalsIgnoreCase(userInfo.getAddress())) {
                                userBean.logo = userInfo.getAvatarUrl();
                                userBean.nickName = getRemarkName(userBean.comm_address, userInfo.getDisplayName());
                                if (userBean.isEffect) {
                                    userBean.update_time = getupdateTime();
                                }
                                break;
                            }
                        }
                    }
                }
                if (mode == ChainSyncActivity.MODE_WHITE) {
                    mWhiteListLD.setValue(userLists);
                } else if (mode == ChainSyncActivity.MODE_BLACK) {
                    mBlackListLD.setValue(userLists);
                }
                return null;
            }
        });

    }


    private Map<String,Remark> remarkMap;
    
    private String getRemarkName(String uid, String defaultName) {
        if (null != remarks && remarks.size() > 0) {
            if (remarkMap == null) {
                remarkMap = new HashMap<>();
                for (Remark remark : remarks) {
                    remarkMap.put(remark.getUserId(), remark);
                }
            }
            String remarkName = remarkMap.get(uid).getRemark();
            if (!TextUtils.isEmpty(remarkName)) {
                return remarkName;
            }
        }

        return defaultName;
    }

    
    private long getupdateTime() {
        EvmosChatFeeBean setting = mSettingLD.getValue();
        if (null != setting && null != setting.data) {
            return setting.data.update_time;
        }
        return 0;
    }

    
    public void searchBWList(String keyword, int mode) {
        if (TextUtils.isEmpty(keyword)) {
            if (mode == ChainSyncActivity.MODE_WHITE) {
                mWhiteListLD.setValue(mWhiteListLD.getValue());
            } else {
                mBlackListLD.setValue(mBlackListLD.getValue());
            }
            return;
        }
        List<EvmosUserBean> list = (mode == ChainSyncActivity.MODE_WHITE) ? mWhiteListLD.getValue() : mBlackListLD.getValue();
        List<EvmosUserBean> searchReslut = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (EvmosUserBean item : list) {
                if (item.containsKey(keyword)) {
                    searchReslut.add(item);
                }
            }
        }
        if (mode == ChainSyncActivity.MODE_WHITE) {
            mWhiteSearchListLD.setValue(searchReslut);
        } else {
            mBlackSearchListLD.setValue(searchReslut);
        }
    }

    
    private List<String> getWhiteResultList() {
        return getUidStrList(mWhiteListLD.getValue());
    }

    
    private List<String> getBlackResultList() {
        return getUidStrList(mBlackListLD.getValue());
    }

    
    private List<String> getUidStrList(List<EvmosUserBean> list) {
        List<String> addressList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (EvmosUserBean userBean : list) {
                addressList.add(userBean.comm_address);
            }
        }
        return addressList;
    }

    
    private void refreshLocalDatas(List<String> blacks, List<String> whites, String bwHash, List<Remark> remarks) {
        
        saveBlackWhiteJsonDb(blacks,whites,bwHash);

        
        if (null != remarks && remarks.size() > 0) {
            for (int i=0; i<remarks.size(); i++) {
                remarks.get(i).setSync(1);
            }
        }
        ChatStatusProvide.forceUpdateRemarks(getApplication(), remarks);
    }

    
    public void showGasAlert(String fromAddr, EvmosChatFeeBean feeSetting) {
        mRpcApi.getSaveFBWInfoGas(fromAddr, "xxx", "xxxx", "xxxx",
                "xxxx", "xxxx", feeSetting).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        if (value != null && value.isSuccess()) {
                            mShowGasDialogLD.setValue(value);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get gas result is null";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        showToast(e.getMessage());
                    }
                });
    }

    
    public void submit(String fromAddr, EvmosChatFeeBean feeSetting, WalletEntity wallet, String pwd) {
        ChatStatusProvide.getServerPubKeyTask(getApplication(), this, serverPublickKey -> {
            if (TextUtils.isEmpty(serverPublickKey)) {
                showToast(getApplication().getString(R.string.get_gateway_key_fail));
                return null;
            }
            List<String> whiteList = getWhiteResultList();
            List<String> blackList = getBlackResultList();
            
            if (null != whiteList && whiteList.size() > 0 && blackList != null && blackList.size() > 0) {
                boolean hasIllege = false;
                for (String white : whiteList) {
                    for(String black : blackList) {
                        if (white.equalsIgnoreCase(black)) {
                            hasIllege = true;
                            break;
                        }
                    }
                    if (hasIllege) {
                        break;
                    }
                }
                if (hasIllege) {
                    showToast(getApplication().getString(R.string.set_black_white_error));
                    return null;
                }
            }

            submitInfo(fromAddr, blackList, whiteList, remarks, null, feeSetting, wallet, pwd, serverPublickKey);
            return null;
        });
    }


    
    public void submitInfo(String fromAddr, List<String> blackList, List<String> whiteList, List<Remark> remarks, List<String> friends,
                         EvmosChatFeeBean feeSetting, WalletEntity wallet, String pwd, String serverPubKey) {
        
        String encryPublickKey = ChatSdk.getPubEcKeyFromPri(wallet.decodePrivateKey(pwd));

        
        String encryBlackList = EvmosChatFeeBean.encryList(encryPublickKey, blackList);
        String encryWhiteList = EvmosChatFeeBean.encryList(encryPublickKey, whiteList);

        
        String encryBlackListGateWay = EvmosChatFeeBean.encryListGateWay(serverPubKey, blackList);
        String encryWhiteListGateway = EvmosChatFeeBean.encryListGateWay(serverPubKey, whiteList);

        
        FriendsRemarkBean frData = new FriendsRemarkBean();

        if (null != remarks && remarks.size() > 0) {
            List<FriendsRemarkBean.FRemark> list = new ArrayList<>();
            for (Remark remark : remarks) {
                list.add(new FriendsRemarkBean.FRemark(remark.getUserId(), remark.getRemark()));
            }
            frData.remarks = list;
        }
        if (null != friends && friends.size() > 0) {
            frData.friends = friends;
        }
        String encryAddressBook = EvmosChatFeeBean.encryAddressBook(encryPublickKey, frData);

        showLoadingDialog("");
        mRpcApi.getSaveFBWInfoGas(fromAddr, encryBlackList, encryWhiteList, encryAddressBook,
                encryBlackListGateWay, encryWhiteListGateway, feeSetting).concatMap(new Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>() {
            @Override
            public ObservableSource<EvmosSignResult> apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                    return signSetChatFee(fromAddr, encryBlackList, encryWhiteList, encryAddressBook,
                            encryBlackListGateWay, encryWhiteListGateway,
                            feeSetting, evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(),
                            evmosSeqGasBean.gas.getGasLimit(), wallet, pwd, serverPubKey);
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
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosTransferResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(EvmosTransferResultBean data) {
                if (null != data && data.isSuccess()) {
                    String hash = EvmosChatFeeBean.getBlackWhiteHash(encryBlackList, encryWhiteList);
                    refreshLocalDatas(blackList, whiteList, hash, remarks);
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



    
    private Observable<EvmosSignResult> signSetChatFee(String address, String encryBlackList, String encryWhiteList, String encryAddressBook,
                                                       String encryBlackListGateWay, String encryWhiteListGateway,
                                                       EvmosChatFeeBean feeSetting, EvmosSeqAcountBean.Data seqAccountBean,
                                                       final String gasAmount, final String gasLimit, WalletEntity wallet,
                                                       String pwd, String serverPubKey) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                
                String publickey = new String(wallet.getmPublicKey());
                String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
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
                String remark = "";
                LogUtil.i("accountNum=" + accountNum + ", accountSeq=" + accountSeq);
                ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, remark);


                byte[] signByte = {};
                signByte = ChatSdk.signSaveFBWInfoGas(encryBlackList, encryWhiteList, encryAddressBook,
                        encryBlackListGateWay, encryWhiteListGateway, feeSetting);

                
                
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


    private int mUseTime;

    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime += times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
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
                    } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = false;
                        result.info = getApplication().getString(com.wallet.ctc.R.string.transfer_fail);
                        postTxReslult(result);
                    } else {
                        checkTxResult(transferResult, true, 2);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
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


    public void getRemarks() {
        ChatStatusProvide.getRemarks(getApplication()).observe(getLifecyleOwner(), results -> {
            if (null != results && results.size() > 0) {
                for (int i=0; i<results.size(); i++) {
                    if (results.get(i).isSync() == 1) {
                        
                        results.get(i).setSyncTime(getupdateTime());
                    }
                }
            }

            searchRemarkInfo(results);
        });
    }

    
    public void searchRemarkInfo(List<Remark> remarkList) {
        if (null == remarkList || remarkList.size() ==0) {
            notifyRemarksChange(remarkList);
            return;
        }
        List<String> addressList = new ArrayList<>();
        for (Remark remark : remarkList) {
            addressList.add(AllUtils.getAddressByUid(remark.getUserId()));
        }
        mRpcApi.searchUserDatas(addressList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<List<EvmosUserBean>>() {
                    @Override
                    public void dealData(List<EvmosUserBean> value) {
                        if (null != value && value.size() > 0) {
                            for(int i=0; i<remarkList.size(); i++) {
                                for (EvmosUserBean user : value) {
                                    if (!TextUtils.isEmpty(user.mobile) && remarkList.get(i).getUserId().equals(user.comm_address)) {
                                        remarkList.get(i).setDid(user.mobile);
                                    }
                                }
                            }
                        }
                        notifyRemarksChange(remarkList);
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        e.printStackTrace();
                        notifyRemarksChange(remarkList);
                    }
                });
    }

    
    private void notifyRemarksChange(List<Remark> remarkList) {
        remarks.clear();
        if (remarkList != null) {
            remarks.addAll(remarkList);
        }
        mRemarkListLD.setValue(remarks);
    }


    
    public void saveBlackWhiteJsonDb(List<String> blackList, List<String> whiteList, String dateHash) {
        String objectJsonStr = EvmosChatFeeBean.convertBlackWhiteJsonStr(blackList, whiteList);
        ChainEntity chainEntity = new ChainEntity("1", dateHash, objectJsonStr);
        ChatStatusProvide.saveChainInfo(getApplication(), chainEntity,() -> {
            
        });
    }

    
    public BlackWhiteBean getBlackWhiteListFromDb() {
        ChainEntity chainEntity = getBlackWhiteDbBean();
        if (null != chainEntity && !TextUtils.isEmpty(chainEntity.getJson())) {
            BlackWhiteBean data = EvmosChatFeeBean.blackWhiteFromJson(chainEntity.getJson());
            return data;
        }
        return null;
    }

    
    public ChainEntity getBlackWhiteDbBean() {
        return ChatStatusProvide.getChainInfoCache(getApplication());
    }

    
    public void deleteLocalBlackWhiteList() {
        ChatStatusProvide.deleteAllChainInfo(getApplication());
    }

}
