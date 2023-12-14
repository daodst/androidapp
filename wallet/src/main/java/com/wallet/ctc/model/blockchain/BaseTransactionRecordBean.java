

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class BaseTransactionRecordBean {

    private double per_page;
    private String current_page;
    private List<TransactionEthRecordBean> data;

    public double getPer_page() {
        return per_page;
    }

    public void setPer_page(double per_page) {
        this.per_page = per_page;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    public List<TransactionEthRecordBean> getData() {
        return data;
    }

    public void setData(List<TransactionEthRecordBean> data) {
        this.data = data;
    }

}
