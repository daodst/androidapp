package com.wallet.ctc.ui.blockchain.issuance.net.rpc;


import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinPageInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteParam;

import common.app.utils.SpUtil;
import io.reactivex.Observable;

public interface ISWalletRpcNet {

    int LIMIT = 15;
    
    default String getEvmosRpcUrl() {
        return SpUtil.getDefNode(4);
    }

    Observable<EvmosTransferResultBean> getRPCVoteInfo(WRPCVoteParam<?> param, String memo, WalletEntity walletEntity, String pwd, SWalletRpcImpl.IDoSdk doSdk);

    Observable<WRPCVoteInfo> getGas(String walletAddress, WRPCVoteParam<?> param);


    Observable<EvmosTransferResultBean> sign(String address, SWalletRpcImpl.IDoSdk iDoSdk,
                                     EvmosSeqAcountBean.Data seqAccountBean,
                                     String memo, String gasAmount, String gasLimit, WalletEntity wallet, String pwd);

    Observable<IssuanceCoinPageInfo> getIssuanceCoinPageInfo(IssuanceCoinPageInfo page, String owner);

}
