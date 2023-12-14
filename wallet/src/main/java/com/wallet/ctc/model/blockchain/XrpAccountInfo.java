

package com.wallet.ctc.model.blockchain;


public class XrpAccountInfo {

    private String account;
    private Boolean strict;
    private String ledger_index;
    private Boolean queue;
    public  XrpAccountInfo(){

    }
    public  XrpAccountInfo(String account){
        this.account=account;
        this.strict=true;
        this.ledger_index="current";
        this.queue=true;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public String getLedger_index() {
        return ledger_index;
    }

    public void setLedger_index(String ledger_index) {
        this.ledger_index = ledger_index;
    }

    public Boolean getQueue() {
        return queue;
    }

    public void setQueue(Boolean queue) {
        this.queue = queue;
    }
}
