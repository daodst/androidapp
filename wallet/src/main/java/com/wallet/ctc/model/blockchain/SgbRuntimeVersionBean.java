

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class SgbRuntimeVersionBean {

    private List<List<String>> apis;
    private Integer authoringVersion;
    private String implName;
    private Integer implVersion;
    private String specName;
    private Integer specVersion;
    private Integer transactionVersion;

    public List<List<String>> getApis() {
        return apis;
    }

    public void setApis(List<List<String>> apis) {
        this.apis = apis;
    }

    public Integer getAuthoringVersion() {
        return authoringVersion;
    }

    public void setAuthoringVersion(Integer authoringVersion) {
        this.authoringVersion = authoringVersion;
    }

    public String getImplName() {
        return implName;
    }

    public void setImplName(String implName) {
        this.implName = implName;
    }

    public Integer getImplVersion() {
        return implVersion;
    }

    public void setImplVersion(Integer implVersion) {
        this.implVersion = implVersion;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(Integer specVersion) {
        this.specVersion = specVersion;
    }

    public Integer getTransactionVersion() {
        return transactionVersion;
    }

    public void setTransactionVersion(Integer transactionVersion) {
        this.transactionVersion = transactionVersion;
    }
}
