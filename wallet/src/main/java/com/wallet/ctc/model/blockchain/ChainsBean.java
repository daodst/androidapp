

package com.wallet.ctc.model.blockchain;

import static com.wallet.ctc.crypto.WalletUtil.ADA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BCH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOGE_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.FIL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.LTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MATIC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SGB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SOL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ZEC_COIN;

import android.text.TextUtils;

import com.wallet.ctc.R;

import java.util.List;

import common.app.AppApplication;


public class ChainsBean {


    


    private String name;
    private String desc;
    private String logo;
    private String type;
    private String address_prifix;
    private List<String> rpc_urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress_prifix() {
        return address_prifix;
    }

    public void setAddress_prifix(String address_prifix) {
        this.address_prifix = address_prifix;
    }

    public List<String> getRpc_urls() {
        return rpc_urls;
    }

    public void setRpc_urls(List<String> rpc_urls) {
        this.rpc_urls = rpc_urls;
    }
    
    public int getWalletType() {
        int coinType = -1;
        if (TextUtils.isEmpty(type)) {
            return coinType;
        }
        if (type.equals("dm")) {
            coinType = DM_COIN;
        } else if (type.equals("eth")) {
            coinType = ETH_COIN;
        } else if (type.equals("btc")) {
            coinType = BTC_COIN;
        } else if (type.equals("eos")) {
            coinType = EOS_COIN;
        } else if (type.equals("mcc")) {
            coinType = MCC_COIN;
        } else if (type.equals("other")) {
            coinType = OTHER_COIN;
        } else if (type.equals("xrp")) {
            coinType = XRP_COIN;
        } else if (type.equals("trx")) {
            coinType = TRX_COIN;
        } else if (type.equals(AppApplication.getInstance().getApplicationContext().getString(R.string.default_etf))) {
            coinType = ETF_COIN;
        } else if (type.equals("heco")) {
            coinType = HT_COIN;
        } else if (type.equals("dmf")) {
            coinType = DMF_COIN;
        } else if (type.equals("bian_smart")) {
            coinType = BNB_COIN;
        } else if (type.equals("fil")) {
            coinType = FIL_COIN;
        } else if (type.equals("doge")) {
            coinType = DOGE_COIN;
        } else if (type.equals("dot")) {
            coinType = DOT_COIN;
        } else if (type.equals("ltc")) {
            coinType = LTC_COIN;
        } else if (type.equals("bch")) {
            coinType = BCH_COIN;
        } else if (type.equals("zec")) {
            coinType = ZEC_COIN;
        } else if (type.equals("etc")) {
            coinType = ETC_COIN;
        } else if (type.equals("ada")) {
            coinType = ADA_COIN;
        } else if (type.equals("sgb")) {
            coinType = SGB_COIN;
        } else if (type.equals("sol")) {
            coinType = SOL_COIN;
        } else if (type.equals("matic")) {
            coinType = MATIC_COIN;
        } else if(type.equals(AppApplication.getInstance().getString(R.string.default_token_name))) {
            coinType = MCC_COIN;
        }
        return coinType;
    }

    
    public static ChainsBean newInstance(String typeName) {
        ChainsBean bean = new ChainsBean();
        bean.type = typeName;
        return bean;
    }
}
