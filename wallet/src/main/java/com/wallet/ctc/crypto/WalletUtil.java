

package com.wallet.ctc.crypto;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BaseEthSignBean;
import com.wallet.ctc.model.blockchain.BaseGoWalletBean;
import com.wallet.ctc.model.blockchain.CreateEthBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.GoWalletBean;
import com.wallet.ctc.model.blockchain.TrustWalletBean;
import com.wallet.ctc.model.blockchain.XrpTrustBean;
import com.wallet.ctc.ui.blockchain.transfer.TransferActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferBTCActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferEthActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferTrxActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferXrpActivity;
import com.wallet.ctc.ui.dapp.util.DappJsToWeb;
import com.wallet.ctc.ui.dapp.util.SgbJsToWeb;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.Hex16;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import common.app.AppApplication;
import common.app.RxBus;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.utils.SpUtil;
import common.app.utils.digest.EAICoderUtil;
import owallet.DotSdk;
import owallet.MnemonicOut;
import owallet.Owallet;
import owallet.PrivateOut;
import wallet.core.jni.AnyAddress;
import wallet.core.jni.BitcoinAddress;
import wallet.core.jni.CoinType;
import wallet.core.jni.Curve;
import wallet.core.jni.Derivation;
import wallet.core.jni.HDWallet;
import wallet.core.jni.Hash;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.PublicKey;
import wallet.core.jni.StoredKey;



public class WalletUtil {
    private static final String TAG = "WalletUtil";
    private static Gson gson = new Gson();
    private final static String net = BuildConfig.BTC_NET;

    public static final int DM_COIN = 0;
    public static final int ETH_COIN = 1;
    public static final int BTC_COIN = 2;
    public static final int EOS_COIN = 3;
    public static final int MCC_COIN = 4;
    public static final int OTHER_COIN = 5;
    public static final int XRP_COIN = 6;
    public static final int TRX_COIN = 7;
    public static final int ETF_COIN = 8;
    public static final int DMF_COIN = 9;
    public static final int DMF_BA_COIN = 10;
    public static final int HT_COIN = 11;
    public static final int BNB_COIN = 12;

    
    public static final int FIL_COIN = 13;
    public static final int DOGE_COIN = 14;
    public static final int DOT_COIN = 15;
    public static final int LTC_COIN = 16;
    public static final int BCH_COIN = 17;
    public static final int ZEC_COIN = 18;
    public static final int ADA_COIN = 19;
    public static final int ETC_COIN = 20;
    public static final int SGB_COIN = 21;
    public static final int SOL_COIN = 22;
    public static final int MATIC_COIN = 23;

    public static final byte BTC_TEST = 111;

    public static void initAarSdk() {

        ChatSdk.startSdk(ChainsRpcsUtil.getChainIdMccStr());
    }

    public static DotSdk dotSdk;

    public static DotSdk getDotSdk() {
        if (null == dotSdk) {
            dotSdk = new DotSdk(BuildConfig.WS_SGB, 0);
        }
        return dotSdk;
    }

    
    public static WalletEntity CreatWallet(String pwd, int type) {
        WalletEntity walletEntity = null;
        HDWallet hdWallet = new HDWallet(128, "");
        if (type == SGB_COIN) {
            try {
                MnemonicOut mnemonicOut = getDotSdk().getAccount().generateByMnemonic(hdWallet.mnemonic());
                walletEntity = getSgbWalletBean(mnemonicOut, pwd);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {
            CoinType coinType = getCoinType(type);
            if (coinType == null) return null;
            walletEntity = getTrustWalletBean(hdWallet, coinType, pwd);
        }
        return walletEntity;
    }

    
    public static WalletEntity ImportWalletByMnemonic(List<String> mnemonic, String pwd, int type, int account) {
        String mnemonicStr = "";
        for (int i = 0; i < mnemonic.size(); i++) {
            mnemonicStr = mnemonicStr + mnemonic.get(i) + " ";
        }
        mnemonicStr = mnemonicStr.trim();
        WalletEntity walletEntity = null;
        if (type == SGB_COIN) {
            try {
                MnemonicOut mnemonicOut = getDotSdk().getAccount().generateByMnemonic(mnemonicStr);
                walletEntity = getSgbWalletBean(mnemonicOut, pwd);
            } catch (Exception e) {
                LogUtil.d("" + e.getMessage());
            }
        } else {
            CoinType coinType = getCoinType(type);
            if (coinType == null) return null;
            try {
                HDWallet hdWallet = new HDWallet(mnemonicStr, "");
                if (hdWallet == null) return null;
                walletEntity = getTrustWalletBean(hdWallet, coinType, pwd, account);
            } catch (Exception e) {
                return null;
            }
        }
        if (null != walletEntity) {
            walletEntity.setType(type);
        }
        return walletEntity;
    }

    
    public static WalletEntity ImportWalletByMnemonic(List<String> mnemonic, String pwd, int type) {
        return ImportWalletByMnemonic(mnemonic, pwd, type, 0);
    }

    
    public static WalletEntity changeWalletAddressPrefix(WalletEntity wallet) {
        String address = wallet.getAllAddress();
        String toAddress = "";
        if (type == WalletUtil.OTHER_COIN && !TextUtils.isEmpty(BuildConfig.ENABLE_OTHER_ADDRESS) && address.startsWith("0x")) {
            toAddress = BuildConfig.ENABLE_OTHER_ADDRESS + address.substring(2, address.length());
        } else if (type == WalletUtil.ETF_COIN && !TextUtils.isEmpty(BuildConfig.ENABLE_ETF_ADDRESS) && address.startsWith("0x")) {
            toAddress = BuildConfig.ENABLE_ETF_ADDRESS + address.substring(2, address.length());
        }
        if (!TextUtils.isEmpty(toAddress)) {
            wallet.setmAddress(toAddress);
        }
        return wallet;
    }

    
    public static WalletEntity ImportWalletByPrivateKey(String mPrivateKey, String pwd, int type) {
        WalletEntity walletEntity = null;
        if (type == SGB_COIN) {
            try {
                DotSdk dotSdk = getDotSdk();
                PrivateOut mnemonicOut = dotSdk.getAccount().generateByPriv(mPrivateKey);
                walletEntity = new WalletEntity();
                walletEntity.setmAddress(mnemonicOut.getAddress());
                walletEntity.setmPrivateKey(DecriptUtil.Encrypt(mnemonicOut.getPrivateKey(), pwd));
                walletEntity.setmPublicKey(mnemonicOut.getPublicKey().getBytes());
                walletEntity.setmPassword(DecriptUtil.MD5(pwd));
                walletEntity.setType(type);
            } catch (Exception e) {

            }
        } else {
            CoinType coinType = getCoinType(type);
            if (coinType == null) return null;
            try {
                TrustWalletBean trustWalletBean = new TrustWalletBean();
                byte[] priviteKey = Hex16.hexStringToByteArray(mPrivateKey);
                PrivateKey p = new PrivateKey(priviteKey);
                PublicKey pub = getPubKeyByPrivateKey(p, coinType);
                StoredKey storedKey = null;
                if (coinType.value() == CoinType.CARDANO.value() || coinType.value() == CoinType.BITCOIN.value() || coinType.value() == CoinType.XRP.value() || coinType.value() == CoinType.TRON.value()) {

                } else {
                    storedKey = StoredKey.importPrivateKey(priviteKey, "", pwd.getBytes(), coinType);
                }
                trustWalletBean.setPrivateKey(p);
                trustWalletBean.setPublicKey(pub);
                trustWalletBean.setStoredKey(storedKey);
                walletEntity = importTrustWalletBean(trustWalletBean, coinType, pwd);
                walletEntity.setType(type);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(e.getMessage());
                return null;
            }

        }
        return walletEntity;
    }

    
    public static WalletEntity ImportWalletByKeystore(String keystore, String pwd, String newPwd, int type) {
        WalletEntity walletEntity = null;
        switch (type) {
            case DM_COIN:
            case ETH_COIN:
            case MCC_COIN:
            case OTHER_COIN:
            case ETF_COIN:
            case DMF_COIN:
            case DMF_BA_COIN:
            case HT_COIN:
            case BNB_COIN:
            case EOS_COIN:
            case FIL_COIN://Filecoin
            case DOGE_COIN://Dogecoin
            case DOT_COIN://Polkadot
            case LTC_COIN://Litecoin
            case BCH_COIN://Bitcoin Cash
            case ZEC_COIN://Zcash
            case ETC_COIN://Ethereum Classic
            case SOL_COIN:
            case MATIC_COIN:
                CoinType coinType = getCoinType(type);
                if (coinType.value() == CoinType.CARDANO.value()) return null;
                if (coinType == null) return null;
                try {
                    TrustWalletBean trustWalletBean = new TrustWalletBean();
                    StoredKey storedKey = StoredKey.importJSON(keystore.getBytes());
                    if (storedKey == null) return null;
                    PrivateKey p = storedKey.privateKey(coinType, pwd.getBytes());
                    PublicKey pub = getPubKeyByPrivateKey(p, coinType);
                    trustWalletBean.setPrivateKey(p);
                    trustWalletBean.setPublicKey(pub);
                    trustWalletBean.setStoredKey(storedKey);
                    walletEntity = importTrustWalletBean(trustWalletBean, coinType, pwd);
                    walletEntity.setType(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return walletEntity;
    }

    
    public static String getTrandsSign(String trans, int type) {
        BaseEthSignBean bean = null;
        String data = "";
        switch (type) {
            case DM_COIN:
            case ETH_COIN:
            case MCC_COIN:
            case OTHER_COIN:
            case ETF_COIN:
            case DMF_COIN:
            case DMF_BA_COIN:
            case HT_COIN:
            case BNB_COIN:
                data = Owallet.createEthTransaction(trans);
                break;
            case BTC_COIN:
                data = Owallet.createBtcTransaction(trans);
                break;
            case XRP_COIN:
                data = Owallet.createXrpTransaction(trans);
                break;
            case TRX_COIN:
                data = Owallet.createTronTransaction(trans);
                ;
                break;
            case EOS_COIN:
            case FIL_COIN://Filecoin
            case DOGE_COIN://Dogecoin
            case DOT_COIN://Polkadot
            case LTC_COIN://Litecoin
            case BCH_COIN://Bitcoin Cash
            case ZEC_COIN://Zcash
            case ADA_COIN://Cardano
            case ETC_COIN://Ethereum Classic
            case SOL_COIN:
            case MATIC_COIN:

                break;
            default:
                data = "";
                break;
        }
        bean = gson.fromJson(data, BaseEthSignBean.class);
        return bean.getResult();
    }

    
    public static String getCreateEthTokenSign(CreateEthBean trans) {
        String data = Owallet.createEthToken(gson.toJson(trans));
        BaseEthSignBean bean = gson.fromJson(data, BaseEthSignBean.class);
        return bean.getResult();
    }

    
    private static CoinType getCoinType(int type) {
        switch (type) {
            case MCC_COIN:
                return CoinType.EVMOS;
            case DM_COIN:
            case OTHER_COIN:
            case ETF_COIN:
            case DMF_COIN:
            case DMF_BA_COIN:
            case HT_COIN:
            case BNB_COIN:
            case ETH_COIN:
                return CoinType.ETHEREUM;
            case BTC_COIN:
                return CoinType.BITCOIN;
            case TRX_COIN:
                return CoinType.TRON;
            case XRP_COIN:
                return CoinType.XRP;
            case EOS_COIN:
                return CoinType.EOS;
            case FIL_COIN:
                return CoinType.FILECOIN;
            case DOGE_COIN:
                return CoinType.DOGECOIN;
            case DOT_COIN:
                return CoinType.POLKADOT;
            case LTC_COIN:
                return CoinType.LITECOIN;
            case BCH_COIN:
                return CoinType.BITCOINCASH;
            case ZEC_COIN:
                return CoinType.ZCASH;
            case ADA_COIN:
                return CoinType.CARDANO;
            case ETC_COIN:
                return CoinType.ETHEREUMCLASSIC;
            case SOL_COIN:
                return CoinType.SOLANA;
            case MATIC_COIN:
                return CoinType.POLYGON;
        }
        return null;
    }

    
    private static WalletEntity getWalletBean(String data, String pwd) {
        WalletEntity WalletEntity = new WalletEntity();
        try {
            BaseGoWalletBean goWallet = gson.fromJson(data, BaseGoWalletBean.class);
            if (null != goWallet.getError() && goWallet.getError().length() > 0) {
                return null;
            }
            GoWalletBean goWalletBean = goWallet.getResult();
            if (null != goWalletBean.getMnemonic() && goWalletBean.getMnemonic().length() > 4) {
                String[] mnemonicArray = goWalletBean.getMnemonic().split(" ");
                List<String> mnemonic = Arrays.asList(mnemonicArray);
                String me = new Gson().toJson(mnemonic);
                String result = Owallet.getIdentifier(goWalletBean.getMnemonic());
                BaseEthSignBean bean = gson.fromJson(result, BaseEthSignBean.class);
                WalletEntity.setWalletId(bean.getResult());
                WalletEntity.setmMnemonic(DecriptUtil.Encrypt(me, pwd));
            }
            WalletEntity.setmAddress(goWalletBean.getAddress().getAddress());
            WalletEntity.setmPrivateKey(DecriptUtil.Encrypt(goWalletBean.getAddress().getPrivatekey(), pwd));
            WalletEntity.setmPublicKey(goWalletBean.getAddress().getPublickey().getBytes());
            WalletEntity.setmKeystore(DecriptUtil.Encrypt(new Gson().toJson(goWalletBean.getKeystore()), pwd));
            WalletEntity.setmPassword(DecriptUtil.MD5(pwd));
        } catch (Exception e) {
            return null;
        }
        return WalletEntity;
    }

    
    private static WalletEntity getSgbWalletBean(MnemonicOut goWalletBean, String pwd) {
        WalletEntity WalletEntity = new WalletEntity();
        try {
            if (null != goWalletBean.getMnemonic() && goWalletBean.getMnemonic().length() > 4) {
                String[] mnemonicArray = goWalletBean.getMnemonic().split(" ");
                List<String> mnemonic = Arrays.asList(mnemonicArray);
                String me = new Gson().toJson(mnemonic);
                String result = Owallet.getIdentifier(goWalletBean.getMnemonic());
                BaseEthSignBean bean = gson.fromJson(result, BaseEthSignBean.class);
                WalletEntity.setWalletId(bean.getResult());
                WalletEntity.setmMnemonic(DecriptUtil.Encrypt(me, pwd));
            }
            WalletEntity.setmAddress(goWalletBean.getAddress());
            WalletEntity.setmPrivateKey(DecriptUtil.Encrypt(goWalletBean.getPrivateKey(), pwd));
            WalletEntity.setmPublicKey(goWalletBean.getPublicKey().getBytes());
            WalletEntity.setmPassword(DecriptUtil.MD5(pwd));
            getSgbKetstory(goWalletBean.getMnemonic(), pwd);
        } catch (Exception e) {
             e.printStackTrace();
            return null;
        }

        return WalletEntity;
    }

    private static WebView webView;

    private static WebView getWebView() {
        if (null == webView) {
            
            webView = new WebView(AppApplication.getInstance().getApplicationContext());
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            settings.setUseWideViewPort(false);
            settings.setAllowFileAccess(true);
            settings.setBuiltInZoomControls(false);
            settings.setBlockNetworkImage(false);
            settings.setBlockNetworkLoads(false);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(false);
            settings.setAppCacheEnabled(false);
            webView.setWebChromeClient(new WebChromeClient() {
                                           @Override
                                           public void onProgressChanged(WebView view, int newProgress) {
                                               super.onProgressChanged(view, newProgress);
                                               if (newProgress == 100) {
                                                   if (type == 1) {
                                                       type = 0;
                                                       LogUtil.d("");
                                                       webView.loadUrl("javascript:keyring.recover('mnemonic', 'sr25519','" + m + "','" + p + "').then(\n" +
                                                               "                function(res) {\n" +
                                                               "                    trust.getKeystory(JSON.stringify(res),'" + p + "');\n" +
                                                               "                }).catch(\n" +
                                                               "                function(err) {\n" +
                                                               "                    trust.getKeystoryError(err,'" + m + "','" + p + "');\n" +
                                                               "                }\n" +
                                                               "            );");
                                                   } else if (type == 2) {
                                                       type = 0;
                                                       LogUtil.d("" + funType);
                                                       String js = "keyring.signTxAsExtension('" + p + "'," + req + ")";
                                                       if (funType.equalsIgnoreCase("pub(bytes.sign)")) {
                                                           js = "keyring.signBytesAsExtension('" + p + "'," + req + ")";
                                                       }
                                                       webView.loadUrl("javascript:keyring.initKeys([" + k + "],[0,2,27]).then(" +
                                                               "                function() {" +
                                                               "    trust.initSuccess();\n" +
                                                               js + ".then(\n" +
                                                               "                function(res) {\n" +
                                                               "                    trust.signResult(JSON.stringify(res),'" + funType + "');\n" +
                                                               "                }).catch(\n" +
                                                               "                function(err) {\n" +
                                                               "                    trust.signResult(err,'" + funType + "');\n" +
                                                               "                }\n" +
                                                               "            );" +
                                                               "               }).catch(" +
                                                               "                function(err) {" +
                                                               "                trust.initError(err); " +
                                                               "                }" +
                                                               "               );");
                                                   }
                                               }
                                           }
                                       }
            );
        }
        webView.addJavascriptInterface(new SgbJsToWeb(AppApplication.getContext(), webView), DappJsToWeb.METHOD_NAME);
        LogUtil.d("");
        webView.loadUrl("file:
        return webView;
    }

    
    private static int type = 0;
    private static String m;
    private static String p;

    public static void getSgbKetstory(String mnemonic, String pwd) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                
                type = 1;
                m = mnemonic;
                p = pwd;
                getWebView();
            }
        });
    }

    
    private static String k;
    private static String funType;
    private static String req;

    public static void getSgbSign(String funtype, String request, String pwd, String keystory) {
        funType = funtype;
        req = request;
        type = 2;
        k = keystory;
        p = pwd;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                getWebView();
            }
        });

    }

    private static WalletEntity getTrustWalletBean(HDWallet hdWallet, CoinType coinType, String pwd, int count) {
        WalletEntity WalletEntity = new WalletEntity();
        try {
            String mnemonicStr = hdWallet.mnemonic();
            if (null != hdWallet.mnemonic() && hdWallet.mnemonic().length() > 4) {
                String[] mnemonicArray = hdWallet.mnemonic().split(" ");
                List<String> mnemonic = Arrays.asList(mnemonicArray);
                String me = new Gson().toJson(mnemonic);
                WalletEntity.setmMnemonic(DecriptUtil.Encrypt(me, pwd));

                String walletAddr = hdWallet.getAddressForCoin(coinType);
                WalletEntity.setWalletId(walletAddr);
            }

            PrivateKey pKey = null;
            if (count > 0) {
                
                String derivationPath = createDerivationPath(coinType, count);
                pKey = hdWallet.getKey(coinType, derivationPath);
                WalletEntity.setDefwallet(count);
            } else {
                pKey = hdWallet.getKeyForCoin(coinType);
            }
            if (null == pKey) {
                Log.e(TAG, "pKey is null");
                return null;
            }



            String privateKey = Hex16.encodeHexStr(pKey.data());

            WalletEntity.setmPrivateKey(DecriptUtil.Encrypt(privateKey, pwd));
            PublicKey publicKey = getPubKeyByPrivateKey(pKey, coinType);

            
            AnyAddress anyAddress = new AnyAddress(publicKey, coinType);
            String address = anyAddress.description();
            WalletEntity.setmAddress(address);


            if (coinType.value() == CoinType.BITCOIN.value() || coinType.value() == CoinType.DOGECOIN.value() || coinType.value() == CoinType.LITECOIN.value() || coinType.value() == CoinType.BITCOINCASH.value() || coinType.value() == CoinType.ZCASH.value()) {
                String pub = "";
                if (count > 0) {
                    pub = hdWallet.getExtendedPublicKeyAccount(coinType.purpose(), coinType, Derivation.DEFAULT, coinType.xpubVersion(), count);
                } else {
                    pub = hdWallet.getExtendedPublicKey(coinType.purpose(), coinType, coinType.xpubVersion());
                }
                WalletEntity.setmPublicKey(pub.getBytes());
            } else if (null != publicKey) {
                WalletEntity.setmPublicKey(HexUtils.toHex(publicKey.data()).getBytes());
            }
            if (coinType == CoinType.EVMOS) {
                WalletEntity = setEvmosAddr(WalletEntity, privateKey, coinType);
            }
            StoredKey storedKey = null;
            String keystory = "";
            if (coinType.value() == CoinType.CARDANO.value() || coinType.value() == CoinType.BITCOIN.value() || coinType.value() == CoinType.XRP.value()) {

            } else {
                storedKey = StoredKey.importPrivateKey(pKey.data(), "", pwd.getBytes(), coinType);
            }
            if (storedKey != null && storedKey.exportJSON() != null) {
                keystory = new String(storedKey.exportJSON());
                WalletEntity.setmKeystore(DecriptUtil.Encrypt(keystory, pwd));
            }
            if (coinType.value() == CoinType.BITCOINCASH.value()) {
                
                String b = new BitcoinAddress(publicKey, coinType.p2pkhPrefix()).description();
                String c = new BitcoinAddress(publicKey, BTC_TEST).description();
                String d = new BitcoinAddress(publicKey, coinType.p2shPrefix()).description();
                LogUtil.d("" + address + "    address" + b + "    " + c + "       " + d);
                address = new BitcoinAddress(publicKey, coinType.p2pkhPrefix()).description();
                WalletEntity.setmAddress(address);
            }
            
            if (coinType.value() == CoinType.BITCOIN.value()) {
                address = new BitcoinAddress(publicKey, BTC_TEST).description();
                WalletEntity.setmAddress(address);
            }
            WalletEntity.setmPassword(DecriptUtil.MD5(pwd));

            
            WalletEntity = WalletUtil.changeWalletAddressPrefix(WalletEntity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("WalletUtil", "createTrust null=" + e.getMessage());
            return null;
        }

        return WalletEntity;
    }

    private static WalletEntity getTrustWalletBean(HDWallet hdWallet, CoinType coinType, String pwd) {
        return getTrustWalletBean(hdWallet, coinType, pwd, 0);
    }


    
    private static String createDerivationPath(CoinType coinType, int acount) {
        
        String defaultDerivationPath = coinType.derivationPath();
        if (!TextUtils.isEmpty(defaultDerivationPath)) {
            int index = defaultDerivationPath.lastIndexOf("/");
            if (index > 0) {
                String startStr = defaultDerivationPath.substring(0, index + 1);
                return startStr + acount;
            }
        }
        return null;
    }

    private static WalletEntity setEvmosAddr(WalletEntity wallet, String privateKey, CoinType coinType) {
        if (TextUtils.isEmpty(privateKey) || coinType != CoinType.EVMOS) {
            return wallet;
        }
        
        
        String json = new String(ChatSdk.createAddress(privateKey));
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("Data")) {
                    jsonObject = jsonObject.getJSONObject("Data");
                } else if (jsonObject.has("data")) {
                    jsonObject = jsonObject.getJSONObject("data");
                }
                if (jsonObject.has("cosmos_address")) {
                    String address2 = jsonObject.getString("cosmos_address");
                    LogUtil.i("cosmos_address=" + address2);
                    wallet.setmAddress2(address2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        
        String chatPrivateKey = EAICoderUtil.getSha256Code(privateKey);
        String sha256Json = new String(ChatSdk.createAddress(chatPrivateKey));

        if (!TextUtils.isEmpty(sha256Json)) {
            wallet.setChatPrivateKey(chatPrivateKey);
            try {
                JSONObject jsonObject = new JSONObject(sha256Json);
                if (jsonObject.has("Data")) {
                    jsonObject = jsonObject.getJSONObject("Data");
                } else if (jsonObject.has("data")) {
                    jsonObject = jsonObject.getJSONObject("data");
                }
                if (jsonObject.has("cosmos_address")) {
                    String evmosAddr = jsonObject.getString("cosmos_address");
                    wallet.setChatAddress(evmosAddr);
                }
                if (jsonObject.has("eth_address")) {
                    String ethAddr = jsonObject.getString("eth_address");
                    wallet.setChatEthAddress(ethAddr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return wallet;
    }

    private static PublicKey getPubKeyByPrivateKey(PrivateKey privateKey, CoinType coinType) {

        if (coinType.value() == CoinType.FILECOIN.value() || coinType.value() == CoinType.ETHEREUMCLASSIC.value() ||
                coinType.value() == CoinType.ETHEREUM.value() || coinType.value() == CoinType.TRON.value() ||
                coinType.value() == CoinType.SOLANA.value() || coinType.value() == CoinType.POLYGON.value() ||
                coinType.value() == CoinType.EVMOS.value()) {
            return privateKey.getPublicKeySecp256k1(false);
        } else if (coinType.value() == CoinType.XRP.value() || coinType.value() == CoinType.BITCOIN.value() || coinType.value() == CoinType.DOGECOIN.value() || coinType.value() == CoinType.LITECOIN.value() || coinType.value() == CoinType.BITCOINCASH.value() || coinType.value() == CoinType.ZCASH.value() || coinType.value() == CoinType.EOS.value()) {
            return privateKey.getPublicKeySecp256k1(true);
        } else if (coinType.value() == CoinType.POLKADOT.value()) {
            return privateKey.getPublicKeyEd25519();
        } else if (coinType.value() == CoinType.CARDANO.value()) {
            return privateKey.getPublicKeyEd25519Cardano();
        }
        return null;
    }

    private static WalletEntity importTrustWalletBean(TrustWalletBean trustWalletBean, CoinType coinType, String pwd) {
        WalletEntity WalletEntity = new WalletEntity();
        try {
            if (null != trustWalletBean.getMnemonic() && trustWalletBean.getMnemonic().length() > 4) {
                String[] mnemonicArray = trustWalletBean.getMnemonic().split(" ");
                List<String> mnemonic = Arrays.asList(mnemonicArray);
                String me = new Gson().toJson(mnemonic);
                String result = Owallet.getIdentifier(trustWalletBean.getMnemonic());
                BaseEthSignBean bean = gson.fromJson(result, BaseEthSignBean.class);
                WalletEntity.setWalletId(bean.getResult());
                WalletEntity.setmMnemonic(DecriptUtil.Encrypt(me, pwd));
            }
            String privateKey = Hex16.encodeHexStr(trustWalletBean.getPrivateKey().data());
            if (coinType == CoinType.EVMOS) {
                WalletEntity = setEvmosAddr(WalletEntity, privateKey, coinType);
            }
            AnyAddress anyAddress = new AnyAddress(trustWalletBean.getPublicKey(), coinType);
            WalletEntity.setmAddress(anyAddress.description());
            WalletEntity.setmPrivateKey(DecriptUtil.Encrypt(Hex16.encodeHexStr(trustWalletBean.getPrivateKey().data()), pwd));
            WalletEntity.setmPublicKey(HexUtils.toHex(trustWalletBean.getPublicKey().data()).getBytes());
            if (trustWalletBean.getStoredKey() != null && trustWalletBean.getStoredKey().exportJSON() != null) {
                WalletEntity.setmKeystore(DecriptUtil.Encrypt(new String(trustWalletBean.getStoredKey().exportJSON()), pwd));
            }
            WalletEntity.setmPassword(DecriptUtil.MD5(pwd));

            
            WalletEntity = changeWalletAddressPrefix(WalletEntity);
        } catch (Exception e) {
            Log.d("WalletUtil", "importTrust ex=" + e.getMessage());
            return null;
        }
        return WalletEntity;
    }

    public static String trusXrpToken(XrpTrustBean trustBean) {
        LogUtil.d(gson.toJson(trustBean));
        String data = Owallet.createXrpTrust(gson.toJson(trustBean));
        BaseEthSignBean bean = gson.fromJson(data, BaseEthSignBean.class);
        LogUtil.d(data);
        return bean.getResult();
    }

    
    public static String customSign(String keystore, String params, int type) {
        byte[] message = params.getBytes(StandardCharsets.UTF_8);
        byte[] data = Hash.keccak256(message);
        PrivateKey privateKey = new PrivateKey(Numeric.hexStringToByteArray(keystore));
        byte[] signatureData = privateKey.sign(data, Curve.SECP256K1);
        return Numeric.toHexStringNoPrefix(signatureData);
    }

    
    public static String cosmosSign(String privateKey, String publickey, String address, String params, int type) {

        if (!TextUtils.isEmpty(privateKey) && privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }

        JSONObject json = new JSONObject();
        try {
            json.put("address", address);
            json.put("publickey", publickey);
            json.put("privatekey", privateKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = json.toString();
        
        ChatSdk.setupCosmosWallet(jsonStr.getBytes(StandardCharsets.UTF_8));
        String signResult = ChatSdk.sign(params);
        try {
            EvmosSignResult result = new Gson().fromJson(signResult, EvmosSignResult.class);
            if (result.isSuccess() && !TextUtils.isEmpty(result.Data)) {
                return result.Data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static void changeWalletPwd(Context mContext, WalletEntity WalletEntity, String oldpwd, String pwd) {

        
        boolean pwdChangeSuccess = false;
        byte[] privateKeyByteArray = WalletEntity.getmPrivateKey();
        if (privateKeyByteArray != null && privateKeyByteArray.length > 0) {
            String privtekey = getDecryptionKey(privateKeyByteArray, oldpwd);
            if (!TextUtils.isEmpty(privtekey)) {
                
                WalletEntity.setmPrivateKey(DecriptUtil.Encrypt(privtekey, pwd));
                pwdChangeSuccess = true;
            }
        }

        
        byte[] mnemonicByteArray = WalletEntity.getmMnemonic();
        if (mnemonicByteArray != null && mnemonicByteArray.length > 0) {
            String zhujici = getDecryptionKey(mnemonicByteArray, oldpwd);
            if (!TextUtils.isEmpty(zhujici)) {
                
                WalletEntity.setmMnemonic(DecriptUtil.Encrypt(zhujici, pwd));
            }
        }

        
        byte[] keystoreByteArray = WalletEntity.getmKeystore();
        if (keystoreByteArray != null && keystoreByteArray.length > 0) {
            String keystore = getDecryptionKey(keystoreByteArray, oldpwd);
            if (!TextUtils.isEmpty(keystore)) {
                
                WalletEntity.setmKeystore(DecriptUtil.Encrypt(keystore, pwd));
            }
        }

        if (pwdChangeSuccess) {
            
            WalletEntity.setmPassword(DecriptUtil.MD5(pwd));
            DBManager.getInstance(mContext).updateWallet(new Gson().fromJson(new Gson().toJson(WalletEntity), WalletEntity.class));
        }



    }


    
    public static String getDecryptionKey(byte[] bytes, String pwd) {
        return DecriptUtil.Decrypt(bytes, pwd);
    }

    public static String provideEncryptionPrivateKey(String privateKey) {
        return DecriptUtil.MD5("privateKey" + privateKey + "privateKey");
    }

    
    public static String getCosmosCompressPublickey(String hexPrivateKey) {
        PrivateKey privateKey1 = new PrivateKey(Numeric.hexStringToByteArray(hexPrivateKey));
        String publickey = HexUtils.toHex(privateKey1.getPublicKeySecp256k1(true).data());
        return publickey;
    }

    
    public static String getBase58Address(String address) {
        String data = Owallet.tronHex2Addr(address);
        BaseEthSignBean bean = gson.fromJson(data, BaseEthSignBean.class);
        return bean.getResult();
    }

    
    public static AssertBean getdes(String tokenName, List<AssertBean> list, List<AssertBean> mustC) {
        if (TextUtils.isEmpty(tokenName)) {
            tokenName = "";
        }
        AssertBean dec = null;
        List<AssertBean> lists = new ArrayList<>();
        lists.addAll(list);
        lists.addAll(mustC);
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getShort_name().toLowerCase().equals(tokenName.toLowerCase())) {
                dec = lists.get(i);
                break;
            }
            if (lists.get(i).getContract().toLowerCase().equals(tokenName.toLowerCase())) {
                dec = lists.get(i);
                break;
            }
        }
        return dec;
    }

    
    public static void goTrans(Context context, AssertBean dex, String amountStr, String toAddress) {
        Intent intent;
        if (dex == null) {
            ToastUtil.showToast(context.getString(R.string.wallet_not_support));
            return;
        } else if (dex.getType() == DM_COIN) {
            intent = new Intent(context, TransferActivity.class);
            intent.putExtra("tokenName", dex.getShort_name().toUpperCase());
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("type", dex.getType());
        } else if (dex.getType() == ETH_COIN || dex.getType() == ETF_COIN || dex.getType() == DMF_BA_COIN || dex.getType() == DMF_COIN || dex.getType() == HT_COIN || dex.getType() == BNB_COIN) {
            intent = new Intent(context, TransferEthActivity.class);
            intent.putExtra("tokenName", dex.getShort_name().toUpperCase());
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("tokenType", dex.getContract());
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("decimal", dex.getDecimal());
            intent.putExtra("gasCount", dex.getGas());
        } else if (dex.getType() == BTC_COIN) {
            intent = new Intent(context, TransferBTCActivity.class);
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("tokenName", dex.getShort_name().toUpperCase());
        } else if (dex.getType() == XRP_COIN) {
            intent = new Intent(context, TransferXrpActivity.class);
            intent.putExtra("tokenName", dex.getShort_name().toUpperCase());
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("tokenType", dex.getContract());
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("decimal", dex.getDecimal());
            intent.putExtra("gasCount", dex.getGas());
        } else if (dex.getType() == MCC_COIN || dex.getType() == OTHER_COIN) {
            intent = new Intent(context, TransferActivity.class);
            intent.putExtra("tokenName", dex.getShort_name());
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("type", dex.getType());
        } else if (dex.getType() == TRX_COIN) {
            intent = new Intent(context, TransferTrxActivity.class);
            intent.putExtra("tokenName", dex.getShort_name().toUpperCase());
            intent.putExtra("amountStr", amountStr);
            intent.putExtra("tokenType", dex.getContract());
            intent.putExtra("toAddress", toAddress);
            intent.putExtra("decimal", dex.getDecimal());
            intent.putExtra("gasCount", dex.getGas());
        } else {
            return;
        }
        context.startActivity(intent);
    }

    public static boolean isWalletAddress(int type, String address) {
        if (type == MCC_COIN) {
            if (TextUtils.isEmpty(address) || address.length() < 15 || !address.startsWith(BuildConfig.ENABLE_MCC_ADDRESS)) {
                return false;
            } else {
                return true;
            }
        }
        return AnyAddress.isValid(address, getCoinType(type));
    }

    public static String getNftChain(int type) {
        if (type == ETH_COIN) {
            return BuildConfig.CHAIN_ETH;
        } else if (type == BNB_COIN) {
            return BuildConfig.CHAIN_BSC;
        } else {
            return "";
        }
    }

    
    public static String getSignAddress(String address) {
        if (null == address || address.length() < 10) {
            return address + "";
        }
        String walletAddress = address.substring(0, 10);
        int len = address.length();
        walletAddress = walletAddress + "..." + address.substring(len - 10, len);
        return walletAddress;
    }


    
    public static String getApproveData(String spenderContractAddress, String bigAmount) {
        
        
        BigInteger _amount = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        String FUNC_BURNTOKEN = "approve"; 
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Address(spenderContractAddress), new Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return data;
    }


    
    public static String getSwapDstCrossChain(String tokenContract, String dstBigAmount, String toAddress) {
        
        BigInteger _amount = new BigInteger(dstBigAmount);
        String FUNC_BURNTOKEN = "swapDstCrossChain"; 
        byte[] key32Array = getByte32Key();
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Address(tokenContract), new Uint256(_amount), new Utf8String(toAddress), new Bytes32(key32Array)), 
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return data;
    }

    
    public static AssertBean getUsdtAssert(int walletType) {
        if (walletType == MCC_COIN) {
            return Constants.getUSDTEvmosAssets();
        } else if (walletType == BNB_COIN) {
            return Constants.getUSDTBscAssets();
        } else if (walletType == ETH_COIN) {
            return Constants.getUSDTEthAssets();
        }
        return null;
    }

    
    public static AssertBean getMainChainAssert(Context context, int walletType) {
        List<AssertBean> mustAsset = WalletDBUtil.getInstent(context).getMustWallet(walletType);
        if (null != mustAsset && mustAsset.size() > 0) {
            return mustAsset.get(0);
        }
        return null;
    }


    
    public static String getChainBridgeContract(int walletType) {
        if (walletType == MCC_COIN) {
            return Constants.CHAIN_BRIDGE_EVMOS_CONTRACT;
        } else if (walletType == BNB_COIN) {
            return Constants.CHAIN_BRIDGE_BSC_CONTRACT;
        } else if (walletType == ETH_COIN) {
            return Constants.CHAIN_BRIDGE_ETH_CONTRACT;
        }
        return null;
    }

    
    public static String getSmartRpcUrl(int walletType) {
        if (walletType == MCC_COIN) {
            return SpUtil.getNodeSmartUrl();
        } else {
            return SpUtil.getDefNode(walletType);
        }
    }

    public static byte[] getByte32Key() {
        Random random = new Random();
        byte[] array = new byte[32];
        random.nextBytes(array);
        return array;
    }

    
    public static void saveNodeInfo(int walletType, String nodeUrl, String noSegm, String nodeName, String imUrl,
                                    String nodeSmartUrl, String nodeInfoUrl, String chatCall, String chain_id, String ws_url, String tts_url) {
        SpUtil.saveNodeInfo(walletType, nodeUrl, noSegm, nodeName, imUrl, nodeSmartUrl, nodeInfoUrl, chatCall, chain_id, ws_url, tts_url);

        
        notifyNodeChangeNotice();
    }

    
    public static void saveDefNode(int walletType, String rpcUrl) {
        SpUtil.saveDefNode(walletType, rpcUrl);

        
        notifyNodeChangeNotice();
    }

    
    public static void notifyNodeChangeNotice() {
        RxBus.getInstance().post(new RxNotice(RxNotice.MSG_RPC_NODE_CHANGE));
    }
}
