

package com.wallet.ctc.model.blockchain;



public class TrxPayInfoBean {


    private String contract_address;
    private String owner_address;
    private String function_selector;
    private String parameter;
    private Integer call_value;
    private Integer fee_limit;
    private long frozen_duration;
    private long frozen_balance;
    private String resource;
    private String receiver_address;

    public String getContract_address() {
        return contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }

    public String getOwner_address() {
        return owner_address;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }

    public String getFunction_selector() {
        return function_selector;
    }

    public void setFunction_selector(String function_selector) {
        this.function_selector = function_selector;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Integer getCall_value() {
        return call_value;
    }

    public void setCall_value(Integer call_value) {
        this.call_value = call_value;
    }

    public Integer getFee_limit() {
        return fee_limit;
    }

    public void setFee_limit(Integer fee_limit) {
        this.fee_limit = fee_limit;
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
