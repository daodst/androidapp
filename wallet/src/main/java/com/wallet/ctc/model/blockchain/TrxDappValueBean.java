

package com.wallet.ctc.model.blockchain;



public class TrxDappValueBean {
    private String data;
    private String owner_address;
    private String contract_address;
    private String to_address;
    private long amount;
    private long frozen_duration;
    private long frozen_balance;
    private String resource;
    private String receiver_address;

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOwner_address() {
        return owner_address;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }

    public String getContract_address() {
        return contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }

    public long getFrozen_duration() {
        return frozen_duration;
    }

    public void setFrozen_duration(long frozen_duration) {
        this.frozen_duration = frozen_duration;
    }

    public long getFrozen_balance() {
        return frozen_balance;
    }

    public void setFrozen_balance(long frozen_balance) {
        this.frozen_balance = frozen_balance;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getReceiver_address() {
        return receiver_address;
    }

    public void setReceiver_address(String receiver_address) {
        this.receiver_address = receiver_address;
    }
}
