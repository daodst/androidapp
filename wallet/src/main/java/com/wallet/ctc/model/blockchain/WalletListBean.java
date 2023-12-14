

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.db.WalletEntity;

import java.util.List;



public class WalletListBean {
    private List<WalletEntity> mWalletName;

    public WalletListBean(List<WalletEntity> list){
        this.mWalletName=list;
    }

    public List<WalletEntity> getmWalletName() {
        return mWalletName;
    }

    public void setmWalletName(List<WalletEntity> mWalletName) {
        this.mWalletName = mWalletName;
    }
}
