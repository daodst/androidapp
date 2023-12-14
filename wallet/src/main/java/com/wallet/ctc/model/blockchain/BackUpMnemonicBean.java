

package com.wallet.ctc.model.blockchain;



public class BackUpMnemonicBean {
    private String text;
    private boolean choose;

    public BackUpMnemonicBean(){

    }
    public BackUpMnemonicBean(String data){
        this.text=data;
        this.choose=false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
