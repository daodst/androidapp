

package com.wallet.ctc.crypto;

import com.wallet.ctc.model.blockchain.TransctionInitBean;
import com.wallet.ctc.model.blockchain.TransferBean;



public interface Transction {
    void init(TransctionInitBean initBean);
    void doTrans(TransferBean mBean,String pwd);
}
