

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class TrxDappBean {


    private ResultBean result;
    private Integer energy_used;
    private List<String> constant_result;
    private TrxTransactionPushBean transaction;
    public static class ResultBean {
        private Boolean result;
        public ResultBean(boolean result){
            this.result=result;
        }

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public Integer getEnergy_used() {
        return energy_used;
    }

    public void setEnergy_used(Integer energy_used) {
        this.energy_used = energy_used;
    }

    public List<String> getConstant_result() {
        return constant_result;
    }

    public void setConstant_result(List<String> constant_result) {
        this.constant_result = constant_result;
    }

    public TrxTransactionPushBean getTransaction() {
        return transaction;
    }

    public void setTransaction(TrxTransactionPushBean transaction) {
        this.transaction = transaction;
    }
}
