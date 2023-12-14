package com.wallet.ctc.ui.blockchain.issuance.pojo;

import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;

public class WRPCVoteParam<T> {

    
    public String msg_type;
    
    public T msgs;
    
    public EvmosSeqAcountBean.Data seq_detail;

    
    public static final String TYPE_APPTOKENISSUE = "contract/AppTokenIssue";
}
