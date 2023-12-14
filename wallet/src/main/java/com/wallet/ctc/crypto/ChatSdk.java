package com.wallet.ctc.crypto;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.Constants;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeCompleteIdsBean;
import com.wallet.ctc.model.blockchain.ChainBridgeConfigBean;
import com.wallet.ctc.model.blockchain.ChainBridgeExConfirmBean;
import com.wallet.ctc.model.blockchain.ChainBridgeMainOrdersBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderDetailBean;
import com.wallet.ctc.model.blockchain.ChainBridgePreOrdersBean;
import com.wallet.ctc.model.blockchain.ChainBridgeServiceStatusBean;
import com.wallet.ctc.model.blockchain.ChatSdkExBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteInfoBean;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayNumberCountBean;
import com.wallet.ctc.model.blockchain.EvmosHttpBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosRpcQueryBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import chat_sdk.Chat_sdk;
import common.app.utils.AllUtils;
import common.app.utils.Base64Utils;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupList;


public class ChatSdk {
    private static final String TAG = "ChatSdk";

    
    public static void startSdk(String chainId) {
        Chat_sdk.startSdk(chainId);
    }

    
    public static void setNodeAddr(String nodeUrlPort) {
        Chat_sdk.setNodeAddr(nodeUrlPort);
    }

    
    public static void createClient(String homeUrl) {
        Chat_sdk.createClient(homeUrl);
    }


    
    public static ChatSdkExBean startChainBridgeTask(Context appContext) {
        
        File file = appContext.getDatabasePath("chainbridge");
        if (!file.exists()) {
            file.mkdirs();
        }
        String dbDirecotry = file.getPath();
        Log.i(TAG, "dbDirecotry=" + dbDirecotry);
        startCrossChain(dbDirecotry);
        setChainBridgeClientAddr(appContext, true);
        return startSync();
    }

    
    public static void setChainBridgeClientAddr(Context appContext, boolean initClient) {

        if (Constants.DEBUG) {
            
            Chat_sdk.setDebug();
        }

        
        int[] walletTypeArray = {WalletUtil.MCC_COIN, WalletUtil.BNB_COIN, WalletUtil.ETH_COIN};
        for (int walletType : walletTypeArray) {
            
            
            String rpcNode = WalletUtil.getSmartRpcUrl(walletType);
            String chainName = typeToChainName(walletType);
            if (initClient) {
                
                String crossChainContract = WalletUtil.getChainBridgeContract(walletType);
                
                AssertBean exAsset = WalletUtil.getUsdtAssert(walletType);
                
                initClient(rpcNode, crossChainContract, exAsset.getContract(), chainName);
            } else {
                setRpcNode(rpcNode, chainName);
            }
            
            List<WalletEntity> allWallets = WalletDBUtil.getInstent(appContext).getWalletList(walletType);
            if (allWallets != null && allWallets.size() > 0) {
                StringBuilder allAddrSb = new StringBuilder();
                for (WalletEntity wallet : allWallets) {
                    if (wallet.getLevel() != -1) {
                        String walletAddr = wallet.getDefaultAddress();
                        
                        if (TextUtils.isEmpty(allAddrSb)) {
                            
                            allAddrSb.append(walletAddr);
                        } else {
                            allAddrSb.append(",").append(walletAddr);
                        }
                    }
                }
                if (!TextUtils.isEmpty(allAddrSb)) {
                    addObserving(allAddrSb.toString(), chainName);
                }
            }

        }
    }

    public static String typeToChainName(int walletType) {
        if (walletType == WalletUtil.ETH_COIN) {
            return "eth";
        } else if (walletType == WalletUtil.MCC_COIN) {
            return "dst";
        } else if (walletType == WalletUtil.BNB_COIN) {
            return "bsc";
        }
        return "";
    }

    public static int chainNameToType(String chainName) {
        if ("eth".equalsIgnoreCase(chainName)) {
            return WalletUtil.ETH_COIN;
        } else if ("dst".equalsIgnoreCase(chainName)) {
            return WalletUtil.MCC_COIN;
        } else if ("bsc".equalsIgnoreCase(chainName)) {
            return WalletUtil.BNB_COIN;
        }
        return -1;
    }

    
    public static ChatSdkExBean startCrossChain(String dbPath) {
        Log.i(TAG, "startCrossChain(" + dbPath);
        byte[] result = Chat_sdk.startCrossChain(dbPath);
        return coverResult(result, ChatSdkExBean.class);
    }

    
    public static ChatSdkExBean initClient(String rpcNode, String crossChainContract, String assetContractAddr, String chainName) {
        Log.i(TAG, "initClient(" + rpcNode + ", " + crossChainContract + ", " + assetContractAddr + ", " + chainName);
        byte[] result = Chat_sdk.initClient(rpcNode, crossChainContract, assetContractAddr, chainName);
        return coverResult(result, ChatSdkExBean.class);
    }


    
    public static ChatSdkExBean addObserving(String address, String chainName) {
        Log.i(TAG, "addObserving(" + address + "," + chainName);
        byte[] result = Chat_sdk.addObserving(address, chainName);
        return coverResult(result, ChatSdkExBean.class);
    }


    
    public static ChatSdkExBean startSync() {
        Log.i(TAG, "startSync(");
        byte[] result = Chat_sdk.startSync();
        return coverResult(result, ChatSdkExBean.class);
    }

    
    public static ChatSdkExBean stopSync() {
        Log.i(TAG, "stopSync(");
        byte[] result = Chat_sdk.stopSync();
        return coverResult(result, ChatSdkExBean.class);
    }


    
    public static ChainBridgeCompleteIdsBean completeOrder() {
        Log.i(TAG, "completeOrder(");
        byte[] result = Chat_sdk.completeOrder();
        return coverResult(result, ChainBridgeCompleteIdsBean.class);
    }

    
    public static ChainBridgeServiceStatusBean serviceStatus() {
        Log.i(TAG, "serviceStatus(");
        byte[] result = Chat_sdk.serviceStatus();
        return coverResult(result, ChainBridgeServiceStatusBean.class);
    }


    
    public static byte[] addCrossWalletPir(String privateKeyStr) {
        Log.i(TAG, "addCrossWalletPir(" + privateKeyStr);
        return Chat_sdk.addCrossWalletPir(privateKeyStr);
    }

    
    private static byte[] setRpcNode(String rpcNode, String chainName) {
        return Chat_sdk.setRpcNode(rpcNode, chainName);
    }

    
    public static ChainBridgePreOrdersBean crossOrderPreview(String sellChain, String buyChain, String bigNumber) {
        Log.i(TAG, "crossOrderPreview(" + sellChain + ", " + buyChain + ", " + bigNumber);
        byte[] result = Chat_sdk.crossOrderPreview(sellChain, buyChain, bigNumber);
        return coverResult(result, ChainBridgePreOrdersBean.class);
    }


    
    public static ChainBridgeExConfirmBean crossConfirm(String sellChainName, String buyChainName, String depositAddr, String withdrawAddr, String bigAmount, String orderId) {
        Log.i(TAG, "crossConfirm(" + sellChainName + "," + buyChainName + ", " + depositAddr + ", " + withdrawAddr + ", " + bigAmount + ", " + orderId);
        byte[] result = Chat_sdk.crossConfirm(sellChainName, buyChainName, depositAddr, withdrawAddr, bigAmount, orderId);
        return coverResult(result, ChainBridgeExConfirmBean.class);
    }


    public static <T extends EvmosHttpBean> T coverResult(byte[] result, Class<T> tClass) {
        T data = null;
        try {
            String jsonStr = new String(result);
            Log.i(TAG, "coverResult() jsonStr=" + jsonStr);
            if (!TextUtils.isEmpty(jsonStr)) {
                data = new Gson().fromJson(jsonStr, tClass);
            } else {
                data = tClass.newInstance();
                data.setInfo("data is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                data = tClass.newInstance();
                data.setInfo(e + ":" + e.getMessage());
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }

    
    public static ChainBridgeMainOrdersBean mainOrderList(String address, long status, long page, long pageSize) {
        Log.i(TAG, "mainOrderList(" + address + ", " + status + ", " + page + ", " + pageSize);
        if (address == null) {
            address = "";
        }
        byte[] result = Chat_sdk.mainOrderList(address, status, page, pageSize);
        return coverResult(result, ChainBridgeMainOrdersBean.class);
    }




    
    public static ChainBridgeOrderDetailBean orderDetail(long mainOrderId) {
        Log.i(TAG, "orderDetail(" + mainOrderId);
        byte[] result = Chat_sdk.orderDetail(mainOrderId);
        return coverResult(result, ChainBridgeOrderDetailBean.class);
    }


    
    public static ChatSdkExBean dpositWithdraw(String orderKey) {
        Log.i(TAG, "dpositWithdraw(" + orderKey);
        byte[] result = Chat_sdk.dpositWithdraw(orderKey);
        return coverResult(result, ChatSdkExBean.class);
    }

    
    public static ChainBridgeConfigBean depositInfo(String sellChainName) {
        Log.i(TAG, "depositInfo(" + sellChainName);
        byte[] result = Chat_sdk.depositInfo(sellChainName);
        return coverResult(result, ChainBridgeConfigBean.class);
    }


    
    public static byte[] createAddress(String privateKey) {
        String privateKey2 = privateKey;
        if (privateKey2.startsWith("0x")) {
            privateKey2 = privateKey2.substring(2);
        }
        return Chat_sdk.createAddress(privateKey2);
    }

    
    public static byte[] setupCosmosWallet(String address, String publicKey, String privateKey) {
        String privateKey2 = privateKey;
        if (privateKey2.startsWith("0x")) {
            privateKey2 = privateKey2.substring(2);
        }
        JSONObject json = new JSONObject();
        try {
            json.put("address", address);
            json.put("publickey", publicKey);
            json.put("privatekey", privateKey2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = json.toString();
        
        return setupCosmosWallet(jsonStr.getBytes(StandardCharsets.UTF_8));
    }

    
    public static byte[] setupCosmosWallet(byte[] account) {
        return Chat_sdk.setupCosmosWallet(account);
    }

    
    public static String sign(String params) {
        if (TextUtils.isEmpty(params)) {
            return params;
        }
        byte[] message = params.getBytes(StandardCharsets.UTF_8);
        String signResult = new String(sign(message));
        return signResult;
    }


    
    public static byte[] sign(byte[] msg) {
        return Chat_sdk.sign(msg);
    }

    
    public static byte[] setSignTxBase(String accountNum, String accountSeq, String gaslimit, String gasAmount, String memo) {
        return Chat_sdk.setSignTxBase(accountNum, accountSeq, gaslimit, gasAmount, memo);
    }


    
    public static byte[] signTransfer(String toAddr, String symbol, String amount) {
        return Chat_sdk.signTransfer(toAddr, symbol, amount);
    }

    
    public static byte[] signResgister(String chatAddress, String nodeAddress, String amount, String symbol, String mobilePrefix) {
        
        return null;
    }

    
    public static byte[] SignBurnGetMedal(String fromAddress, String toAddress, String coinAmount, String coinSymbol, String gateway) {
        
        return null;
    }

    
    public static byte[] signBurnGetMobile(String mobilePrefix, String gateWayAddr, String chatAddr) {
        return Chat_sdk.signBurnGetMobile(mobilePrefix, gateWayAddr, chatAddr);
    }

    
    public static byte[] signChatDelegate(String delegateAddress, String valadateAddress, String amount, String symbol) {
        
        return null;
    }

    
    public static byte[] signChatUnDelegate(String delegateAddress, String coinAmount, String coinSymbol) {
        
        
        return null;
    }

    
    public static byte[] signChatWithdraw(String delegateAddress) {
        
        return null;
    }


    
    public static byte[] signChatSendGift(String fromAddress, String toAddress, String coinAmount, String coinSymbol) {
        
        return null;
    }

    
    public static byte[] signMobileTransfer(String toAddr, String mobile, String memo) {
        return Chat_sdk.signMobileTransfer(toAddr, mobile, memo);
    }


    
    public static byte[] signChangeNode(String nodeAddress, EvmosChatFeeBean chatFeeBean) {
        return signSetChatInfo(nodeAddress, chatFeeBean.data.chat_restricted_mode, chatFeeBean.data.getChatFeeAmount(),
                chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist, chatFeeBean.data.chat_whitelist,
                chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list, chatFeeBean.data.chat_white_enc_list);
    }

    
    public static byte[] signSetPrivacySetting(String feeMode, String bigAmount, EvmosChatFeeBean chatFeeBean) {
        return signSetChatInfo(chatFeeBean.data.node_address, feeMode,
                bigAmount, chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist,
                chatFeeBean.data.chat_whitelist, chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list,
                chatFeeBean.data.chat_white_enc_list);
    }


    
    public static byte[] signSaveFBWInfoGas(String encryBlackList, String encryWhiteList, String encryAddressBook,
                                            String encryBlackListGateWay, String encryWhiteListGateway, EvmosChatFeeBean chatFeeBean) {
        return signSetChatInfo(chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), encryBlackList,
                encryWhiteList, encryAddressBook, encryBlackListGateWay, encryWhiteListGateway);
    }

    
    public static byte[] signSaveBlackList(String encryBlackList, String encryBlackListGateWay, EvmosChatFeeBean chatFeeBean) {
        return signSetChatInfo(chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), encryBlackList,
                chatFeeBean.data.chat_whitelist, chatFeeBean.data.address_book, encryBlackListGateWay, chatFeeBean.data.chat_white_enc_list);
    }

    
    public static byte[] signSaveWhiteList(String encryWhiteList, String encryWhiteListGateway, EvmosChatFeeBean chatFeeBean) {
        return signSetChatInfo(chatFeeBean.data.node_address, chatFeeBean.data.chat_restricted_mode,
                chatFeeBean.data.getChatFeeAmount(), chatFeeBean.data.getChatFeeDenom(), chatFeeBean.data.chat_blacklist,
                encryWhiteList, chatFeeBean.data.address_book, chatFeeBean.data.chat_black_enc_list, encryWhiteListGateway);
    }


    
    public static byte[] signSetChatInfo(String nodeAddress, String chatRestrictedMode, String chatFeeAmount, String chatFeeCoinSymbol,
                                         String chatBlackArrayJson, String chatWhiteArrayJson, String addressBookArrayJson,
                                         String chatBlacklistEnc, String chatWhitelistEnc) {
        return Chat_sdk.signSetChatInfo(nodeAddress, chatRestrictedMode, chatFeeAmount, chatFeeCoinSymbol,
                chatBlackArrayJson, chatWhiteArrayJson, addressBookArrayJson,
                chatBlacklistEnc, chatWhitelistEnc, "");
    }

    
    public static byte[] signGovVote(int proposalId, int option) {
        return Chat_sdk.signGovVote(proposalId, option);
    }

    
    public static byte[] signDposDelagate(String valadateAddress, String amount, String symbol) {
        return Chat_sdk.signDposDelagate(valadateAddress, amount, symbol);
    }

    
    public static byte[] signDposWithdrawReward(String valadateAddress) {
        
        return Chat_sdk.signDposWithdrawReward();
    }

    
    public static byte[] signDposUnDelagate(String valadateAddress, String amount, String symbol, String indexNum) {
        return Chat_sdk.signDposUnDelagate(valadateAddress, amount, symbol, indexNum);
    }

    public static byte[] signUserTokenIssue(IssuanceParam param) {
        return Chat_sdk.signUserTokenIssue(param.name, param.symbol, param.pre_mint_amount, param.decimals, param.logo_url);
    }


    
    public static String getPubEcKeyFromPri(String privateKey) {
        try {
            return Chat_sdk.getPubEcKeyFromPri(privateKey.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static String getPubEcKeyFromPri(byte[] privateKeyByte) {
        try {
            return Chat_sdk.getPubEcKeyFromPri(privateKeyByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static String encodeUPri(String privateKey, String message) {
        if (TextUtils.isEmpty(privateKey) || TextUtils.isEmpty(message)) {
            return null;
        }
        String ecdsaPublicKey = getPubEcKeyFromPri(privateKey.getBytes());
        byte[] encryBytes = encode(ecdsaPublicKey, message.getBytes());
        return Base64Utils.encode(encryBytes);
    }


    
    public static String encode(String ecdsaPublicKey, String message) {
        if (TextUtils.isEmpty(ecdsaPublicKey) || TextUtils.isEmpty(message)) {
            return null;
        }
        byte[] encryBytes = encode(ecdsaPublicKey, message.getBytes());
        return Base64Utils.encode(encryBytes);
    }

    
    public static byte[] encode(String ecdsaPublicKey, byte[] message) {
        try {
            return Chat_sdk.encode(ecdsaPublicKey, message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static String decode(String privateKey, String encryedMsg) {
        if (TextUtils.isEmpty(privateKey) || TextUtils.isEmpty(encryedMsg)) {
            return null;
        }
        byte[] encryBytes = Base64Utils.decode(encryedMsg);
        try {
            byte[] msgBtyes = decode(privateKey, encryBytes);
            return new String(msgBtyes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static byte[] decode(String privateKey, byte[] encryedByte) {
        try {
            String privateKeyBase64 = Base64Utils.encode(privateKey.getBytes());
            return Chat_sdk.decode(privateKeyBase64, encryedByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    
    public static EvmosOneBalanceBean getOneBalance(String address, String coinName) {
        EvmosOneBalanceBean balance = new EvmosOneBalanceBean();
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(coinName)) {
            balance.setInfo("illegal address or coinName");
            return balance;
        }
        try {
            byte[] result = queryBalance(address, coinName);
            if (null != result) {
                String jsonStr = new String(result);
                balance = new Gson().fromJson(jsonStr, EvmosOneBalanceBean.class);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "queryBalance(" + address + ", " + coinName + ", jsonStr=" + jsonStr);
                }
            } else {
                balance.setInfo("query balance null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            balance.setInfo("getBalance error " + e.getMessage());
        }

        return balance;
    }


    
    public static String queryBigBalance(String address, String coinName) {
        EvmosOneBalanceBean data = getOneBalance(address, coinName);
        if (null != data && data.isSuccess()) {
            return data.data;
        } else {
            return "0";
        }
    }


    
    public static byte[] queryBalance(String address, String coinName) {
        return Chat_sdk.queryBalance(address, coinName);
    }

    
    public static byte[] queryBalance(String address) {
        return Chat_sdk.queryBalances(address);
    }


    
    public static String dstAddr2EthAddr(String str) {
        return Chat_sdk.dstAddr2EthAddr(str);
    }

    
    public static String ethAddr2DstAddr(String str) {
        return Chat_sdk.ethAddr2DstAddr(str);
    }

    
    public static EvmosHxResultBean queryTxResultByHash(String rpcHost, String txHash) {
        Log.i("queryTx", "rpcHost:" + rpcHost + ", " + txHash + ", " + Thread.currentThread());
        if (!TextUtils.isEmpty(rpcHost)) {
            
            setNodeAddr(rpcHost);
            createClient(AllUtils.getHomeUrl(rpcHost));
        }

        EvmosHxResultBean data = new EvmosHxResultBean();
        if (TextUtils.isEmpty(txHash)) {
            Log.e("queryTx", "illegal txHash is null");
            data.setInfo("illegal txHash is null");
            return data;
        }
        try {
            byte[] result = queryTx(txHash);
            if (null != result) {
                String jsonStr = new String(result);
                Log.i("queryTx", jsonStr);
                data = new Gson().fromJson(jsonStr, EvmosHxResultBean.class);
            } else {
                data.setInfo("query result null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("queryTx", "illegal txHash exception " + e.getMessage());
            data.setInfo("queryTx error " + e.getMessage());
        }

        return data;
    }

    
    public static byte[] queryTx(String txHash) {
        return Chat_sdk.queryTx(txHash);
    }

    
    public static byte[] signCrossChainOut(String toAddress, String coinAmount, String coinSymbol, String chainType, String remark) {
        return Chat_sdk.signCrossChainOut(toAddress, coinAmount, coinSymbol, chainType, remark);
    }

    
    public static byte[] signProposalParam(String title, String description, String change, String bigDepositStr) {
        return Chat_sdk.signProposalParam(title, description, change, bigDepositStr);
    }

    
    public static byte[] signProposalCommunity(String title, String description, String recipient, String bigAmount, String bigDepositStr) {
        return Chat_sdk.signProposalCommunity(title, description, recipient, bigAmount, bigDepositStr);
    }

    
    public static byte[] signProposalUpgrade(String title, String description, String name, String info, String bigDepositStr, long height) {
        return Chat_sdk.signProposalUpgrade(title, description, name, info, bigDepositStr, height);
    }


    
    public static EvmosSignResult convertSignData(byte[] signByte) {
        if (signByte == null) {
            return new EvmosSignResult();
        }
        String jsonSignResult = new String(signByte);
        if (TextUtils.isEmpty(jsonSignResult)) {
            return new EvmosSignResult();
        }
        try {
            EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            EvmosSignResult result = new EvmosSignResult();
            result.Info = e.getMessage();
            return result;
        }
    }

    
    public static void resetWalletGasInfo(EvmosSeqGasBean evmosSeqGasBean, WalletEntity wallet, String pwd, String memo) throws Exception {
        if (null == evmosSeqGasBean || evmosSeqGasBean.seqAccount == null || null == wallet || TextUtils.isEmpty(pwd)) {
            throw new Exception("resetWalletGasInfo error acount seq or gas or wallet illegal!!");
        }

        
        String publickey = new String(wallet.getmPublicKey());
        String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
        String address = wallet.getAllAddress();
        if (TextUtils.isEmpty(publickey) || TextUtils.isEmpty(privateKey)) {
            throw new Exception("resetWalletGasInfo key data error: publickey=" + publickey + ", \nprivateKey=" + privateKey + ", \naddress=" + address);
        }
        ChatSdk.setupCosmosWallet(address, publickey, privateKey);

        

        String gasAmount2 = evmosSeqGasBean.gas != null ? evmosSeqGasBean.gas.getGasAmount() : "";
        if (TextUtils.isEmpty(gasAmount2)) {
            gasAmount2 = "100000000000000000";
        }
        String gasLimit2 = evmosSeqGasBean.gas != null ? evmosSeqGasBean.gas.getGasLimit() : "";
        if (TextUtils.isEmpty(gasLimit2)) {
            gasLimit2 = "2000000";
        }
        EvmosSeqAcountBean.Data seqAccountBean = evmosSeqGasBean.seqAccount;
        String accountNum = seqAccountBean.account_number + "";
        String accountSeq = seqAccountBean.sequence + "";
        if (memo == null) {
            memo = "";
        }
        ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, memo);
    }

    
    public static EvmosRpcQueryBean httpRpcQuery(String path, String paramsStr) {
        Log.i(TAG, "httpRpcQuery(" + path + ", " + paramsStr + "---" + Thread.currentThread());
        if (paramsStr == null) {
            paramsStr = "";
        }
        byte[] params = paramsStr.getBytes(StandardCharsets.UTF_8);
        byte[] result = null;
        try {
            result = Chat_sdk.rpcQuery(path, params);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (null != result && result.length > 0) {
            String resultJson = new String(result);
            Log.i(TAG, "httpRpcQuery()result=" + resultJson);
            EvmosRpcQueryBean dataBean = new Gson().fromJson(resultJson, EvmosRpcQueryBean.class);
            return dataBean;
        } else {
            Log.i(TAG, "httpRpcQuery()result=null");
            return null;
        }
    }


    
    public static EvmosMyGroupList httpGetMyDeviceGroups(String account) {
        if (TextUtils.isEmpty(account)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/person_cluster_info", jsonObject.toString());
        if (null != dataBean) {
            EvmosMyGroupList data = new EvmosMyGroupList();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            try {
                String jsonStr = dataBean.data;
                if (!TextUtils.isEmpty(jsonStr)) {
                    EvmosMyGroupList.Data childData = new Gson().fromJson(jsonStr, EvmosMyGroupList.Data.class);
                    data.data = childData;
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                data.setStatus(0);
                data.setInfo(ex + ":" + ex.getMessage());
            }
            return data;
        }
        return null;
    }

    
    public static EvmosGroupDataBean httpGetDeviceGroupData(String deviceGroupId) {
        if (TextUtils.isEmpty(deviceGroupId)) {
            return null;
        }
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/cluster_info", deviceGroupId);
        if (null != dataBean) {
            EvmosGroupDataBean data = new EvmosGroupDataBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    EvmosGroupDataBean.Data childData = new Gson().fromJson(jsonStr, EvmosGroupDataBean.Data.class);
                    data.data = childData;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }
        return null;
    }

    
    public static EvmosMyGroupDataBean httpGetSomeGroupData(String account, String deviceGroupId) {
        if (TextUtils.isEmpty(deviceGroupId)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from_address", account);
            jsonObject.put("cluster_id", deviceGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_cluster_person_info", jsonObject.toString());
        if (null != dataBean) {
            EvmosMyGroupDataBean data = new EvmosMyGroupDataBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    EvmosMyGroupDataBean.Data childData = new Gson().fromJson(jsonStr, EvmosMyGroupDataBean.Data.class);
                    data.data = childData;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }
        return null;
    }

    
    public static EvmosDaoParams httpGetDaoParams() {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_dao_params", null);
        if (null != dataBean) {
            EvmosDaoParams data = new EvmosDaoParams();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    EvmosDaoParams.Data childData = new Gson().fromJson(jsonStr, EvmosDaoParams.Data.class);
                    data.data = childData;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }


    
    public static EvmosDvmListBean httpGetMyDvmList(String account) {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_dvm_list", account);
        if (null != dataBean) {
            EvmosDvmListBean data = new EvmosDvmListBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    List<EvmosDvmListBean.Data> childData = new Gson().fromJson(jsonStr, new TypeToken<List<EvmosDvmListBean.Data>>() {
                    }.getType());
                    data.data = childData;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }

    
    public static EvmosGatewayNumberCountBean httpGetGatewayNumberCount(String gatewayAddress, String amount) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gateway_address", gatewayAddress);
            jsonObject.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EvmosRpcQueryBean dataBean = httpRpcQuery("gateway/gateway_unbond_number_count", jsonObject.toString());
        if (null != dataBean) {
            EvmosGatewayNumberCountBean data = new EvmosGatewayNumberCountBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    data.data = Integer.parseInt(jsonStr);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }


    
    public static byte[] signDaoCreateCluster(String gateAddress, String clusterId, String chatAddress,
                                              String clusterName, String deviceRatio, String salaryRatio, String burnAmount, String freezeAmount,
                                              List<DeviceGroupMember> members) {
        String membersJson = "[]";
        if (null != members && members.size() > 0) {
            membersJson = new Gson().toJson(members);
        }
        return Chat_sdk.signDaoCreateCluster(gateAddress, clusterId, chatAddress,
                clusterName, deviceRatio, salaryRatio, burnAmount, freezeAmount, membersJson);
    }


    
    public static byte[] signDaoClusterAddMembers(String clusterId, List<DeviceGroupMember> members) {
        if (TextUtils.isEmpty(clusterId) || null == members) {
            Log.e(TAG, "signDaoClusterAddMembers(" + clusterId + ", " + members);
            return null;
        }
        String membersJson = new Gson().toJson(members);
        return Chat_sdk.signDaoClusterAddMembers(clusterId, membersJson);
    }


    
    public static byte[] signDaoDeleteMembers(String clusterId, String members) {
        if (TextUtils.isEmpty(clusterId) || TextUtils.isEmpty(members)) {
            Log.e(TAG, "signDaoDeleteMembers(" + clusterId + ", " + members);
            return null;
        }
        return Chat_sdk.signDaoDeleteMembers(clusterId, members);
    }


    
    public static byte[] signDaoClusterChangeName(String clusterId, String clusterName) {
        if (TextUtils.isEmpty(clusterId) || TextUtils.isEmpty(clusterName)) {
            Log.e(TAG, "signDaoClusterChangeName(" + clusterId + ", " + clusterName);
            return null;
        }
        return Chat_sdk.signDaoClusterChangeName(clusterId, clusterName);
    }


    
    public static byte[] signDaoClusterMemberExit(String clusterId) {
        if (TextUtils.isEmpty(clusterId)) {
            Log.e(TAG, "signDaoClusterMemberExit(" + clusterId);
            return null;
        }
        return Chat_sdk.signDaoClusterMemberExit(clusterId);
    }

    
    public static byte[] signDaoBurnToPower(String to, String clusterId, String gatewayAddress,
                                            String chatAddress, String bigBurnAmoumt, String useFreezeAmount) {
        return Chat_sdk.signDaoBurnToPower(to, clusterId, gatewayAddress, chatAddress, bigBurnAmoumt, useFreezeAmount);
    }


    
    public static byte[] signDaoClusterChangeDeviceRatio(String clusterId, String deviceRatio) {
        return Chat_sdk.signDaoClusterChangeDeviceRatio(clusterId, deviceRatio);
    }

    
    public static byte[] signDaoClusterChangeSalaryRatio(String clusterId, String salaryRatio) {
        return Chat_sdk.signDaoClusterChangeSalaryRatio(clusterId, salaryRatio);
    }

    
    public static byte[] signDaoClusterChangeId(String clusterId, String newClusterId) {
        return Chat_sdk.signDaoClusterChangeId(clusterId, newClusterId);
    }

    
    public static byte[] signDaoWithdrawBurnReward(String clusterId) {
        return Chat_sdk.signDaoWithdrawBurnReward(clusterId);
    }

    
    public static byte[] signDaoWithdrawDeviceReward(String clusterId) {
        return Chat_sdk.signDaoWithdrawDeviceReward(clusterId);
    }

    
    public static byte[] signDaoWithdrawOwnerReward(String clusterId) {
        return Chat_sdk.signDaoWithdrawOwnerReward(clusterId);
    }

    
    public static byte[] signDaoThawFrozenPower(String clusterId, String gatewayAddr, String chatAddr, String thawAmount) {
        return Chat_sdk.signDaoThawFrozenPower(clusterId, gatewayAddr, chatAddr, thawAmount);
    }


    
    public static byte[] signGroupSubmitTransferProposal(String clusterId, String amount, String toAddress, String title, String description) {
        return Chat_sdk.groupSubmitTransferProposal(clusterId, amount, toAddress, title, description);
    }

    
    public static byte[] signGroupDaoAuthorizationProposal(String clusterId, String endBlock, String toAddress, String title, String description) {
        return Chat_sdk.groupSubmitApprovePowerProposal(clusterId, endBlock, toAddress, title, description);
    }

    
    public static EvmosClusterVoteBean httpGetClusterVoteList(String clusterId) {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_cluster_personals", clusterId);
        if (null != dataBean) {
            EvmosClusterVoteBean data = new EvmosClusterVoteBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    TypeToken<List<EvmosClusterVoteBean.Data>> tokenType = new TypeToken<>() {
                    };
                    data.data = new Gson().fromJson(jsonStr, tokenType.getType());
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }

    
    public static EvmosClusterVoteDetailBean httpGetClusterVoteDetail(int proposalId) {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_cluster_personal_info", proposalId + "");
        if (null != dataBean) {
            EvmosClusterVoteDetailBean data = new EvmosClusterVoteDetailBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    data.data = new Gson().fromJson(jsonStr, EvmosClusterVoteDetailBean.Data.class);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }

    public static EvmosClusterVoteInfoBean httpGetVoteDetail(int proposalId) {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_proposal_tally_result", proposalId + "");
        if (null != dataBean) {
            EvmosClusterVoteInfoBean data = new EvmosClusterVoteInfoBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    data.data = new Gson().fromJson(jsonStr, EvmosClusterVoteInfoBean.Data.class);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }

        return null;
    }

    
    public static EvmosClusterPersonVoteBean httpGetClusterPersonVote(int proposalId, String voter) {
        JSONObject json = new JSONObject();
        try {
            json.put("proposal_id", proposalId);
            json.put("voter", voter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_cluster_personal_voter", json.toString());
        if (null != dataBean) {
            EvmosClusterPersonVoteBean data = new EvmosClusterPersonVoteBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }
        return null;
    }

    
    public static EvmosClusterPersonVoteBean httpGetClusterAllPersonVote(int proposalId) {
        EvmosRpcQueryBean dataBean = httpRpcQuery("dao/query_cluster_personal_voters", proposalId + "");
        if (null != dataBean) {
            EvmosClusterPersonVoteBean data = new EvmosClusterPersonVoteBean();
            data.setStatus(dataBean.getStatus());
            data.setInfo(dataBean.getSrcInfo());
            String jsonStr = dataBean.data;
            if (!TextUtils.isEmpty(jsonStr)) {
                try {
                    data.data = new Gson().fromJson(jsonStr, EvmosClusterPersonVoteBean.Data.class);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    data.setStatus(0);
                    data.setInfo(ex + ":" + ex.getMessage());
                }
            }
            return data;
        }
        return null;
    }

    
    public static byte[] signGroupProposalVote(int proposalId, String option) {
        return Chat_sdk.groupProposalVote(proposalId + "", option);
    }

    
    public static byte[] signPersonDvmApprove(String approveAddress, String clusterId, String approveEndBlock) {
        return Chat_sdk.signPersonDvmApprove(approveAddress, clusterId, approveEndBlock);
    }


    
    public static byte[] groupSubmitChangeDeviceRatioProposal(String clusterId, String deviceRatio, String title, String description) {
        return Chat_sdk.groupSubmitChangeDeviceRatioProposal(clusterId, deviceRatio, title, description);
    }


    
    public static byte[] groupSubmitChangeSalaryRatioProposal(String clusterId, String salaryRatio, String title, String description) {
        return Chat_sdk.groupSubmitChangeSalaryRatioProposal(clusterId, salaryRatio, title, description);
    }


}
