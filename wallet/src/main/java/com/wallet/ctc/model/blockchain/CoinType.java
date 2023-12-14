

package com.wallet.ctc.model.blockchain;

import androidx.annotation.IntDef;



@IntDef({CoinType.ETH,CoinType.TRON})
public @interface CoinType {
    int ETH = 0;
    int TRON= 3;
}
