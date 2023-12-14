

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class DotBalanceBean {

    

    private AtBean at;
    

    private String nonce;
    private String tokenSymbol;
    private String free;
    private String reserved;
    private String miscFrozen;
    private String feeFrozen;
    private List<Object> locks;

    public AtBean getAt() {
        return at;
    }

    public void setAt(AtBean at) {
        this.at = at;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getMiscFrozen() {
        return miscFrozen;
    }

    public void setMiscFrozen(String miscFrozen) {
        this.miscFrozen = miscFrozen;
    }

    public String getFeeFrozen() {
        return feeFrozen;
    }

    public void setFeeFrozen(String feeFrozen) {
        this.feeFrozen = feeFrozen;
    }

    public List<Object> getLocks() {
        return locks;
    }

    public void setLocks(List<Object> locks) {
        this.locks = locks;
    }

    public static class AtBean {
        private String hash;
        private String height;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }
}
