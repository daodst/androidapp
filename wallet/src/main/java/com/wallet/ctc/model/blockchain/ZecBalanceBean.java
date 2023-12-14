

package com.wallet.ctc.model.blockchain;


public class ZecBalanceBean {

    

    private int page;
    private int totalPages;
    private int itemsOnPage;
    private String address;
    private String balance;
    private String totalReceived;
    private String totalSent;
    private String unconfirmedBalance;
    private int unconfirmedTxs;
    private int txs;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getItemsOnPage() {
        return itemsOnPage;
    }

    public void setItemsOnPage(int itemsOnPage) {
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

    public int getUnconfirmedTxs() {
        return unconfirmedTxs;
    }

    public void setUnconfirmedTxs(int unconfirmedTxs) {
        this.unconfirmedTxs = unconfirmedTxs;
    }

    public int getTxs() {
        return txs;
    }

    public void setTxs(int txs) {
        this.txs = txs;
    }
}
