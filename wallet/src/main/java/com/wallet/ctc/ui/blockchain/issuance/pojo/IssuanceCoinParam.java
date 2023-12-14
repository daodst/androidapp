package com.wallet.ctc.ui.blockchain.issuance.pojo;

public class IssuanceCoinParam {
    public int page;
    public int page_size;
    public String owner;

    public IssuanceCoinParam() {
    }

    public IssuanceCoinParam(int page, int page_size, String owner) {
        this.page = page;
        this.page_size = page_size;
        this.owner = owner;
    }
}
