package com.wallet.ctc.ui.pay;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.lang.ref.WeakReference;

import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.MyProgressDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TranslationControlDialog {

    private WeakReference<Context> mContextRf;
    private final CompositeDisposable mDisposable;
    private WalletEntity mSelecteWallet;
    private EvmosSeqGasBean mEvmosSeqGasBean;
    private TransferListener mTransferListener;
    private RpcApi mRpcApi;
    private MyProgressDialog mLoadingDialog;
    private boolean showLoading = true;

    public TranslationControlDialog(Context context, boolean showLoading) {
        this.mContextRf = new WeakReference<>(context);
        this.mDisposable = new CompositeDisposable();
        this.mRpcApi = new RpcApi();
        this.showLoading = showLoading;
    }


    private Context getContext() {
        return mContextRf.get();
    }

    public interface TransferListener {
        
        byte[] signData();

        void onFail(String errorInfo);

        
        void onSubmitSuccess();

        
        void onTransSuccess();
    }

    
    public void doTranslate(WalletEntity wallet, String tradeDesc, String amount, String toAddrss,
                            Observable<EvmosSeqGasBean> gasObservable, TransferListener signDataListener) {
        this.mSelecteWallet = wallet;
        this.mTransferListener = signDataListener;
        getAndShowGas(gasObservable, tradeDesc, amount, toAddrss);
    }

    
    public void doTranlste(WalletEntity wallet, EvmosSeqGasBean gasBean, TransferListener signDataListener) {
        this.mSelecteWallet = wallet;
        this.mEvmosSeqGasBean = gasBean;
        this.mTransferListener = signDataListener;
        showPwdDialog();
    }


    public String getDealmsg(String msg) {
        if (getContext() == null) {
            return msg;
        }
        if (TextUtils.isEmpty(msg)) {
            return "";
        }
        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = getContext().getResources().getIdentifier(realMsgName, "string", getContext().getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = getContext().getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                realMsg = getContext().getString(common.app.R.string.balance_no_enough);
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = getContext().getResources().getIdentifier("error_insufficient_level_to_transfer", "string", getContext().getPackageName());
                realMsg = getContext().getString(strid);
            }
        }
        if (!TextUtils.isEmpty(realMsg)) {

            return realMsg;
        }
        return msg;
    }

    
    private void getAndShowGas(Observable<EvmosSeqGasBean> gasObservable, String tradeDesc, String amount, String toAddrss) {
        if (isContextDestroyed()) {
            return;
        }
        if (null == gasObservable) {
            notifyFailResult(getContext().getString(R.string.wallet_get_gas_fail));
            return;
        }
        if (null == mSelecteWallet) {
            notifyFailResult(getContext().getString(R.string.no_found_wallet_info));
            return;
        }
        showLoadingDialog();
        Disposable disposable = gasObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        dismissLoadingDialog();
                        if (null != value && value.isSuccess()) {
                            showGasAlertDialog(value, tradeDesc, amount, toAddrss);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get gas result null";
                            notifyFailResult(getDealmsg(errorInfo));
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        dismissLoadingDialog();
                        e.printStackTrace();
                        notifyFailResult(e + ":" + getContext().getString(R.string.wallet_get_gas_fail) + e.getMessage());
                    }
                });
        mDisposable.add(disposable);
    }


    
    public void showGasAlertDialog(EvmosSeqGasBean evmosSeqGasBean, String tradeDesc, String amount, String toAddrss) {
        if (isContextDestroyed()) {
            return;
        }
        this.mEvmosSeqGasBean = evmosSeqGasBean;
        TransConfirmDialogBuilder.builder(getContext(), mSelecteWallet)
                .amount(amount)
                
                .fromAddress(mSelecteWallet.getAllAddress())
                
                .toAddress(toAddrss)
                .type(mSelecteWallet.getType())
                .orderDesc(tradeDesc)
                
                .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                
                .goTransferListener(pwd -> {
                    
                    submit(pwd);
                }).show();
    }


    
    private void showPwdDialog() {
        if (isContextDestroyed()) {
            return;
        }
        InputPwdDialog mPwdDialog = new InputPwdDialog(getContext(), getContext().getString(R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                mPwdDialog.dismiss();
                
                submit(pwd);
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }


    
    private void submit(String pwd) {
        Disposable disposable = sign(pwd).subscribe(evmosSignResult -> {
            if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                submitTransfer(evmosSignResult);
            } else {
                String errorInfo = evmosSignResult != null ? evmosSignResult.getInfo() : "sign data is error";
                notifyFailResult(getDealmsg(errorInfo));
            }
        }, throwable -> {
            throwable.printStackTrace();
            notifyFailResult(throwable + ":" + throwable.getMessage());
        });
        mDisposable.add(disposable);

    }

    
    private Observable<EvmosSignResult> sign(String pwd) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                        
                        ChatSdk.resetWalletGasInfo(mEvmosSeqGasBean, mSelecteWallet, pwd, "");
                        EvmosSignResult evmosSignResult = null;
                        byte[] signData = null;
                        if (null != mTransferListener) {
                            signData = mTransferListener.signData();
                        }
                        if (signData == null) {
                            throw new Exception("transfer sign data is null");
                        }
                        evmosSignResult = ChatSdk.convertSignData(signData);
                        if (evmosSignResult == null || !evmosSignResult.isSuccess()) {
                            String errorInfo = evmosSignResult != null ? evmosSignResult.getInfo() : "sign result is null";
                            throw new Exception(errorInfo);
                        }
                        emitter.onNext(evmosSignResult);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    
    private void submitTransfer(EvmosSignResult signData) {
        if (null == signData || !signData.isSuccess() || TextUtils.isEmpty(signData.Data)) {
            return;
        }
        showLoadingDialog();
        Disposable disposable = mRpcApi.submitEvmosTransfer(signData.Data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (null != data && data.isSuccess()) {
                        notifySumitSuccess();
                        checkTxResult(data);
                    } else {
                        dismissLoadingDialog();
                        String errorInfo = data != null ? data.getInfo() : "set chat fee fail";
                        notifyFailResult(getDealmsg(errorInfo));
                    }
                }, throwable -> {
                    dismissLoadingDialog();
                    notifyFailResult(throwable + ":" + throwable.getMessage());
                });
        mDisposable.add(disposable);
    }


    
    private void checkTxResult(EvmosTransferResultBean transferResult) {
        showLoadingDialog();
        mRpcApi.timerCheckTxResult(transferResult, mDisposable, new Observer<EvmosPledgeResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(EvmosPledgeResultBean evmosPledgeResultBean) {
                dismissLoadingDialog();
                if (evmosPledgeResultBean.success) {
                    notifyTransferSuccess();
                } else {
                    notifyFailResult(getDealmsg(evmosPledgeResultBean.info));
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dismissLoadingDialog();
                notifyFailResult(e + ":" + e.getMessage());
            }

            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }
        });
    }

    public void showLoadingDialog() {
        if (!showLoading) {
            return;
        }
        if (isContextDestroyed()) {
            return;
        }
        if (null == mLoadingDialog) {
            mLoadingDialog = new MyProgressDialog(getContext(), "Loading");
        } else {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (null != mLoadingDialog) {
            mLoadingDialog.dismiss();
        }
    }

    
    private void notifyFailResult(String errorInfo) {
        if (isContextDestroyed()) {
            mTransferListener = null;
            return;
        }
        ToastUtil.showToast(errorInfo);
        if (null != mTransferListener) {
            mTransferListener.onFail(errorInfo);
        }
    }

    
    private void notifySumitSuccess() {
        if (isContextDestroyed()) {
            mTransferListener = null;
            return;
        }
        if (null != mTransferListener) {
            mTransferListener.onSubmitSuccess();
        }
    }

    
    private void notifyTransferSuccess() {
        if (isContextDestroyed()) {
            mTransferListener = null;
            return;
        }
        ToastUtil.showToast(getContext().getString(R.string.operate_success_and_wait));
        if (null != mTransferListener) {
            mTransferListener.onTransSuccess();
        }
    }

    
    private boolean isContextDestroyed() {
        Context context = getContext();
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return activity.isDestroyed() || activity.isFinishing();
        }
        return false;
    }

    
    public void destroy() {
        if (mDisposable != null) {
            mDisposable.clear();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        mTransferListener = null;
    }
}
