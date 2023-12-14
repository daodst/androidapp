

package com.app.lg4e.pojo;

import androidx.fragment.app.Fragment;



public class MainTabBean {
    private int logo;
    private int title;
    private Fragment fragment;
    private boolean needLogin;
    private boolean needWallet;

    public MainTabBean(int logo,int title,Fragment fragment,boolean needLogin,boolean needWallet){
        this.logo=logo;
        this.title=title;
        this.fragment=fragment;
        this.needLogin=needLogin;
        this.needWallet=needWallet;
    }


    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public boolean isNeedWallet() {
        return needWallet;
    }

    public void setNeedWallet(boolean needWallet) {
        this.needWallet = needWallet;
    }
}
