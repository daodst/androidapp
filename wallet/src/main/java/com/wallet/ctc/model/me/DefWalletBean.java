

package com.wallet.ctc.model.me;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;

import com.wallet.ctc.R;

import common.app.AppApplication;



public class DefWalletBean {


    

    private String type;
    private String address;

    public DefWalletBean(){

    }

    public DefWalletBean(int tp,String address){
        this.address=address;
        if(tp==DM_COIN){
            this.type="dm";
        }else if(tp==ETH_COIN){
            this.type="eth";
        }else if(tp==BTC_COIN){
            this.type="btc";
        }else if(tp==EOS_COIN){
            this.type="eos";
        }else if(tp==MCC_COIN){
            this.type= AppApplication.getContext().getString(R.string.default_token_name).toLowerCase();
        }else if(tp==OTHER_COIN){
            this.type= AppApplication.getContext().getString(R.string.default_other_token_name).toLowerCase();
        }else if(tp==ETF_COIN){
            this.type= AppApplication.getContext().getString(R.string.default_etf).toLowerCase();
        }else if(tp==DMF_COIN){
            this.type= AppApplication.getContext().getString(R.string.default_dmf_hb).toLowerCase();
        }else if(tp==DMF_BA_COIN){
            this.type= AppApplication.getContext().getString(R.string.default_dmf_ba).toLowerCase();
        }else if(tp==HT_COIN){
            this.type= "ht";
        }else if(tp==BNB_COIN){
            this.type= "bnb";
        }else if(tp==TRX_COIN){
            this.type= "trx";
        }

    }

    public String getType() {
        return type;
    }
    public int getTureType() {
        int T=0;
        if(type.equals("dm")){
            T=DM_COIN;
        }else if(type.equals("eth")){
            T=ETH_COIN;
        }else if(type.equals("btc")){
            T=BTC_COIN;
        }else if(type.equals("eos")){
            T=EOS_COIN;
        }else if(type.equals(AppApplication.getContext().getString(R.string.default_token_name).toLowerCase())){
            T=MCC_COIN;
        }else if(type.equals(AppApplication.getContext().getString(R.string.default_other_token_name).toLowerCase())){
            T=OTHER_COIN;
        }else if(type.equals(AppApplication.getContext().getString(R.string.default_etf).toLowerCase())){
            T=ETF_COIN;
        }else if(type.equals(AppApplication.getContext().getString(R.string.default_dmf_hb).toLowerCase())){
            T=DMF_COIN;
        }else if(type.equals(AppApplication.getContext().getString(R.string.default_dmf_ba).toLowerCase())){
            T=DMF_BA_COIN;
        }else if(type.equals("trx")){
            T=TRX_COIN;
        }else if(type.equals("xrp")){
            T=XRP_COIN;
        }else if(type.equals("bnb")){
            T=BNB_COIN;
        }else if(type.equals("ht")){
            T=HT_COIN;
        }else {
            T=5;
        }

        return T;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
