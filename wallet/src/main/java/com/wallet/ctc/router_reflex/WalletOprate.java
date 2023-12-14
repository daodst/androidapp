

package com.wallet.ctc.router_reflex;

import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.util.DecriptUtil;

import common.app.AppApplication;
import common.app.router_reflex.IWalletOperate;


public class WalletOprate implements IWalletOperate {

    @Override
    public String getAllAddress() {
        return WalletDBUtil.getInstent(AppApplication.getInstance().getApplicationContext()).getWalletInfo().getAllAddress();
    }

    @Override
    public int getWalletType() {
        return WalletDBUtil.getInstent(AppApplication.getInstance().getApplicationContext()).getWalletInfo().getType();
    }

    @Override
    public String getWalletEncriptPwd() {
        return WalletDBUtil.getInstent(AppApplication.getInstance().getApplicationContext()).getWalletInfo().getmPassword();
    }

    @Override
    public String getDecriptMd5(String pwd) {
        return DecriptUtil.MD5(pwd);
    }

    @Override
    public void tuiJianPay() {
        
    }
}
