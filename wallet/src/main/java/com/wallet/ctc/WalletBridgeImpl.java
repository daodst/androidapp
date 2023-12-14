

package com.wallet.ctc;

import static com.wallet.ctc.BuildConfig.HOST_BCH;
import static com.wallet.ctc.BuildConfig.HOST_DOGE;
import static com.wallet.ctc.BuildConfig.HOST_DOT;
import static com.wallet.ctc.BuildConfig.HOST_ETC;
import static com.wallet.ctc.BuildConfig.HOST_FIL;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_BNB;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_BTC;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_DMF;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_ETF;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_ETH;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_HT;
import static com.wallet.ctc.BuildConfig.HOST_JSONRPC_XRP;
import static com.wallet.ctc.BuildConfig.HOST_LTC;
import static com.wallet.ctc.BuildConfig.HOST_MATIC;
import static com.wallet.ctc.BuildConfig.HOST_QUOTES;
import static com.wallet.ctc.BuildConfig.HOST_SGB;
import static com.wallet.ctc.BuildConfig.HOST_SOL;
import static com.wallet.ctc.BuildConfig.HOST_TRX_QUOTES;
import static com.wallet.ctc.BuildConfig.HOST_ZEC;
import static com.wallet.ctc.crypto.WalletUtil.ADA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BCH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
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

import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.DecriptUtil;

import java.util.Map;

import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import common.app.utils.PhoneUtil;
import common.app.wallet.WalletBridge;


public class WalletBridgeImpl implements WalletBridge {

    @Override
    public int getWalletSize(Context context) {
        return WalletDBUtil.getInstent(context).getWallName().size();
    }

    @Override
    public Map<String, String> getGroupPayParams(Context context, Map<String, String> params, String pwd, String authType) {
        WalletEntity walletEntity = WalletDBUtil.getInstent(context).getWalletInfo();
        if (!walletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
            ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
            return null;
        }
        params.put("auth_type", authType + "");
        String mPrivateKey = WalletUtil.getDecryptionKey(walletEntity.getmPrivateKey(), pwd);
        String key = AllUtils.createRandom(false, 8);
        String pa =  "&key=" + key + "&ver=" + PhoneUtil.getVersionName(context);
        String hexValue = WalletUtil.customSign(mPrivateKey, pa, walletEntity.getType());
        String publicKey = WalletUtil.getDecryptionKey(walletEntity.getmPublicKey(), pwd);
        if (TextUtils.isEmpty(hexValue)) {
            ToastUtil.showToast("" + mPrivateKey);
            return null;
        }
        params.put("key", key);
        params.put("address", walletEntity.getAllAddress());
        params.put("signature", hexValue);
        params.put("pubkey", publicKey);
        String coinType = "eth";
        switch (walletEntity.getType()) {
            case BTC_COIN:
                coinType = "btc";
                break;
            case XRP_COIN:
                coinType = "xrp";
                break;
            case TRX_COIN:
                coinType = "trx";
                break;
        }
        params.put("type", coinType);
        return params;
    }

    @Override
    public String getJsonRpc(int type) {
        String url = "";
        switch (type) {
            case ETH_COIN://1 eth
                url=HOST_JSONRPC_ETH;
                break;
            case BTC_COIN:// 2 btc
                url=HOST_JSONRPC_BTC;
                break;
            case EOS_COIN://3 eos
                break;
            case MCC_COIN://4 mcc
                url = HOST_QUOTES;
                break;
            case OTHER_COIN:
                break;
            case XRP_COIN://6 xrp 
                url=HOST_JSONRPC_XRP;
                break;
            case TRX_COIN://7 trx 
                url = HOST_TRX_QUOTES;
                break;
            case ETF_COIN://8  etf ETH ETH
                url=HOST_JSONRPC_ETF;
                break;
            case DMF_COIN://9  DMF ETH ETH   
                url=HOST_JSONRPC_DMF;
                break;
            case DMF_BA_COIN://9  DMF ETH ETH  
                url=HOST_JSONRPC_DMF;
                break;
            case HT_COIN:
                url=HOST_JSONRPC_HT;
                break;
            case BNB_COIN:
                url=HOST_JSONRPC_BNB;
                break;
            
            case FIL_COIN://Filecoin
                url=HOST_FIL;
                break;
            case DOGE_COIN://Dogecoin
                url=HOST_DOGE;
                break;
            case DOT_COIN://Polkadot
                url=HOST_DOT;
                break;
            case LTC_COIN://Litecoin
                url=HOST_LTC;
                break;
            case BCH_COIN://Bitcoin Cash
                url=HOST_BCH;
                break;
            case ZEC_COIN://Zcash
                url=HOST_ZEC;
                break;
            case ADA_COIN://Cardano
                break;
            case ETC_COIN://Ethereum Classic
                url=HOST_ETC;
                break;
            case SGB_COIN://Ethereum Classic
                url=HOST_SGB;
                break;
            case SOL_COIN://Ethereum Classic
                url=HOST_SOL;
                break;
            case MATIC_COIN://Ethereum Classic
                url=HOST_MATIC;
                break;
        }
        return url;
    }
}
