package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.api.blockchain.TrustWalletApi;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosTokenRecordsBean;
import com.wallet.ctc.model.blockchain.EvmosTransRecordsBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;
import com.wallet.ctc.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AssetsDetailViewModel extends BaseViewModel {

    private RpcApi mRpcApi;
    private TrustWalletApi mTrustWalletApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<List<TransactionRecordBean>> mRecordsLD;
    public MutableLiveData<List<FilTransRecordBean.DocsBean>> mEthRecordsLD;
    public MutableLiveData<Boolean> mRefreshStatusLD;
    public MutableLiveData<String> mBalanceLD;

    
    public AssetsDetailViewModel(@NonNull Application application) {
        super(application);
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mRecordsLD = new MutableLiveData<>();
        mRefreshStatusLD = new MutableLiveData<>();
        mBalanceLD = new MutableLiveData<>();
        mTrustWalletApi = new TrustWalletApi();
        mEthRecordsLD = new MutableLiveData<>();
    }


    
    public void getDatas(int page, String address, AssertBean assertData) {
        if (TextUtils.isEmpty(address) || null == assertData) {
            showToast("address or assert is null");
            return;
        }
        int walletType = assertData.getType();
        String contract = assertData.getContract();
        if (walletType == WalletUtil.MCC_COIN) {
            
            String coinName = assertData.getShort_name();
            if (TextUtils.isEmpty(contract)) {
                getEvmosHistory(page, address, coinName);
            } else {
                int decimal = assertData.getDecimal();
                getEvmosTokenHistory(page, address, contract, coinName, decimal);
            }
        } else {
            
            mEthRecordsLD.setValue(new ArrayList<>());
            notifyRefreshSuccess();
        }

        

    }


    @Deprecated
    private void getDatas(int page, String address, String coinName) {
        getEvmosHistory(page, address, coinName);

        
    }

    
    private void getBalance(String address, AssertBean assertData) {
        int walletType = assertData.getType();
        String contract = assertData.getContract();
        if (walletType == WalletUtil.MCC_COIN) {
            
            String coinName = assertData.getShort_name();
            if (TextUtils.isEmpty(contract)) {
                getEvmosBanlance(address, coinName);
            } else {
                
                getEthBalance(address, assertData);
            }
        } else {
            
            getEthBalance(address, assertData);
        }
    }

    
    private void getEvmosHistory(int page, String address, String coinName) {
        Disposable disposable = mRpcApi.getEvmosHistory(page, address, coinName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosTransRecordsBean>() {
                    @Override
                    public void dealData(EvmosTransRecordsBean value) {
                        if (null != value && value.isSuccess()) {
                            notifyRefreshSuccess();
                            mRecordsLD.setValue(value.getRecords());
                        } else {
                            notifyRefreshFail();
                            String errorInfo = value != null ? value.getInfo() : "get records data null!";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        notifyRefreshFail();
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void getEvmosBanlance(String address, String coinName) {
        Disposable disposable = mRpcApi.getEvmosOneBalance(address, coinName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosOneBalanceBean>() {
                    @Override
                    public void dealData(EvmosOneBalanceBean value) {
                        if (null != value && value.isSuccess()) {
                            AssertBean assertBean = WalletDBUtil.getInstent(getApplication()).getWalletAssets(WalletUtil.MCC_COIN, coinName);
                            int decimal = 18;
                            if (null != assertBean) {
                                decimal = assertBean.getDecimal();
                            }
                            String balance = value.getBalance(decimal);
                            mBalanceLD.setValue(balance);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get records data null!";
                            showToast(errorInfo);
                        }
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void getEthBalance(String address, AssertBean assertBean) {
        String contract = assertBean.getContract();
        int walletType = assertBean.getType();
        Disposable disposable = mRpcApi.getEthBanlance(address, contract, walletType)
                .map(new io.reactivex.functions.Function<FilBalanceBean, String>() {
                    @Override
                    public String apply(FilBalanceBean value) throws Exception {
                        if (null != value && !TextUtils.isEmpty(value.getResult())) {
                            String bigNum = HexUtils.hextoTen(value.getResult());
                            String balance = AllUtils.getTenDecimalValue(bigNum, assertBean.getDecimal(), 6);
                            return balance;
                        } else {
                            throw new Exception("get eth balance value is null");
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<String>() {
                    @Override
                    public void dealData(String value) {
                        mBalanceLD.setValue(value);
                    }
                });
        mDisposable.add(disposable);
    }


    
    private void getEvmosTokenHistory(int page, String address, String contract, String tokenName, int decimal) {
        Disposable disposable = mRpcApi.getEvmosTokenHistory(page, address, contract)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosTokenRecordsBean>() {
                    @Override
                    public void dealData(EvmosTokenRecordsBean data) {
                        
                        if (data != null && data.isSuccess()) {
                            notifyRefreshSuccess();
                            List<EvmosTokenRecordsBean.Data> datas = data.convertData(address, tokenName, decimal);
                            List<FilTransRecordBean.DocsBean> lists = new ArrayList<>();
                            if (null != datas && datas.size() > 0) {
                                for (EvmosTokenRecordsBean.Data record : datas) {
                                    lists.add(FilTransRecordBean.toDoc(record));
                                }
                            }
                            mEthRecordsLD.setValue(lists);
                        } else {
                            String errorInfo = data != null ? data.getInfo() : "get data null";
                            ToastUtil.showToast(errorInfo);
                            notifyRefreshFail();
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        notifyRefreshFail();
                    }
                });
        mDisposable.add(disposable);
    }


    
    private void getEthHistory(int walletType, String address, String contract) {
        String coin = null;
        String data = null;
        if (TextUtils.isEmpty(address)) {
            showToast("address is null");
            return;
        }
        WalletDBUtil walletDBUtil = WalletDBUtil.getInstent(getApplication());
        WalletEntity wallet = walletDBUtil.getWalletInfoByAddress(address, walletType);
        if (null == wallet) {
            showToast("no found wallet info");
            return;
        }
        if (walletType == WalletUtil.FIL_COIN) {
            coin = "filecoin";
            data = wallet.getAllAddress();
            getTransRecords(coin, data);
        } else if (walletType == WalletUtil.DOGE_COIN) {
            coin = "doge";
            data = new String(wallet.getmPublicKey());
            getXpubTransRecords(coin, data);
        } else if (walletType == WalletUtil.DOT_COIN) {
            coin = "polkadot";
            data = wallet.getAllAddress();
            getTransRecords(coin, data);
        } else if (walletType == WalletUtil.LTC_COIN) {
            coin = "litecoin";
            data = new String(wallet.getmPublicKey());
            getXpubTransRecords(coin, data);
        } else if (walletType == WalletUtil.BCH_COIN) {
            coin = "bitcoincash";
            data = new String(wallet.getmPublicKey());
            getXpubTransRecords(coin, data);
        } else if (walletType == WalletUtil.ZEC_COIN) {
            coin = "zcash";
            data = new String(wallet.getmPublicKey());
            getXpubTransRecords(coin, data);
        } else if (walletType == WalletUtil.ETC_COIN) {
            coin = "classic";
            data = wallet.getAllAddress();
            getTransRecords(coin, data);
        } else if (walletType == WalletUtil.XRP_COIN) {
            coin = "ripple";
            data = wallet.getAllAddress();
            getTransRecords(coin, data);
        } else if (walletType == WalletUtil.BTC_COIN) {
            coin = "bitcoin";
            data = new String(wallet.getmPublicKey());
            getXpubTransRecords(coin, data);
        } else if (walletType == WalletUtil.TRX_COIN) {
            coin = "tron";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.ETH_COIN) {
            coin = "ethereum";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.BNB_COIN) {
            coin = "smartchain";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.HT_COIN) {
            coin = "hecochain";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.DMF_BA_COIN) {
            coin = "dmf";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.DMF_COIN) {
            coin = "dmf";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.ETF_COIN) {
            coin = "etf";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.SOL_COIN) {
            coin = "solana";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        } else if (walletType == WalletUtil.MATIC_COIN) {
            coin = "polygon";
            data = wallet.getAllAddress();
            if (TextUtils.isEmpty(contract)) {
                getTransRecords(coin, data);
            } else {
                getTransRecordsToken(coin, data, contract);
            }
        }
    }

    
    private void getTransRecords(String coin, String data) {
        Disposable disposable = mTrustWalletApi.getTransRecords(coin, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<FilTransRecordBean>() {
                    @Override
                    public void dealData(FilTransRecordBean value) {
                        if (null != value && value.isStatus()) {
                            notifyRefreshSuccess();
                            mEthRecordsLD.setValue(value.getDocs());
                        } else {
                            notifyRefreshFail();
                            String errorInfo = (value != null && null != value.getError()) ? value.getError().getMessage() : "get records is null";
                            ToastUtil.showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        notifyRefreshFail();
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void getTransRecordsToken(String coin, String data, String contract) {
        Disposable disposable = mTrustWalletApi.getTransRecordsToken(coin, data, contract)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<FilTransRecordBean>() {
                    @Override
                    public void dealData(FilTransRecordBean value) {
                        if (null != value && value.isStatus()) {
                            notifyRefreshSuccess();
                            mEthRecordsLD.setValue(value.getDocs());
                        } else {
                            notifyRefreshFail();
                            String errorInfo = (value != null && null != value.getError()) ? value.getError().getMessage() : "get records is null";
                            ToastUtil.showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        notifyRefreshFail();
                    }
                });
        mDisposable.add(disposable);

    }

    
    private void getXpubTransRecords(String coin, String data) {
        Disposable disposable = mTrustWalletApi.getXpubTransRecords(coin, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<FilTransRecordBean>() {
                    @Override
                    public void dealData(FilTransRecordBean value) {
                        if (null != value && value.isStatus()) {
                            notifyRefreshSuccess();
                            mEthRecordsLD.setValue(value.getDocs());
                        } else {
                            notifyRefreshFail();
                            String errorInfo = (value != null && null != value.getError()) ? value.getError().getMessage() : "get records is null";
                            ToastUtil.showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        notifyRefreshFail();
                    }
                });
        mDisposable.add(disposable);
    }


    
    private void notifyRefreshSuccess() {
        mRefreshStatusLD.setValue(true);
    }

    
    private void notifyRefreshFail() {
        mRefreshStatusLD.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
