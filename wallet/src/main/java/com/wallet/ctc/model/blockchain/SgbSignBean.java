

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class SgbSignBean {

    private String specVersion;
    private String transactionVersion;
    private String address;
    private String blockHash;
    private String blockNumber;
    private String era;
    private String genesisHash;
    private String method;
    private String nonce;
    private List<String> signedExtensions;
    private String tip;
    private Integer version;

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getTransactionVersion() {
        return transactionVersion;
    }

    public void setTransactionVersion(String transactionVersion) {
        this.transactionVersion = transactionVersion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getGenesisHash() {
        return genesisHash;
    }

    public void setGenesisHash(String genesisHash) {
        this.genesisHash = genesisHash;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public List<String> getSignedExtensions() {
        return signedExtensions;
    }

    public void setSignedExtensions(List<String> signedExtensions) {
        this.signedExtensions = signedExtensions;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
