

package com.wallet.ctc.model.blockchain;



public class EthCallBean {
    private String to;
    private String data;

    public EthCallBean(){

    }
    public EthCallBean(String to,String data){
        this.to=to;
        this.data=data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "to='" + to + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
