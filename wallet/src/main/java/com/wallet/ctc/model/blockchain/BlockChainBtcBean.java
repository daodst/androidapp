

package com.wallet.ctc.model.blockchain;


public class BlockChainBtcBean {


    private Integer page;
    private Integer totalPages;
    private Integer itemsOnPage;
    private String address;
    private String balance;
    private String totalReceived;
    private String totalSent;
    private String unconfirmedBalance;
    private Integer unconfirmedTxs;
    private Integer txs;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getItemsOnPage() {
        return itemsOnPage;
    }

    public void setItemsOnPage(Integer itemsOnPage) {
        this.itemsOnPage = itemsOnPage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(String totalReceived) {
        this.totalReceived = totalReceived;
    }

    public String getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(String totalSent) {
        this.totalSent = totalSent;
    }

    public String getUnconfirmedBalance() {
        return unconfirmedBalance;
    }

    public void setUnconfirmedBalance(String unconfirmedBalance) {
        this.unconfirmedBalance = unconfirmedBalance;
    }

    public Integer getUnconfirmedTxs() {
        return unconfirmedTxs;
    }

    public void setUnconfirmedTxs(Integer unconfirmedTxs) {
        this.unconfirmedTxs = unconfirmedTxs;
    }

    public Integer getTxs() {
        return txs;
    }

    public void setTxs(Integer txs) {
        this.txs = txs;
    }
}
