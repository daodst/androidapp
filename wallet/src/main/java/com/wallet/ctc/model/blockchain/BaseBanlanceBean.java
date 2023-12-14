

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.db.WalletEntity;

import java.util.List;



public class BaseBanlanceBean {
    private WalletEntity walletEntity;
    private List<AssertBean> assertBeans;
    private BandWidthBean bandWidthBean;

    public WalletEntity getWalletEntity() {
        return walletEntity;
    }

    public void setWalletEntity(WalletEntity walletEntity) {
        this.walletEntity = walletEntity;
    }

    public List<AssertBean> getAssertBeans() {
        return assertBeans;
    }

    public void setAssertBeans(List<AssertBean> assertBeans) {
        this.assertBeans = assertBeans;
    }

    public BandWidthBean getBandWidthBean() {
        return bandWidthBean;
    }

    public void setBandWidthBean(BandWidthBean bandWidthBean) {
        this.bandWidthBean = bandWidthBean;
    }
}
