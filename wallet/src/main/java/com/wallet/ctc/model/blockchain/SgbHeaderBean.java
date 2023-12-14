

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class SgbHeaderBean {

    private DigestBean digest;
    private String extrinsicsRoot;
    private String number;
    private String parentHash;
    private String stateRoot;

    public DigestBean getDigest() {
        return digest;
    }

    public void setDigest(DigestBean digest) {
        this.digest = digest;
    }

    public String getExtrinsicsRoot() {
        return extrinsicsRoot;
    }

    public void setExtrinsicsRoot(String extrinsicsRoot) {
        this.extrinsicsRoot = extrinsicsRoot;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String getStateRoot() {
        return stateRoot;
    }

    public void setStateRoot(String stateRoot) {
        this.stateRoot = stateRoot;
    }

    public static class DigestBean {
        private List<String> logs;

        public List<String> getLogs() {
            return logs;
        }

        public void setLogs(List<String> logs) {
            this.logs = logs;
        }
    }
}
