

package com.wallet.ctc;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.text.TextUtils;

import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.SettingPrefUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

import common.app.AppApplication;


public class Constants {
    public static final String BASE_URL = SettingPrefUtil.getEthHostApi(AppApplication.getContext());
    public static final String API_URL = BASE_URL+"v1/";
    public static final String API_URL2 = SettingPrefUtil.getHostApi(AppApplication.getContext())+"api/v1/";
    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_FAIL = 0;

    public static final String AES = "AES";
    public static final String KEY = "5efd3f6060e20330";
    public static final String CBC = "AES/CBC/PKCS5Padding";
    public static final String IV = "625202f9149e0611";

    public static final BigInteger GAS_LIMIT = new BigInteger("80000");
    public static final BigDecimal GAS_PRICE = new BigDecimal(4+"000000000");

    
    public static boolean DEBUG = true;

    public static AssertBean createAssertBean(String logo, String symbol, String fullName, String contract, int decimal, int walletType) {
        if (TextUtils.isEmpty(contract) || decimal == 0 || TextUtils.isEmpty(symbol)) {
            return null;
        }
        return new AssertBean(logo, symbol, fullName, contract, "60000", decimal + "", walletType, 0);
    }

    
    

    
    public static final String NXN_BSC_TEST_CONTRACT = "";
    
    
    public static final String NXN_BSC_MAIN_CONTRACT = "";
    
    public static AssertBean getFmBscAssets() {
        String contract = DEBUG ? NXN_BSC_TEST_CONTRACT : NXN_BSC_MAIN_CONTRACT;
        if (TextUtils.isEmpty(contract)) {
            return null;
        }
        return createAssertBean("res://mipmap/mcc_logo", "nxn", "nxn", contract, 18, BNB_COIN);
    }

    
    public static final String DST_BSC_TEST_CONTRACT = "0x61C03A5693354C35ca2C23AFaC3bA05227F1fAB5";
    
    
    public static final String DST_BSC_MAIN_CONTRACT = "";
    public static final String DST_BSC_CONTRACT = DEBUG ? DST_BSC_TEST_CONTRACT : DST_BSC_MAIN_CONTRACT;
    
    public static AssertBean getDstBscAssets() {
        String contract = DST_BSC_CONTRACT;
        return createAssertBean("res://mipmap/tt_logo", "dst", "dst", contract, 18, BNB_COIN);
    }

    
    public static final String USDT_BSC_TEST_CONTRACT = "0xb0f5E208507dED150fEE5b39F78A1b109Cd81A12";
    
    public static final String USDT_BSC_MAIN_CONTRACT = "0x55d398326f99059ff775485246999027b3197955";
    public static final String USDT_BSC_CONTRACT = DEBUG ? USDT_BSC_TEST_CONTRACT : USDT_BSC_MAIN_CONTRACT;
    public static AssertBean getUSDTBscAssets() {
        String contract = DEBUG ? USDT_BSC_TEST_CONTRACT : USDT_BSC_MAIN_CONTRACT;
        return createAssertBean("res://mipmap/usdt_logo", "usdt", "usdt", contract, 18, BNB_COIN);
    }

    
    public static final String WBNB_BSC_TEST_CONTRACT = "0xae13d989daC2f0dEbFf460aC112a837C89BAa7cd";
    
    public static final String WBNB_BSC_MAIN_CONTRACT = "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c";
    public static final String WBNB_BSC_CONTRACT = DEBUG ? WBNB_BSC_TEST_CONTRACT : WBNB_BSC_MAIN_CONTRACT;
    public static AssertBean getWbnbBscAssets() {
        String contract = DEBUG ? WBNB_BSC_TEST_CONTRACT : WBNB_BSC_MAIN_CONTRACT;
        return createAssertBean("res://mipmap/bnb_logo", "wbnb", "wbnb", contract, 18, BNB_COIN);
    }

    
    public static final String THIRD_TOKEN_MAP_BSC_TEST_CONTRACT = "0xb29A1886025c6CF4a94EA3fdE00f67f890e83908";
    
    
    public static final String THIRD_TOKEN_MAP_BSC_MAIN_CONTRACT = "";
    public static final String THIRD_TOKEN_MAP_BSC_CONTRACT = DEBUG ? THIRD_TOKEN_MAP_BSC_TEST_CONTRACT : THIRD_TOKEN_MAP_BSC_MAIN_CONTRACT;

    
    public static final String BUY_DST_BSC_TEST_CONTRACT = "0x8001a491fce4095f931e075a63d9a326106bce94";
    
    
    public static final String BUY_DST_BSC_MAIN_CONTRACT = "";
    public static final String BUY_DST_BSC_CONTRACT = DEBUG ? BUY_DST_BSC_TEST_CONTRACT : BUY_DST_BSC_MAIN_CONTRACT;


    
    public static final String PANCAKESWAP_BSC_TEST_CONTRACT = "0xD99D1c33F9fC3444f8101754aBC46c52416550D1";
    
    public static final String PANCAKESWAP_BSC_MAIN_CONTRACT = "0x10ED43C718714eb63d5aA57B78B54704E256024E";
    public static final String PANCAKESWAP_BSC_CONTRACT = DEBUG ? PANCAKESWAP_BSC_TEST_CONTRACT : PANCAKESWAP_BSC_MAIN_CONTRACT;

    
    public static final String PANCAKESWAP_URL_USDT_DST = "https://pancakeswap.finance/swap"+
            (DEBUG ? "?chain=bscTestnet&" : "?") +
            "inputCurrency="+USDT_BSC_CONTRACT+"&outputCurrency="+DST_BSC_CONTRACT;


    
    public static final String CHAIN_BRIDGE_BSC_TEST_CONTRACT = "0x0f2A1DA58E738E50c5f05E3030275335478E4020";
    
    
    public static final String CHAIN_BRIDGE_BSC_MAIN_CONTRACT = "";
    public static final String CHAIN_BRIDGE_BSC_CONTRACT = DEBUG ? CHAIN_BRIDGE_BSC_TEST_CONTRACT : CHAIN_BRIDGE_BSC_MAIN_CONTRACT;


    
    
    public static final String USDT_ETH_TEST_CONTRACT = "0xd79BdAbBCAc0AB9AA00042651E162055cC03134A";
    
    public static final String USDT_ETH_MAIN_CONTRACT = "0xdac17f958d2ee523a2206206994597c13d831ec7";
    public static final String USDT_ETH_CONTRACT = DEBUG ? USDT_ETH_TEST_CONTRACT : USDT_ETH_MAIN_CONTRACT;
    public static AssertBean getUSDTEthAssets() {
        String contract = DEBUG ? USDT_ETH_TEST_CONTRACT : USDT_ETH_MAIN_CONTRACT;
        return createAssertBean("res://mipmap/usdt_logo", "usdt", "usdt", contract, 6, ETH_COIN);
    }

    
    public static final String CHAIN_BRIDGE_ETH_TEST_CONTRACT = "0x38322b4a9F2C56b4E718919A319427b19dacd225";
    
    
    public static final String CHAIN_BRIDGE_ETH_MAIN_CONTRACT = "";
    public static final String CHAIN_BRIDGE_ETH_CONTRACT = DEBUG ? CHAIN_BRIDGE_ETH_TEST_CONTRACT : CHAIN_BRIDGE_ETH_MAIN_CONTRACT;



    
    
    public static final String USDT_EVMOS_TEST_CONTRACT = "0x3D8968806EFaCA3769aE83c6983C0B4424827d77";
    
    
    public static final String USDT_EVMOS_MAIN_CONTRACT = "";
    public static AssertBean getUSDTEvmosAssets() {
        String contract = DEBUG ? USDT_EVMOS_TEST_CONTRACT : USDT_EVMOS_MAIN_CONTRACT;
        return createAssertBean("res://mipmap/usdt_logo", "usdt", "usdt", contract, 18, MCC_COIN);
    }

    
    
    public static final String CHAIN_BRIDGE_EVMOS_TEST_CONTRACT = "0x44120442C78102f50bcD17071F202D86FC9539b2";
    
    public static final String CHAIN_BRIDGE_EVMOS_MAIN_CONTRACT = "";
    public static final String CHAIN_BRIDGE_EVMOS_CONTRACT = DEBUG ? CHAIN_BRIDGE_EVMOS_TEST_CONTRACT : CHAIN_BRIDGE_EVMOS_MAIN_CONTRACT;


}
