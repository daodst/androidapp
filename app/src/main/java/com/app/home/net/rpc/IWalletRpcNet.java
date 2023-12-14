package com.app.home.net.rpc;

import com.app.home.pojo.PayVoteStep1Info;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;

import common.app.utils.SpUtil;
import io.reactivex.Observable;

public interface IWalletRpcNet {


    
    default String getEvmosRpcUrl() {
        return SpUtil.getDefNode(4);
    }

    default String getNodeInfoUrl() {
        return SpUtil.getNodeInfoUrl();
    }

    Observable<EvmosTransferResultBean> getRPCVoteInfo(RPCVoteParam<?> param, String memo, WalletEntity walletEntity, String pwd, WalletRpcImpl.IDoSdk doSdk);

    Observable<RPCVoteInfo> getGas(String walletAddress, RPCVoteParam<?> param);


    Observable<EvmosTransferResultBean> sign(String address, WalletRpcImpl.IDoSdk iDoSdk,
                                             EvmosSeqAcountBean.Data seqAccountBean,
                                             String memo, String gasAmount, String gasLimit, WalletEntity wallet, String pwd);


    Observable<PayVoteStep1Info> getPayVoteStep1Info();
}
