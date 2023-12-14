

package com.wallet.ctc.crypto;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.text.TextUtils;
import android.util.Log;

import com.wallet.ctc.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.app.ActivityRouter;
import common.app.utils.AllUtils;
import common.app.utils.SpUtil;
import common.app.wallet.WalletBridge;


public class ChainsRpcsUtil {

    public static final int CHAINID_ETH = 1;
    public static final int CHAINID_ETH_TEST = 3;
    public static final int CHAINID_SEELE = 186; 
    public static final int CHAINID_BNB = 56; 
    public static final int CHAINID_BNB_TEST = 97; 
    public static final int CHAINID_HT = 128; 
    public static final int CHAINID_OKEX = 66; 
    public static final int CHAINID_ETF = 9886; 
    public static final int CHAINID_MCC = 7777; 

    private static String CHAIN_ID_MCC_STR = "";

    public static String getChainIdMccStr() {

        if (TextUtils.isEmpty(CHAIN_ID_MCC_STR)) {
            CHAIN_ID_MCC_STR = SpUtil.getChainId();
        }
        return CHAIN_ID_MCC_STR;
    }

    
    public static final int CHAINID_MCC_PUBLIC = 7777;


    
    public static int walletTypeToChainId(int walletType) {
        switch (walletType) {
            case ETH_COIN:
                return CHAINID_ETH;
            case BNB_COIN:
                return CHAINID_BNB;
            case HT_COIN:
                return CHAINID_HT;
            case DMF_COIN:
                return CHAINID_OKEX;
            case ETF_COIN:
                return CHAINID_ETF;
            case MCC_COIN:
                int chainId = getMccSmartChainId();
                return chainId;
        }
        return 0;
    }

    
    public static int getMccSmartChainId() {
        String evmosChainId = getChainIdMccStr();
        if (TextUtils.isEmpty(evmosChainId)) {
            Log.e("chainid", "chainId is empty use default value");
            return CHAINID_MCC;
        }
        String[] arrayStr = evmosChainId.split("_");
        if (null != arrayStr && arrayStr.length == 2) {
            String numberStr = arrayStr[1];
            if (TextUtils.isEmpty(numberStr)) {
                Log.e("chainid", "numberStr is empty return default value");
                return CHAINID_MCC;
            }
            String number = "";
            String[] numberArray = numberStr.split("-");
            if (null != numberArray && numberArray.length > 0) {
                number = numberArray[0];
            }
            if (TextUtils.isEmpty(number)) {
                Log.e("chainId", "chainid parse error "+numberStr);
                return CHAINID_MCC;
            }
            try {
                Integer integer = Integer.parseInt(number);
                if (null != integer) {
                    return integer.intValue();
                } else {
                    Log.e("chainId", "chainid parse error integer null "+numberStr);
                    return CHAINID_MCC;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
                Log.e("chainId", "chainid parse exception : "+e.getMessage());
                return CHAINID_MCC;
            }
        } else {
            Log.e("chainid", "invalidate chainId : "+evmosChainId);
            return CHAINID_MCC;
        }
    }

    
    public static int chainIdToWalletType(int chainId) {
        int walletyType = -1;
        int mccSmartChainId = getMccSmartChainId();
        if (chainId == mccSmartChainId) {
            walletyType = MCC_COIN;
            return walletyType;
        }
        switch (chainId) {
            case CHAINID_BNB://BNB
            case CHAINID_BNB_TEST://BNB
                walletyType = BNB_COIN;
                break;
            case CHAINID_ETH://ETH
            case CHAINID_ETH_TEST:
                walletyType = ETH_COIN;
                break;
            case CHAINID_HT:// hT
                walletyType = HT_COIN;
                break;
            case CHAINID_ETF:// ETF
                walletyType = ETF_COIN;
                break;
            case CHAINID_MCC://MCC 
                walletyType = MCC_COIN;
                break;
        }
        return walletyType;
    }

    
    public static String getRpcUrlByWalletType(int walletType) {
        String rpcUrl = "";
        if (walletType == MCC_COIN) {
            rpcUrl = SpUtil.getNodeSmartUrl();
        } else {
            rpcUrl = SpUtil.getDefNode(walletType);
        }
        return rpcUrl;
    }

    
    public static String getRpcUrlByChainid(int chainId) {
        return getRpcUrlByWalletType(chainIdToWalletType(chainId));
    }

    
    private static WalletBridge mWalletBridge;

    public static String getAppDeftRpcUrl(int walletType) {
        if (null == mWalletBridge) {
            mWalletBridge = ActivityRouter.getWalletBridge(ActivityRouter.Wallet.A_WALLETBRIDGEIMPL);
        }
        return mWalletBridge.getJsonRpc(walletType);
    }

    
    public static List<String> getSuggestRpcUrls(int walletType) {
        String[] rpcUrls = null;
        String defaultRpcUrl = getAppDeftRpcUrl(walletType);
        if (walletType == ETH_COIN) {
            
            rpcUrls = new String[]{"https://mainnet.infura.io/v3/" + BuildConfig.INFURA_APPKEY, "https://web3.mytokenpocket.vip",
                    "https://api.mycryptoapi.com/eth", "https://rpc.ankr.com/eth", "https://mainnet.eth.cloud.ava.do"};
        } else if (walletType == BNB_COIN) {
            
            rpcUrls = new String[]{"https://bsc-dataseed.binance.org", "https://bsc-dataseed1.defibit.io", "https://bsc-dataseed1.ninicoin.io",
                    "https://bsc-dataseed2.defibit.io", "https://bsc-dataseed3.defibit.io", "https://bsc-dataseed4.defibit.io", "https://bsc-dataseed2.ninicoin.io",
                    "https://bsc-dataseed3.ninicoin.io", "https://bsc-dataseed4.ninicoin.io", "https://bsc-dataseed1.binance.org", "https://bsc-dataseed2.binance.org",
                    "https://bsc-dataseed3.binance.org", "https://bsc-dataseed4.binance.org"};
        } else if (walletType == HT_COIN) {
            
            rpcUrls = new String[]{"https://http-mainnet-node.huobichain.com", "https://http-mainnet.hecochain.com",
                    "https://pub001.hg.network/rpc"};
        }
        List<String> list = new ArrayList<>();
        
        if (!TextUtils.isEmpty(defaultRpcUrl)) {
            list.add(defaultRpcUrl);
        }
        if (null != rpcUrls && rpcUrls.length > 0) {
            list.addAll(new ArrayList<>(Arrays.asList(rpcUrls)));
        }
        
        return AllUtils.distinctList(list);
    }


}
