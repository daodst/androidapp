

package com.wallet.ctc.ui.dapp.util;

import com.wallet.ctc.model.blockchain.SgbDappBean;



public interface DappFuncation {

    void setJsToWeb(DappJsToWeb dappJsToWeb);
    public DappJsToWeb getJsToWeb();

    
    void getTrxSign(String data);

    
    void sendEthTransaction(String reqid, String to, String value, String nonce, String gasLimit, String gasPrice, String data);


    
    void switchEthereumChain(String id, String address, String chainId);

    
    void addEthereumChain(String id, String address, String chainId, String rpcUrls, String symbol);

    
    void ethRequest(String reqid, String name, String data);

    
    void sgbRequest(SgbDappBean data);

}
