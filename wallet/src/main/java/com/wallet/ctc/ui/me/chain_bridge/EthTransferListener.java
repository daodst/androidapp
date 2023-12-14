package com.wallet.ctc.ui.me.chain_bridge;

import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.GasPriceBean;


public class EthTransferListener implements WalletTransctionUtil.TransctionListen{
    @Override
    public void showLoading() {

    }

    @Override
    public void showGasCount(String gasCount) {

    }

    @Override
    public void showGasprice(GasPriceBean gasPriceBean) {

    }

    @Override
    public void showEip1559(String baseFeePerGas) {

    }

    @Override
    public void showDefGasprice(String defGasprice) {

    }

    @Override
    public void showTransctionSuccess(String hash) {

    }

    @Override
    public void onFail(String msg) {

    }
}
