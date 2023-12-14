package com.app.home.pojo.rpc;

import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;

public class RPCVoteParam<T> {

    
    public String msg_type;
    
    public T msgs;
    
    public EvmosSeqAcountBean.Data seq_detail;


    
    public static final String TYPE_MSGDELEGATE = "cosmos-sdk/MsgDelegate";
    
    public static final String TYPE_MSGUNDELEGATE = "gateway/MsgGatewayUndelegate";
    
    public static final String TYPE_MSGWITHDRAWDELEGATIONREWARD = "cosmos-sdk/MsgWithdrawDelegationReward";
    
    public static final String TYPE_MSGVOTE = "cosmos-sdk/MsgVote";

    @Override
    public String toString() {
        return "RPCVoteParam{" +
                "msg_type='" + msg_type + '\'' +
                ", msgs=" + msgs +
                ", seq_detail=" + seq_detail +
                '}';
    }
}
