

package com.wallet.ctc.model.blockchain;


public class EvmosSignResult {
    public int Status;
    public String Info;
    public String Data;

    public boolean isSuccess() {
        return Status == 1;
    }

    public String getInfo() {
        return Info;
    }
}
