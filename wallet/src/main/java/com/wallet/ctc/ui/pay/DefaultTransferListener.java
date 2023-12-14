package com.wallet.ctc.ui.pay;

import im.wallet.router.listener.TranslationListener;


public abstract class DefaultTransferListener implements TranslationControlDialog.TransferListener{

    private TranslationListener mListener;
    public DefaultTransferListener(TranslationListener listener) {
        this.mListener = listener;
    }

    @Override
    abstract public byte[] signData();

    @Override
    public void onFail(String errorInfo) {
        if (null != mListener) {
            mListener.onFail(errorInfo);
        }
    }

    @Override
    public void onSubmitSuccess() {
        if (null != mListener) {
            mListener.onSubmitSuccess();
        }
    }

    @Override
    public void onTransSuccess() {
        if (null != mListener) {
            mListener.onTransSuccess();
        }
    }
}
