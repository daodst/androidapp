

package com.wallet.ctc.model.me;



public class OneClickLoginBean {

    public String innerAccount;
    public String userName;
    public String account;
    public String ico;
    public String nickName;
    public String auth;

    public OneClickLoginBean(){

    }

    public OneClickLoginBean(String innerAccount, String userName, String account, String ico, String nickName, String auth){
        this.innerAccount=innerAccount;
        this.userName=userName;
        this.account=account;
        this.ico=ico;
        this.nickName=nickName;
        this.auth=auth;
    }

}
