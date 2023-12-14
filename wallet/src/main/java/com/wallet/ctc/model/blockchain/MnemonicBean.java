

package com.wallet.ctc.model.blockchain;



public class MnemonicBean {
    String key;
    int choose;

    public MnemonicBean(String key){
        this.key=key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getChoose() {
        return choose;
    }

    public void setChoose(int choose) {
        this.choose = choose;
    }
}
