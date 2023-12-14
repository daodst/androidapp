

package com.wallet.ctc.ui.dapp;

import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static common.app.my.RxNotice.MSG_WALLET_NUM_CHANGE;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChainsRpcsUtil;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.DappResult;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.SgbDappBean;
import com.wallet.ctc.model.blockchain.SgbDappWalletBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.TrxDappBean;
import com.wallet.ctc.model.blockchain.TrxDappValueBean;
import com.wallet.ctc.model.blockchain.TrxPayInfoBean;
import com.wallet.ctc.model.blockchain.TrxRawData;
import com.wallet.ctc.model.blockchain.TrxTransSignBean;
import com.wallet.ctc.model.blockchain.TrxTransactionPushBean;
import com.wallet.ctc.ui.dapp.util.DappFuncation;
import com.wallet.ctc.ui.dapp.util.DappJsToWeb;
import com.wallet.ctc.ui.dapp.util.DappUtil;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.LoadingDialog;

import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.app.AppApplication;
import common.app.RxBus;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.ui.view.InputPwdDialog;
import owallet.Owallet;
import wallet.core.jni.Curve;
import wallet.core.jni.Hash;
import wallet.core.jni.PrivateKey;



public class DappFuncationImpl implements DappFuncation {
    private static final String TAG = "Dapp";
    public LoadingDialog mLoadingDialog;
    private WalletTransctionUtil trxTransctionListen;
    private WalletTransctionUtil walletTransctionUtil;
    private WalletTransctionUtil sgbTransctionListen;
    private BigDecimal gasCount = new BigDecimal("0");
    private BigDecimal gasprice;
    private double num = 4;
    private String feiyongStr = "--";
    private WalletEntity walletEntity;
    private String toAddress = "";
    private String tokentype = "";
    private int decimal = 18;
    private String amountStr;
    private String TokenName = "ETH";
    private String signData;
    private String reqid;
    private Context mContext;
    private DappJsToWeb dappJsToWeb;

    public DappFuncationImpl(Context mContext) {
        this.mContext = mContext;
        mLoadingDialog = new LoadingDialog(mContext, mContext.getString(R.string.loading));
        trxTransctionListen = new WalletTransctionUtil(mContext);
        trxTransctionListen.setTrxTransctionListen(new WalletTransctionUtil.TrxTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                LogUtil.d(""+hash);
                dappJsToWeb.trxSignResult(new DappResult(true, hash));
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
                dappJsToWeb.trxSignResult(new DappResult(false, "cancle"));
            }
        });
        sgbTransctionListen = new WalletTransctionUtil(mContext);
        sgbTransctionListen.setSgbTransctionListen(new WalletTransctionUtil.SgbTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String hash) {

            }
            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
                dappJsToWeb.trxSignResult(new DappResult(false, "cancle"));
            }
        });

        walletTransctionUtil = new WalletTransctionUtil(mContext);
        walletTransctionUtil.setOnTransctionListen(new WalletTransctionUtil.TransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showGasCount(String gasc) {
                gasCount = new BigDecimal(gasc);
            }

            @Override
            public void showGasprice(GasPriceBean bean) {
            }

            @Override
            public void showDefGasprice(String defGasprice) {
                mLoadingDialog.dismiss();
                num = new BigDecimal(defGasprice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                gasprice = new BigDecimal(defGasprice).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("1000000000"));
                getprice(gasprice);
                TransferBean data = new TransferBean(toAddress, getWalletAddr(walletEntity), amountStr, feiyongStr.replace("ether", ""), walletEntity.getType(), TokenName, feiyongStr);
                data.setPayaddress(getWalletAddr(walletEntity));
                data.setPrice(amountStr);
                data.setTokenName(TokenName);
                data.setRemark("Dapp");
                data.setKuanggong(feiyongStr);
                data.setGascount(gasCount.intValue());
                data.setGasprice(num + "");
                data.setRuaddress(toAddress);
                data.setTokenType(tokentype);
                data.setDecimal(decimal);
                data.setData(signData);

                if (!TextUtils.isEmpty(feiyongStr) && new BigDecimal(feiyongStr).doubleValue() > 0) {
                    
                    walletTransctionUtil.getBalance(walletEntity).subscribe(new NextSubscriber<String>() {
                        @Override
                        public void dealData(String balance) {
                            if (!TextUtils.isEmpty(balance)) {
                                if (new BigDecimal(feiyongStr).compareTo(new BigDecimal(balance)) > 0) {
                                    dappJsToWeb.ethTransactionResult(reqid, new DappResult(false, AppApplication.getContext().getString(R.string.dapp_no_balance_error)));
                                    ToastUtil.showToast(AppApplication.getContext().getString(R.string.dapp_no_balance_error));
                                    return;
                                } else {
                                    
                                    walletTransctionUtil.DoTransctionDapp(data, walletEntity.getType());
                                }
                            } else {
                                
                                walletTransctionUtil.DoTransctionDapp(data, walletEntity.getType());
                            }
                        }

                        @Override
                        protected void dealError(Throwable e) {
                            e.printStackTrace();
                            ToastUtil.showToast(e.getMessage());
                        }
                    });
                } else {
                    
                    walletTransctionUtil.DoTransctionDapp(data, walletEntity.getType());
                }

                

            }

            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                dappJsToWeb.ethTransactionResult(reqid, new DappResult(true, hash));
                ToastUtil.showToast(mContext.getString(R.string.caozuo_success));
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                dappJsToWeb.ethTransactionResult(reqid, new DappResult(false, "Canceled"));
                ToastUtil.showToast(msg);

            }

            @Override
            public void showEip1559(String baseFeePerGas) {

            }
        });
    }

    
    private String getWalletAddr(WalletEntity wallet) {
        return DappUtil.getAddress(wallet);
    }

    @Override
    public void setJsToWeb(DappJsToWeb dappJsToWeb) {
        this.dappJsToWeb = dappJsToWeb;
    }

    @Override
    public DappJsToWeb getJsToWeb() {
        return dappJsToWeb;
    }

    @Override
    public void getTrxSign(String data) {
        LogUtil.d(""+data);
        
        TrxDappBean trxDappBean = new Gson().fromJson(data, TrxDappBean.class);
        try {
            TrxDappValueBean valueBean;
            TrxRawData.ContractBean contractBean;
            TrxRawData rawData;
            TrxPayInfoBean trxPayInfoBean;
            long time;
            long expiration;
            if (null != trxDappBean && null != trxDappBean.getTransaction()) {
                rawData = trxDappBean.getTransaction().getRaw_data();
                contractBean = rawData.getContract().get(0);
                time = trxDappBean.getTransaction().getRaw_data().getTimestamp();
                expiration = trxDappBean.getTransaction().getRaw_data().getExpiration();
                trxPayInfoBean=trxDappBean.getTransaction().getPayInfo();
            } else {
                TrxTransactionPushBean transactionBean = new Gson().fromJson(data, TrxTransactionPushBean.class);
                rawData = transactionBean.getRaw_data();
                contractBean = rawData.getContract().get(0);
                time = transactionBean.getRaw_data().getTimestamp();
                expiration = transactionBean.getRaw_data().getExpiration();
                trxPayInfoBean=transactionBean.getPayInfo();
            }
            valueBean = contractBean.getParameter().getValue();
            String toAddress = toBase58(valueBean.getTo_address());
            String fromAddress = toBase58(valueBean.getOwner_address());
            String contract = toBase58(valueBean.getContract_address());

            if (contractBean.getType().equalsIgnoreCase("TransferContract")) {
                String amountStr = "";
                int decmail = 18;
                if (TextUtils.isEmpty(valueBean.getContract_address())) {
                    decmail = 6;
                    amountStr = new BigDecimal(valueBean.getAmount()).divide(new BigDecimal(Math.pow(10, 6)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                }
                TransferBean mBeam = new TransferBean(toAddress, fromAddress, amountStr, TRX_COIN, "", contract, decmail);
                mBeam.setTimestamp(time);
                mBeam.setInfo(contractBean.getType());
                mBeam.setExpiration(expiration);
                trxTransctionListen.DoTrxTransctionDapp(mBeam, TRX_COIN);
            } else if (contractBean.getType().equalsIgnoreCase("TriggerSmartContract") && !TextUtils.isEmpty(valueBean.getData())) {
                TransferBean mBeam = new TransferBean(toAddress, fromAddress, valueBean.getAmount() + "", TRX_COIN, "", contract, 18);
                mBeam.setData(valueBean.getData());
                mBeam.setTimestamp(time);
                mBeam.setInfo(contractBean.getType());
                mBeam.setExpiration(expiration);
                trxTransctionListen.DoTrxTransctionDapp(mBeam, TRX_COIN);
            }  else if (contractBean.getType().equalsIgnoreCase("FreezeBalanceContract")) {
                TransferBean mBeam = new TransferBean(toAddress, fromAddress, valueBean.getAmount() + "", TRX_COIN, "", contract, 18);
                mBeam.setTimestamp(time);
                mBeam.setInfo(contractBean.getType());
                mBeam.setExpiration(expiration);
                mBeam.setFrozen_balance(trxPayInfoBean.getFrozen_balance());
                mBeam.setFrozen_duration(trxPayInfoBean.getFrozen_duration());
                mBeam.setReceiver_address(trxPayInfoBean.getReceiver_address());
                mBeam.setResource( toBase58(trxPayInfoBean.getResource()));
                trxTransctionListen.DoTrxTransctionDapp(mBeam, TRX_COIN);
            }else if (contractBean.getType().equalsIgnoreCase("UnfreezeBalanceContract")) {
                TransferBean mBeam = new TransferBean(toAddress, fromAddress, valueBean.getAmount() + "", TRX_COIN, "", contract, 18);
                mBeam.setTimestamp(time);
                mBeam.setInfo(contractBean.getType());
                mBeam.setExpiration(expiration);
                mBeam.setReceiver_address(trxPayInfoBean.getReceiver_address());
                mBeam.setResource( toBase58(trxPayInfoBean.getResource()));
                trxTransctionListen.DoTrxTransctionDapp(mBeam, TRX_COIN);
            } else {
                dappJsToWeb.trxSignResult(new DappResult(false, "cancle"));
            }
        } catch (Exception e) {
            common.app.utils.LogUtil.d("zzz", e.getMessage());
        }
    }

    @Override
    public void sendEthTransaction(String callbackId, String to, String value, String nonce, String gasLimit, String gasPrice, String data) {
        walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfo();
        reqid = callbackId;
        toAddress = to;
        signData = data;
        decimal = 18;
        tokentype = "";
        TokenName = "ETH";
        if (!TextUtils.isEmpty(gasLimit)) {
            if (gasLimit.startsWith("0x")) {
                gasLimit = gasLimit.substring(2);
            }
            BigInteger gas = new BigInteger(gasLimit, 16);
            gasCount = new BigDecimal(gas);
        }
        String qianzhu = "0xa9059cbb";
        
        if (!TextUtils.isEmpty(data) && !data.startsWith(qianzhu)) {
            
            if (!TextUtils.isEmpty(value) && value.length() > 2) {
                if (value.startsWith("0x")) {
                    value = value.substring(2);
                }
                AssertBean assertBean = WalletDBUtil.getInstent(mContext).getAssetsByAddress(getWalletAddr(walletEntity), to, walletEntity.getType());
                if (null != assertBean) {
                    decimal = assertBean.getDecimal();
                    TokenName = assertBean.getShort_name().toUpperCase();
                    LogUtil.d("" + decimal + "" + value);
                }
                BigInteger amount = new BigInteger(value, 16);
                amountStr = new BigDecimal(amount).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            } else {
                amountStr = "";
            }
            tokentype = to;
            if (null == gasCount || gasCount.longValue() == 0) {
                walletTransctionUtil.getEthGas(getWalletAddr(walletEntity), tokentype, walletEntity.getType(), data, value);
            } else {
                walletTransctionUtil.getEthDefPrice(walletEntity.getType());
            }
        } else {
            String mainCoinValue = value;
            if (TextUtils.isEmpty(data) || data.equals("0x") || data.length() < 5) {
                if (value.startsWith("0x")) {
                    value = value.substring(2);
                }
                BigInteger amount = new BigInteger(value, 16);
                amountStr = amount.toString();
                amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            } else {
                String address = data.substring(qianzhu.length(), qianzhu.length() + 64);
                toAddress = "0x" + address.substring(24);
                value = data.substring(qianzhu.length() + 64);
                BigInteger othamount = new BigInteger(value, 16);
                amountStr = othamount.toString();
                tokentype = to;
                LogUtil.d(toAddress + "    " + amountStr + "    " + tokentype + "   \n" + data);
                AssertBean assertBean = WalletDBUtil.getInstent(mContext).getAssetsByAddress(getWalletAddr(walletEntity), tokentype, walletEntity.getType());
                if (null != assertBean) {
                    decimal = assertBean.getDecimal();
                    TokenName = assertBean.getShort_name().toUpperCase();
                    LogUtil.d("" + decimal + "" + amountStr);
                    amountStr = new BigDecimal(amountStr).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                } else {
                    amountStr = "";
                }
            }
            walletTransctionUtil.getEthGas(getWalletAddr(walletEntity), tokentype, walletEntity.getType(), data, mainCoinValue);
        }
    }

    @Override
    public void switchEthereumChain(String id, String address, String chainId) {
        int chain = HexUtils.hexStrtoTen(chainId);
        int toWalletType = ChainsRpcsUtil.chainIdToWalletType(chain);
        if (toWalletType != -1) {
            
            WalletEntity walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfo();

            
            WalletEntity targetWalletEntity = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(address, toWalletType);

            if (null == targetWalletEntity) {
                
                
                Log.e(TAG, "no fund this type wallet, please create wallet first.");
                String error = mContext.getString(R.string.switch_no_wallet_tip);
                dappJsToWeb.ethTransactionResult(id, new DappResult(false, error));
                return;
            }
            if (null != walletEntity && walletEntity.getType() == toWalletType) {
                
                Log.w(TAG, "has change this chain wallet");
                String rpcUrl = ChainsRpcsUtil.getRpcUrlByChainid(chain);
                dappJsToWeb.setConfig(address, rpcUrl, chain+"");
                dappJsToWeb.emitChainChanged(chainId);
                dappJsToWeb.ethTransactionResult(id, new DappResult(true, ""));
                return;
            }

            
            SettingPrefUtil.setWalletTypeAddress(mContext, toWalletType, address);
            String rpcUrl = ChainsRpcsUtil.getRpcUrlByChainid(chain);
            
            if (!TextUtils.isEmpty(rpcUrl)) {
                dappJsToWeb.setConfig(address, rpcUrl, chain+"");
                dappJsToWeb.emitChainChanged(chainId);
                dappJsToWeb.ethTransactionResult(id, new DappResult(true, ""));
            } else {
                Log.e(TAG, "rpcUrl is null chainid :"+chainId);
                dappJsToWeb.ethTransactionResult(id, new DappResult(false, "unkown chainid :"+chainId));
            }
        } else {
            
            
            Log.e(TAG, "unkown chainId:"+chainId);
            String error = mContext.getResources().getString(R.string.no_support_this_chain)+chainId;
            dappJsToWeb.ethTransactionResult(id, new DappResult(false, error));
        }
    }

    @Override
    public void addEthereumChain(String id, String address, String chainId, String rpcUrls, String symbol) {
        LogUtil.d(symbol);
        int chain = HexUtils.hexStrtoTen(chainId);
        int wallettype = ChainsRpcsUtil.chainIdToWalletType(chain);
        if (wallettype == -1 || wallettype == MCC_COIN) {
            
            Log.e(TAG, "no fund this chainid :"+chainId);
            String error = mContext.getResources().getString(R.string.no_support_this_chain)+chainId;
            dappJsToWeb.ethTransactionResult(id, new DappResult(false, error));
            return;
        }
        WalletEntity walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(address, wallettype);
        if (null == walletEntity) {
            walletEntity = new WalletEntity();
            WalletEntity nowWallet = WalletDBUtil.getInstent(mContext).getWalletInfo();
            walletEntity.setName(nowWallet.getName());
            walletEntity.setUserName(nowWallet.getUserName());
            walletEntity.setmPassword(nowWallet.getmPassword());
            walletEntity.setmPasswordHint(nowWallet.getmPasswordHint());
            walletEntity.setmAddress(getWalletAddr(nowWallet));
            walletEntity.setmPublicKey(nowWallet.getmPublicKey());
            walletEntity.setmPrivateKey(nowWallet.getmPrivateKey());
            walletEntity.setmMnemonic(nowWallet.getmMnemonic());
            walletEntity.setmKeystore(nowWallet.getmKeystore());
            walletEntity.setDefwallet(nowWallet.getDefwallet());
            walletEntity.setLevel(nowWallet.getLevel());
            walletEntity.setLogo(nowWallet.getLogo());
            walletEntity.setMBackup(nowWallet.getMBackup());
            walletEntity.setMMnemonicBackup(nowWallet.getMMnemonicBackup());
            walletEntity.setType(wallettype);
            WalletDBUtil.getInstent(mContext).insertWallet(walletEntity);
            RxBus.getInstance().post(new RxNotice(MSG_WALLET_NUM_CHANGE));
        }

        
        switchEthereumChain(id, address, chainId);
    }

    @Override
    public void ethRequest(String callbackId, String name, String data) {
        switch (name) {
            case "signMessage":
            case "signTypedMessage":
            case "signPersonalMessage":
                try {
                    data=new JSONObject(data).getString("data");
                    signPwd(callbackId, name, data);
                }catch (Exception e){

                }
                break;
            default:
                LogUtil.d("name" + name);
                break;
        }
    }


    @Override
    public void sgbRequest(SgbDappBean data) {
        switch (data.getMsgType()){
            case  "pub(accounts.list)":
                List<WalletEntity> walletEntityList=WalletDBUtil.getInstent(mContext).getWallName();
                List<SgbDappWalletBean> list=new ArrayList<>();
                for(int i=0;i<walletEntityList.size();i++){
                    WalletEntity walletEntity=walletEntityList.get(i);
                    list.add(new SgbDappWalletBean(getWalletAddr(walletEntity),walletEntity.getName()));
                }
                String res=new Gson().toJson(list);
                dappJsToWeb.sgbResult("'"+data.getMsgType()+"','"+res+"'");
                break;
            case "pub(bytes.sign)":
            case "pub(extrinsic.sign)":
                sgbTransctionListen.doSgbTransctionDapp(new Gson().toJson(data.getRequest()),data.getMsgType());
                break;
            default:
                LogUtil.d("Unknown message from dapp:"+data.getMsgType());
        }
    }

    private void getprice(BigDecimal gasprice) {
        if (gasCount == null) {
            return;
        }
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        feiyongStr = sum.divide(jinzhi).toPlainString();
    }

    private String toBase58(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String data = Owallet.tronHex2Addr(address);
        try {
            if (data.startsWith("{")) {
                TrxTransSignBean signBean = new Gson().fromJson(data, TrxTransSignBean.class);
                if (new Gson().toJson(signBean.getError()).length() > 4) {
                    return "";
                } else {
                    return signBean.getResult() + "";
                }
            } else {
                return data;
            }
        } catch (Exception e) {

        }
        return "";
    }

    private void signPwd(String callbackId, String type, String data) {
        walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfo();
        InputPwdDialog mDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if (!walletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(R.string.password_error2);
                    return;
                }
                String pricateKey = WalletUtil.getDecryptionKey(walletEntity.getmPrivateKey(), pwd);
                ECKeyPair pair = ECKeyPair.create(Numeric.toBigInt(pricateKey));
                String result = "";
                switch (type) {
                    case "signMessage":
                    case "signTypedMessage":
                        result = signEthereumMessage(Numeric.hexStringToByteArray(data), false, pricateKey);
                        break;
                    case "signPersonalMessage":
                        Sign.SignatureData sd = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(data), pair);
                        result = Numeric.toHexString(sd.getR()) + Numeric.toHexStringNoPrefix(sd.getS()) + Numeric.toHexStringNoPrefix(sd.getV());
                        break;
                    default:
                        break;
                }
                dappJsToWeb.ethTransactionResult(callbackId, new DappResult(true, result));
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private String signEthereumMessage(byte[] message, Boolean addPrefix, String pkey) {
        byte[] data = message;
        if (addPrefix) {
            String messagePrefix = "\u0019Ethereum Signed Message:\n";
            byte[] prefix = messagePrefix.concat(String.valueOf(message.length)).getBytes();
            byte[] result = new byte[prefix.length + message.length];
            data = Hash.keccak256(result);
        }
        PrivateKey privateKey = new PrivateKey(Numeric.hexStringToByteArray(pkey));
        byte[] signatureData = privateKey.sign(data, Curve.SECP256K1);
        byte signatureDatum = signatureData[signatureData.length - 1];
        signatureData[signatureData.length - 1] = new Byte((byte) (signatureDatum + 27));
        return Numeric.toHexString(signatureData);
    }



    private String signEthereumMessage2(byte[] message, Boolean addPrefix, String pkey) {
        byte[] data = Hash.keccak256(message);
        PrivateKey privateKey = new PrivateKey(Numeric.hexStringToByteArray(pkey));
        byte[] signatureData = privateKey.sign(data, Curve.SECP256K1);
        byte signatureDatum = signatureData[signatureData.length - 1];
        signatureData[signatureData.length - 1] = new Byte(signatureDatum);
        return Numeric.toHexString(signatureData);
    }
}
