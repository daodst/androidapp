package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.db.WalletEntity;

import java.util.List;


public class RootWalletInfo {

    public int type;
    public WalletEntity rootWallet; 
    public List<WalletEntity> childWallets;

    public RootWalletInfo(int type, WalletEntity rootWallet, List<WalletEntity> childWallets) {
        this.type = type;
        this.rootWallet = rootWallet;
        this.childWallets = childWallets;
    }

    
    public int getNextIndex() {
        if (null == childWallets) {
            return 1;
        } else {
            return childWallets.size() + 1;
        }
    }

    
    public String getRootWalletName() {
        if (rootWallet != null) {
            return rootWallet.getName();
        }
        return "";
    }
}
