

package com.wallet.ctc.util;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;

import java.util.ArrayList;
import java.util.List;



public class WalletSpUtil {

    private static final String DAPP_URL = "dapp_url";
    private static final String ETH_HOST = "ethhost";
    private static final String DM_HOST = "dmhost";
    private static final String MCC_HOST = "mcchost";
    private static final String ENABLE_MCC = "enable_mcc";
    private static final String ENABLE_DM = "enable_dm";
    private static final String ENABLE_ETH = "enable_eth";
    private static final String ENABLE_BTC = "enable_btc";
    private static final String ENABLE_EOS = "enable_eos";
    private static final String ENABLE_XRP = "enable_xrp";
    private static final String ENABLE_TRX = "enable_trx";
    private static final String ENABLE_ETF = "enable_etf";
    private static final String ENABLE_DMF = "enable_dmf";
    private static final String ENABLE_DMF_BA = "enable_dmf_ba";
    private static final String ENABLE_HT = "enable_ht";
    private static final String ENABLE_BNB = "enable_bnb";
    private static final String ENABLE_OTHER = "enable_other";

    private static final String ENABLE_FIL = "enable_fil";
    private static final String ENABLE_DOGE = "enable_Dogecoin";
    private static final String ENABLE_DOT = "enable_Polkadot";
    private static final String ENABLE_LTC = "enable_Litecoin";
    private static final String ENABLE_BCH = "enable_Bitcoin Cash";
    private static final String ENABLE_ZEC = "enable_Zcash";
    private static final String ENABLE_ADA = "enable_Cardano";
    private static final String ENABLE_ETC = "enable_Ethereum_Classic";
    private static final String ENABLE_SGB = "enable_SubGame";
    private static final String ENABLE_SOL = "enable_solana";
    private static final String ENABLE_MATIC = "enable_polygon";

    private static MMKV kv;

    private static MMKV getMMKV() {
        if (kv == null) {
            kv = MMKV.defaultMMKV();
        }
        return kv;
    }
    public static void saveEthHostApi(String url) {
        getMMKV().encode(ETH_HOST, url);
    }

    public static String getEthHostApi() {
        return getMMKV().decodeString(ETH_HOST, BuildConfig.ETH_HOST);
    }

    public static void saveDMHostApi(String url) {

        getMMKV().encode(DM_HOST, url);

    }

    public static String getDMHostApi() {
        return getMMKV().decodeString(DM_HOST, BuildConfig.HOST_DM_QUOTES);
    }

    public static void saveMCCHostApi(String url) {

        getMMKV().encode(MCC_HOST, url);

    }

    public static String getMCCHostApi() {
        return getMMKV().decodeString(MCC_HOST, BuildConfig.HOST_QUOTES);
    }
    public static void saveDappUrl(String url) {
        getMMKV().encode(DAPP_URL, url);
    }
    public static String getDappUrl() {
        return getMMKV().decodeString(DAPP_URL, BuildConfig.DAPP_URL);
    }

    
    public static void setAllCoinEanable(boolean enable) {
        int ena = enable ? 1 : 0;
        WalletSpUtil.saveEnableBtc(ena);
        WalletSpUtil.saveEnableDm(ena);
        WalletSpUtil.saveEnableDmf(ena);
        WalletSpUtil.saveEnableDmfBa(ena);
        WalletSpUtil.saveEnableEos(ena);
        WalletSpUtil.saveEnableEtf(ena);
        WalletSpUtil.saveEnableEth(ena);
        WalletSpUtil.saveEnableMcc(ena);
        WalletSpUtil.saveEnableTrx(ena);
        WalletSpUtil.saveEnableXrp(ena);
        WalletSpUtil.saveEnableOther(ena);
        WalletSpUtil.saveEnableHt(ena);
        WalletSpUtil.saveEnableFIL(ena);
        WalletSpUtil.saveEnableDOGE(ena);
        WalletSpUtil.saveEnableDOT(ena);
        WalletSpUtil.saveEnableLTC(ena);
        WalletSpUtil.saveEnableBCH(ena);
        WalletSpUtil.saveEnableZEC(ena);
        WalletSpUtil.saveEnableETC(ena);
        WalletSpUtil.saveEnableADA(ena);
        WalletSpUtil.saveEnableSGB(ena);
        WalletSpUtil.saveEnableSOL(ena);
        WalletSpUtil.saveEnableMATIC(ena);
    }

    
    public static void setCoinEnable(int walletType, boolean enable) {
        int ena = enable ? 1 : 0;
        if (walletType == WalletUtil.DM_COIN) {
            WalletSpUtil.saveEnableDm(ena);
        } else if (walletType == WalletUtil.ETH_COIN) {
            WalletSpUtil.saveEnableEth(ena);
        } else if (walletType == WalletUtil.BTC_COIN) {
            WalletSpUtil.saveEnableBtc(ena);
        } else if (walletType == WalletUtil.EOS_COIN) {
            WalletSpUtil.saveEnableEos(ena);
        } else if (walletType == WalletUtil.MCC_COIN) {
            WalletSpUtil.saveEnableMcc(ena);
        } else if (walletType == WalletUtil.OTHER_COIN) {
            WalletSpUtil.saveEnableOther(ena);
        } else if (walletType == WalletUtil.XRP_COIN) {
            WalletSpUtil.saveEnableXrp(ena);
        } else if (walletType == WalletUtil.TRX_COIN) {
            WalletSpUtil.saveEnableTrx(ena);
        } else if (walletType == WalletUtil.ETF_COIN) {
            WalletSpUtil.saveEnableEtf(ena);
        } else if (walletType == WalletUtil.HT_COIN) {
            WalletSpUtil.saveEnableHt(ena);
        } else if (walletType == WalletUtil.DMF_COIN) {
            WalletSpUtil.saveEnableDmf(ena);
        } else if (walletType == WalletUtil.BNB_COIN) {
            WalletSpUtil.saveEnableBnb(ena);
        } else if (walletType == WalletUtil.FIL_COIN) {
            WalletSpUtil.saveEnableFIL(ena);
        } else if (walletType == WalletUtil.DOGE_COIN) {
            WalletSpUtil.saveEnableDOGE(ena);
        } else if (walletType == WalletUtil.DOT_COIN) {
            WalletSpUtil.saveEnableDOT(ena);
        } else if (walletType == WalletUtil.LTC_COIN) {
            WalletSpUtil.saveEnableLTC(ena);
        } else if (walletType == WalletUtil.BCH_COIN) {
            WalletSpUtil.saveEnableBCH(ena);
        } else if (walletType == WalletUtil.ZEC_COIN) {
            WalletSpUtil.saveEnableZEC(ena);
        } else if (walletType == WalletUtil.ETC_COIN) {
            WalletSpUtil.saveEnableETC(ena);
        } else if (walletType == WalletUtil.ADA_COIN) {
            WalletSpUtil.saveEnableADA(ena);
        }else if (walletType == WalletUtil.SGB_COIN) {
            WalletSpUtil.saveEnableSGB(ena);
        }else if (walletType == WalletUtil.SOL_COIN) {
            WalletSpUtil.saveEnableSOL(ena);
        }else if (walletType == WalletUtil.MATIC_COIN) {
            WalletSpUtil.saveEnableMATIC(ena);
        }
    }



    public static int getEnableMcc() {
        return getMMKV().decodeInt(ENABLE_MCC, BuildConfig.ENABLE_MCC);
    }

    public static void saveEnableMcc(int data) {
        getMMKV().encode(ENABLE_MCC, data);
    }

    public static int getEnableDm() {
        return getMMKV().decodeInt(ENABLE_DM, BuildConfig.ENABLE_DM);
    }

    public static void saveEnableDm(int data) {
        getMMKV().encode(ENABLE_DM, data);
    }

    public static int getEnableEth() {
        return getMMKV().decodeInt(ENABLE_ETH, BuildConfig.ENABLE_ETH);
    }

    public static void saveEnableEth(int data) {
        getMMKV().encode(ENABLE_ETH, data);
    }

    public static int getEnableOther() {
        return getMMKV().decodeInt(ENABLE_OTHER, BuildConfig.ENABLE_OTHER);
    }

    public static void saveEnableOther(int data) {
        getMMKV().encode(ENABLE_OTHER, data);
    }

    public static int getEnableDmfBa() {
        return getMMKV().decodeInt(ENABLE_DMF_BA, BuildConfig.ENABLE_DMF_BA);
    }

    public static void saveEnableDmfBa(int data) {
        getMMKV().encode(ENABLE_DMF_BA, data);
    }

    public static int getEnableHt() {
        return getMMKV().decodeInt(ENABLE_HT, BuildConfig.ENABLE_HT);
    }

    public static void saveEnableHt(int data) {
        getMMKV().encode(ENABLE_HT, data);
    }

    public static int getEnableBnb() {
        return getMMKV().decodeInt(ENABLE_BNB, BuildConfig.ENABLE_BNB);
    }

    public static void saveEnableBnb(int data) {
        getMMKV().encode(ENABLE_BNB, data);
    }


    public static int getEnableDmf() {
        return getMMKV().decodeInt(ENABLE_DMF, BuildConfig.ENABLE_DMF);
    }

    public static void saveEnableDmf(int data) {
        getMMKV().encode(ENABLE_DMF, data);
    }

    public static int getEnableEtf() {
        return getMMKV().decodeInt(ENABLE_ETF, BuildConfig.ENABLE_ETF);
    }

    public static void saveEnableEtf(int data) {
        getMMKV().encode(ENABLE_ETF, data);
    }

    public static int getEnableTrx() {
        return getMMKV().decodeInt(ENABLE_TRX, BuildConfig.ENABLE_TRX);
    }

    public static void saveEnableTrx(int data) {
        getMMKV().encode(ENABLE_TRX, data);
    }

    public static int getEnableXrp() {
        return getMMKV().decodeInt(ENABLE_XRP, BuildConfig.ENABLE_XRP);
    }

    public static void saveEnableXrp(int data) {
        getMMKV().encode(ENABLE_XRP, data);
    }

    public static int getEnableEos() {
        return getMMKV().decodeInt(ENABLE_EOS, BuildConfig.ENABLE_EOS);
    }

    public static void saveEnableEos(int data) {
        getMMKV().encode(ENABLE_EOS, data);
    }

    public static int getEnableBtc() {
        return getMMKV().decodeInt(ENABLE_BTC, BuildConfig.ENABLE_BTC);
    }

    public static void saveEnableBtc(int data) {
        getMMKV().encode(ENABLE_BTC, data);
    }


    
    public static int getEnableFIL() {
        return getMMKV().decodeInt(ENABLE_FIL, BuildConfig.ENABLE_FIL);
    }

    public static void saveEnableFIL(int data) {
        getMMKV().encode(ENABLE_FIL, data);
    }

    public static int getEnableDOGE() {
        return getMMKV().decodeInt(ENABLE_DOGE, BuildConfig.ENABLE_DOGE);
    }

    public static void saveEnableDOGE(int data) {
        getMMKV().encode(ENABLE_DOGE, data);
    }

    public static int getEnableDOT() {
        return getMMKV().decodeInt(ENABLE_DOT, BuildConfig.ENABLE_DOT);
    }

    public static void saveEnableDOT(int data) {
        getMMKV().encode(ENABLE_DOT, data);
    }

    public static int getEnableLTC() {
        return getMMKV().decodeInt(ENABLE_LTC, BuildConfig.ENABLE_LTC);
    }

    public static void saveEnableLTC(int data) {
        getMMKV().encode(ENABLE_LTC, data);
    }

    public static int getEnableBCH() {
        return getMMKV().decodeInt(ENABLE_BCH, BuildConfig.ENABLE_BCH);
    }

    public static void saveEnableBCH(int data) {
        getMMKV().encode(ENABLE_BCH, data);
    }

    public static int getEnableZEC() {
        return getMMKV().decodeInt(ENABLE_ZEC, BuildConfig.ENABLE_ZEC);
    }

    public static void saveEnableZEC(int data) {
        getMMKV().encode(ENABLE_ZEC, data);
    }

    public static int getEnableADA() {
        return getMMKV().decodeInt(ENABLE_ADA, BuildConfig.ENABLE_ADA);
    }

    public static void saveEnableADA(int data) {
        getMMKV().encode(ENABLE_ADA, data);
    }

    public static int getEnableETC() {
        return getMMKV().decodeInt(ENABLE_ETC, BuildConfig.ENABLE_ETC);
    }

    public static void saveEnableETC(int data) {
        getMMKV().encode(ENABLE_ETC, data);
    }

    public static int getEnableSGB() {
        return getMMKV().decodeInt(ENABLE_SGB, BuildConfig.ENABLE_SGB);
    }

    public static void saveEnableSGB(int data) {
        getMMKV().encode(ENABLE_SGB, data);
    }
    public static int getEnableSOL() {
        return getMMKV().decodeInt(ENABLE_SOL, BuildConfig.ENABLE_SOL);
    }

    public static void saveEnableSOL(int data) {
        getMMKV().encode(ENABLE_SOL, data);
    }

    public static int getEnableMATIC() {
        return getMMKV().decodeInt(ENABLE_MATIC, BuildConfig.ENABLE_MATIC);
    }

    public static void saveEnableMATIC(int data) {
        getMMKV().encode(ENABLE_MATIC, data);
    }

    
    public static List<Integer> getEnableCoinTypeList() {
        List<Integer> list = new ArrayList<>();
        if (getEnableDm() == 1) { list.add(WalletUtil.DM_COIN);}
        if (getEnableEth() == 1) {list.add(WalletUtil.ETH_COIN);}
        if (getEnableBtc() == 1) {list.add(WalletUtil.BTC_COIN);}
        if (getEnableEos() == 1) {list.add(WalletUtil.EOS_COIN);}
        if (getEnableMcc() == 1) {list.add(WalletUtil.MCC_COIN);}
        if (getEnableOther() == 1) {list.add(WalletUtil.OTHER_COIN);}
        if (getEnableXrp() == 1) {list.add(WalletUtil.XRP_COIN);}
        if (getEnableTrx() == 1) {list.add(WalletUtil.TRX_COIN);}
        if (getEnableEtf() == 1) {list.add(WalletUtil.ETF_COIN);}
        if (getEnableDmf() == 1) {list.add(WalletUtil.DMF_COIN);}
        if (getEnableDmfBa() == 1) {list.add(WalletUtil.DMF_BA_COIN);}
        if (getEnableHt() == 1) {list.add(WalletUtil.HT_COIN);}
        if (getEnableBnb() == 1) {list.add(WalletUtil.BNB_COIN);}
        if (getEnableFIL() == 1) {list.add(WalletUtil.FIL_COIN);}
        if (getEnableDOGE() == 1) {list.add(WalletUtil.DOGE_COIN);}
        if (getEnableDOT() == 1) {list.add(WalletUtil.DOT_COIN);}
        if (getEnableLTC() == 1) {list.add(WalletUtil.LTC_COIN);}
        if (getEnableBCH() == 1) {list.add(WalletUtil.BCH_COIN);}
        if (getEnableZEC() == 1) {list.add(WalletUtil.ZEC_COIN);}
        if (getEnableADA() == 1) {list.add(WalletUtil.ADA_COIN);}
        if (getEnableETC() == 1) {list.add(WalletUtil.ETC_COIN);}
        if (getEnableSGB() == 1) {list.add(WalletUtil.SGB_COIN);}
        if (getEnableSOL() == 1) {list.add(WalletUtil.SOL_COIN);}
        if (getEnableMATIC() == 1) {list.add(WalletUtil.MATIC_COIN);}
        return list;
    }




    
    private static final String KEY_CHAIN_BRIDGE_FROM_ADDR = "chainBridgeFromAddr";
    private static final String KEY_CHAIN_BRIDGE_FROM_TYPE = "chainBridgeFromType";
    private static final String KEY_CHAIN_BRIDGE_TO_ADDR = "chainBridgeToAddr";
    private static final String KEY_CHAIN_BRIDGE_TO_TYPE = "chainBridgeToType";
    public static void saveChainBridgeFromTo(String fromAddress, int fromType, String toAddr, int toType){
        getMMKV().encode(KEY_CHAIN_BRIDGE_FROM_ADDR, fromAddress);
        getMMKV().encode(KEY_CHAIN_BRIDGE_FROM_TYPE, fromType);

        getMMKV().encode(KEY_CHAIN_BRIDGE_TO_ADDR, toAddr);
        getMMKV().encode(KEY_CHAIN_BRIDGE_TO_TYPE, toType);
    }

    
    public static WalletEntity getChainBridgeFrom(Context context) {
        String address = getMMKV().decodeString(KEY_CHAIN_BRIDGE_FROM_ADDR);
        int type = getMMKV().decodeInt(KEY_CHAIN_BRIDGE_FROM_TYPE);
        WalletEntity wallet = null;
        if (!TextUtils.isEmpty(address) && type >= 0) {
            wallet = WalletDBUtil.getInstent(context).getWalletInfoByAddress(address, type);
        }
        return wallet;
    }

    
    public static WalletEntity getChainBridgeTo(Context context) {
        String address = getMMKV().decodeString(KEY_CHAIN_BRIDGE_TO_ADDR);
        int type = getMMKV().decodeInt(KEY_CHAIN_BRIDGE_TO_TYPE);
        WalletEntity wallet = null;
        if (!TextUtils.isEmpty(address) && type >= 0) {
            wallet = WalletDBUtil.getInstent(context).getWalletInfoByAddress(address, type);
        }
        return wallet;
    }





}
