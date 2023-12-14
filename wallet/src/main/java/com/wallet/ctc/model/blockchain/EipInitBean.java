

package com.wallet.ctc.model.blockchain;



public class EipInitBean {
    
    private Integer chain_id;
    private String max_fee_per_gas;
    private String max_priority_fee_per_gas;

    public Integer getChain_id() {
        return chain_id;
    }

    public void setChain_id(Integer chain_id) {
        this.chain_id = chain_id;
    }

    public String getMax_fee_per_gas() {
        return max_fee_per_gas;
    }

    public void setMax_fee_per_gas(String max_fee_per_gas) {
        this.max_fee_per_gas = max_fee_per_gas;
    }

    public String getMax_priority_fee_per_gas() {
        return max_priority_fee_per_gas;
    }

    public void setMax_priority_fee_per_gas(String max_priority_fee_per_gas) {
        this.max_priority_fee_per_gas = max_priority_fee_per_gas;
    }
}
