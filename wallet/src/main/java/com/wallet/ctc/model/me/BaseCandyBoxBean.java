

package com.wallet.ctc.model.me;

import java.util.List;



public class BaseCandyBoxBean {

    private int total;
    private int per_page;
    private int current_page;
    private List<CandyBoxBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public List<CandyBoxBean> getData() {
        return data;
    }

    public void setData(List<CandyBoxBean> data) {
        this.data = data;
    }

}
